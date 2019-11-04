
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

package com.mountain.sso.api.impl;

import org.springframework.security.oauth2.provider.ClientRegistrationException;
import com.mountain.api.base.service.impl.BaseServiceImpl;
import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.factory.result.code.CodeAttribute;
import com.mountain.factory.result.code.CodeHashMap;
import com.mountain.oauth2.BootClientDetails;
import com.mountain.oauth2.Client;
import com.mountain.sso.api.ClientDetailsServiceRpc;
import com.mountain.sso.mapper.ClientMapper;


/**
 * 
 * 	功能说明：Client Rpc 实现
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-28 : 14:22:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class ClientDetailsServiceRpcImpl extends BaseServiceImpl<Client, ClientMapper> implements ClientDetailsServiceRpc {

	static {
		
		code(new CodeHashMap(applicationName, port) {
			
			{

				this.put(new CodeAttribute(1001, "客户端不存在"));

			}
		
		});

	}
	
	

	@Override
	public AbstractBaseResult loadClientByClientId(String clientId) throws ClientRegistrationException {
		Client client = null;
		try {
			client = this.selectOne(new Client() {{setClientId(clientId);}}).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        if(isNotNull(client)){
            return error(code(1001));
        }
        return success(new BootClientDetails(client));
	}

}
