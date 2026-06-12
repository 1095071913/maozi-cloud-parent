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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 授权规则的 JSON 转换器
 * <p>
 * 继承自 {@link JsonConverter}，专门用于将 JSON 格式的授权规则数据转换为 Sentinel 可识别的规则对象。
 * 使用严格模式解析 JSON，将 JSON 数组中的每条规则转换为 {@link AuthorityRuleEntity}，
 * 然后提取其中的 {@link AuthorityRule} 规则对象放入集合中返回。
 * </p>
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

	/** Jackson JSON 序列化/反序列化工具 */
	private ObjectMapper objectMapper;

	/** 授权规则实体的 Class 对象，用于反序列化 */
	private Class<AuthorityRuleEntity> ruleClass;

	/**
	 * 构造函数
	 *
	 * @param objectMapper Jackson ObjectMapper 实例，用于 JSON 的序列化和反序列化
	 */
	public AuthorityRuleJsonConverter(ObjectMapper objectMapper) {
		super(objectMapper, AuthorityRuleEntity.class);
		this.ruleClass=AuthorityRuleEntity.class;
		this.objectMapper=objectMapper;
	}

	/**
	 * 将 JSON 字符串转换为授权规则对象集合
	 * <p>
	 * 解析 JSON 数组格式的规则数据，将每条规则转换为 {@link AuthorityRuleEntity}，
	 * 然后提取其中的 {@link AuthorityRule} 放入结果集合中。
	 * 处理流程：
	 * <ol>
	 *   <li>检查源字符串是否为空，为空则返回空集合</li>
	 *   <li>将 JSON 字符串解析为 List&lt;HashMap&gt; 结构</li>
	 *   <li>遍历列表中的每个元素，先序列化为 JSON 字符串再反序列化为规则实体</li>
	 *   <li>从规则实体中提取具体的规则对象加入结果集合</li>
	 * </ol>
	 * </p>
	 *
	 * @param source JSON 格式的规则数据字符串
	 * @return 转换后的规则对象集合
	 * @throws RuntimeException 当 JSON 解析或转换失败时抛出
	 */
	@Override
	public Collection<Object> convert(String source) {

		// 初始化结果集合
		Collection<Object> ruleCollection = new ArrayList<>();

		// 如果源数据为空，直接返回空集合
		if (StringUtils.isEmpty(source)) {
			return ruleCollection;
		}
		try {
			// 将 JSON 字符串解析为 List<HashMap> 结构（两步解析，保证类型安全）
			List sourceArray = objectMapper.readValue(source,
					new TypeReference<List<HashMap>>() {
					});

			// 遍历每个规则元素，逐个进行转换
			for (Object obj : sourceArray) {
				String item = null;
				try {
					// 先将 HashMap 对象序列化为 JSON 字符串
					item = objectMapper.writeValueAsString(obj);
					// 再将 JSON 字符串反序列化为 AuthorityRuleEntity，提取其中的规则对象
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
			// 如果是 RuntimeException 直接抛出，否则包装为 RuntimeException 抛出
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			else {
				throw new RuntimeException("convert error: " + e.getMessage(), e);
			}
		}
		return ruleCollection;
	}

	/**
	 * 将 JSON 字符串反序列化为授权规则实体对象
	 *
	 * @param ruleStr JSON 格式的规则字符串
	 * @return 授权规则实体对象
	 * @throws IOException 当 JSON 解析失败时抛出
	 */
	private AuthorityRuleEntity convertRule(String ruleStr) throws IOException {
		return objectMapper.readValue(ruleStr, ruleClass);
	}

}
