package com.maozi.job;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-Job 分布式任务调度配置
 * <p>
 * 配置 XXL-Job 执行器连接参数，包括调度中心地址、执行器名称、
 * 端口、日志路径和访问令牌等。通过 Spring Bean 注册执行器实例。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class XxlJobConfig {

    /** 调度中心地址 */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    /** 访问令牌 */
    @Value("${xxl.job.accessToken}")
    private String accessToken;

    /** 执行器应用名称 */
    @Value("${xxl.job.executor.appname}")
    private String appname;

    /** 执行器注册地址 */
    @Value("${xxl.job.executor.address}")
    private String address;

    /** 执行器 IP */
    @Value("${xxl.job.executor.ip}")
    private String ip;

    /** 执行器端口 */
    @Value("${xxl.job.executor.port}")
    private int port;

    /** 执行器日志文件路径 */
    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    /** 日志保留天数 */
    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;


    /**
     * 创建 XXL-Job 执行器实例
     *
     * @return 配置好的 XxlJobSpringExecutor
     */
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }


}
