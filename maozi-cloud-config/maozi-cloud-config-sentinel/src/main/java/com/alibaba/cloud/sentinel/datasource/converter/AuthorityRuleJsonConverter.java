/*
 * Copyright 2013-2018 the original author or authors.
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
 */

package com.alibaba.cloud.sentinel.datasource.converter;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.util.StringUtils;

/**
 * Convert sentinel rules for json array Using strict mode to parse json.
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see FlowRule
 * @see DegradeRule
 * @see SystemRule
 * @see AuthorityRule
 * @see ParamFlowRule
 * @see ObjectMapper
 */
public class AuthorityRuleJsonConverter extends JsonConverter<AuthorityRuleEntity> {
	
	private ObjectMapper objectMapper;

	private Class<AuthorityRuleEntity> ruleClass;

	public AuthorityRuleJsonConverter(ObjectMapper objectMapper) {
		super(objectMapper, AuthorityRuleEntity.class);
		this.ruleClass=AuthorityRuleEntity.class;
		this.objectMapper=objectMapper;
	}

	@Override
	public Collection<Object> convert(String source) {
		
		Collection<Object> ruleCollection = new ArrayList<>();

		if (StringUtils.isEmpty(source)) {
			return ruleCollection;
		}
		try {
			List sourceArray = objectMapper.readValue(source,
					new TypeReference<List<HashMap>>() {
					});

			for (Object obj : sourceArray) {
				String item = null;
				try {
					item = objectMapper.writeValueAsString(obj);
					Optional.ofNullable(convertRule(item))
							.ifPresent(convertRule -> ruleCollection.add(convertRule.getRule()));
				}
				catch (IOException e) {
					throw new IllegalArgumentException(
							"sentinel rule convert error: " + e.getMessage(), e);
				}
			}
		}
		catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			else {
				throw new RuntimeException("convert error: " + e.getMessage(), e);
			}
		}
		return ruleCollection;
	}

	private AuthorityRuleEntity convertRule(String ruleStr) throws IOException {
		return objectMapper.readValue(ruleStr, ruleClass);
	}

}
