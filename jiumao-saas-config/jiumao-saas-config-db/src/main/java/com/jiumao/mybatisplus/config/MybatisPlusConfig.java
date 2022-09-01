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

package com.jiumao.mybatisplus.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.jiumao.sso.OauthUserDetails;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * 
 * 功能说明：Mybatis Plus配置
 * 
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 * 创建日期：2019-08-03 ：1:32:00
 *
 * 版权归属：蓝河团队
 *
 * 协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Configuration
public class MybatisPlusConfig {

	@Value("${mybatis-plus.configuration.tenantTables.split(','):#{null}}")
	private List<String> tenantTables;
	
	@Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
		
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            	if(authentication != null && authentication.getPrincipal() != null) {
            		OauthUserDetails oauthUserDetails = (OauthUserDetails)authentication.getPrincipal();
            		if(authentication.getDetails()==null) {
            			return null;
            		}
            		return new LongValue(authentication.getDetails().toString());
        		}
            	return null;
            }

            @Override
            public boolean ignoreTable(String tableName) {
            	if (tenantTables == null) {
            		return true;
            	}
                return !tenantTables.contains(tableName);
            }
            
            @Override
            public String getTenantIdColumn() {
            	return "client_id";
            }
            
        }));
        
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

}
