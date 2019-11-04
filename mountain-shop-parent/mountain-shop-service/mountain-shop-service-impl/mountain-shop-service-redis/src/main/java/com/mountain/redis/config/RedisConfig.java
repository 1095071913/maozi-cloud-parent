/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.mountain.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


/**
 * 
 * 	功能说明：Redis配置
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


@Configuration
public class RedisConfig {
    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }
    
    
    
    //Redis集群
    
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration) {
//        return new LettuceConnectionFactory(redisClusterConfiguration);
//    }
//
//    @Bean
//    public RedisClusterConfiguration redisClusterConfiguration() {
//        RedisClusterConfiguration configuration = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
//        configuration.setPassword(RedisPassword.of(redisProperties.getPassword()));
//        return configuration;
//    }
}