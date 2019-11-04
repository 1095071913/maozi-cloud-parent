package com.mountain.mybatisplus.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;

@Configuration
public class MybatisPlusConfig {

	
	/**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
    
    /**
    * SQL执行效率插件
    */
   @Bean
   @Profile({"dev","test"})// 设置 dev test 环境开启
   public PerformanceInterceptor performanceInterceptor() {
       return new PerformanceInterceptor();
   } 
   
   
   @Bean 
   @Primary
   @ConfigurationProperties(prefix = "spring.datasource")
   public DataSource dataSource() {
       // 配置数据源（注意，我使用的是 HikariCP 连接池），以上注解是指定数据源，否则会有冲突
       return DataSourceBuilder.create().build();
   }
   
   
   /**
    * 乐观锁
    * @return
    */
   @Bean
   public OptimisticLockerInterceptor optimisticLockerInterceptor() {
       return new OptimisticLockerInterceptor();
   }
	
}
