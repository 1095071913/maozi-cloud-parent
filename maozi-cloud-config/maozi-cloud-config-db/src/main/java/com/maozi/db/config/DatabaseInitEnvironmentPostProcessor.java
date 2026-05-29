package com.maozi.db.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库自动初始化处理器
 * <p>
 * 在 Spring 容器启动之前执行，检查目标数据库是否存在，
 * 不存在则自动创建（字符集 utf8mb4，排序规则 utf8mb4_general_ci），
 * 确保 Flyway 数据库迁移能够正常执行。
 * </p>
 * <p>
 * 注册方式：通过 {@code META-INF/spring/org.springframework.boot.env.EnvironmentPostProcessor.imports} 文件注册。
 * </p>
 *
 * @author maozi
 */
@Slf4j
public class DatabaseInitEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /** 数据源 URL 配置键 */
    private static final String DATASOURCE_URL = "spring.datasource.url";

    /** 数据源用户名配置键 */
    private static final String DATASOURCE_USERNAME = "spring.datasource.username";

    /** 数据源密码配置键 */
    private static final String DATASOURCE_PASSWORD = "spring.datasource.password";

    /** JDBC MySQL 前缀 */
    private static final String JDBC_MYSQL_PREFIX = "jdbc:mysql://";

    /**
     * 在环境准备阶段执行数据库初始化
     * <p>
     * 从配置中读取数据源信息，解析数据库名称，
     * 连接 MySQL 服务器检查数据库是否存在，不存在则创建。
     * </p>
     *
     * @param environment  Spring 环境配置
     * @param application  Spring 应用实例
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String url = environment.getProperty(DATASOURCE_URL);
        String username = environment.getProperty(DATASOURCE_USERNAME);
        String password = environment.getProperty(DATASOURCE_PASSWORD, "");

        // 非 MySQL 数据源或未配置时跳过
        if (url == null || !url.startsWith(JDBC_MYSQL_PREFIX) || username == null) {
            return;
        }

        String dbName = extractDatabaseName(url);
        if (dbName == null || dbName.isBlank()) {
            return;
        }

        String serverUrl = buildServerUrl(url);

        try (Connection conn = DriverManager.getConnection(serverUrl, username, password);
             Statement stmt = conn.createStatement()) {

            // 检查数据库是否存在
            ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'");

            if (!rs.next()) {
                // 数据库不存在，创建
                stmt.executeUpdate("CREATE DATABASE `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
                log.info("数据库初始化 | 数据库 {} 不存在，已自动创建（utf8mb4 / utf8mb4_general_ci）", dbName);
            } else {
                log.info("数据库初始化 | 数据库 {} 已存在", dbName);
            }

        } catch (Exception e) {
            log.error("数据库初始化 | 检查/创建数据库 {} 失败: {}", dbName, e.getMessage(), e);
            throw new RuntimeException("数据库初始化失败: " + dbName, e);
        }
    }

    /**
     * 从 JDBC URL 中提取数据库名称
     * <p>
     * 示例：{@code jdbc:mysql://localhost:3306/delta_escort?useUnicode=true} → {@code delta_escort}
     * </p>
     *
     * @param url JDBC URL
     * @return 数据库名称
     */
    private String extractDatabaseName(String url) {
        // 定位 host:port 之后的 '/' 和参数分隔符 '?'
        int dbStart = url.indexOf('/', JDBC_MYSQL_PREFIX.length());
        if (dbStart < 0) {
            return null;
        }
        int paramStart = url.indexOf('?', dbStart);
        if (paramStart > 0) {
            return url.substring(dbStart + 1, paramStart);
        }
        return url.substring(dbStart + 1);
    }

    /**
     * 构建 MySQL 服务器连接 URL（不含数据库名）
     * <p>
     * 示例：{@code jdbc:mysql://localhost:3306/delta_escort?useUnicode=true}
     * → {@code jdbc:mysql://localhost:3306/?useUnicode=true}
     * </p>
     *
     * @param url 原始 JDBC URL
     * @return 服务器级别连接 URL
     */
    private String buildServerUrl(String url) {
        int dbStart = url.indexOf('/', JDBC_MYSQL_PREFIX.length());
        int paramStart = url.indexOf('?', dbStart);
        if (paramStart > 0) {
            return url.substring(0, dbStart + 1) + url.substring(paramStart);
        }
        return url.substring(0, dbStart + 1);
    }
}
