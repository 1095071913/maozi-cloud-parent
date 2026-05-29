package com.maozi.db.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库自动初始化处理器（基于 BeanFactoryPostProcessor）
 * <p>
 * 在所有 Bean 实例化之前执行（此时 Environment 已完全就绪，包括 Nacos 远程配置），
 * 检查目标 MySQL 数据库是否存在，不存在则自动创建（字符集 utf8mb4，排序规则 utf8mb4_general_ci），
 * 确保 DataSource 和 Flyway Bean 创建时数据库已存在。
 * </p>
 * <p>
 * 以避免 {@code @Configuration} 类提前实例化的问题。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class DatabaseInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

    /** 数据源 URL 配置键 */
    private static final String DATASOURCE_URL = "spring.datasource.url";

    /** 数据源用户名配置键 */
    private static final String DATASOURCE_USERNAME = "spring.datasource.username";

    /** 数据源密码配置键 */
    private static final String DATASOURCE_PASSWORD = "spring.datasource.password";

    /** JDBC MySQL 前缀 */
    private static final String JDBC_MYSQL_PREFIX = "jdbc:mysql://";

    private Environment environment;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
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
            }

        } catch (Exception e) {
            throw new RuntimeException("数据库初始化失败: " + dbName, e);
        }
    }

    /**
     * 从 JDBC URL 中提取数据库名称
     * <p>
     * 示例：{@code jdbc:mysql://localhost:3306/my_db?useUnicode=true} → {@code my_db}
     * </p>
     *
     * @param url JDBC URL
     * @return 数据库名称
     */
    private String extractDatabaseName(String url) {
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
     * 示例：{@code jdbc:mysql://localhost:3306/my_db?useUnicode=true}
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
