/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.datasource.entity.rule;

import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 授权规则实体类
 * <p>
 * 继承自 {@link AbstractRuleEntity}，用于封装 Sentinel 的授权规则（{@link AuthorityRule}）。
 * 授权规则用于控制请求来源的访问权限，即根据调用方的来源（origin）决定是否允许访问指定资源。
 * 支持"白名单"和"黑名单"两种授权策略。
 * </p>
 * <p>
 * <b>注：</b>本类为复制自 Sentinel 控制台（sentinel-dashboard）1.8.6 的同名类的本地副本，
 * 代码逻辑未做修改；复制目的：在不整体依赖 sentinel-dashboard 模块的前提下，
 * 为客户端侧的授权规则 JSON 转换器（{@code AuthorityRuleJsonConverter}）提供授权规则实体定义，
 * 升级 Sentinel 版本时需同步比对上游变更。
 * </p>
 *
 * @author Eric Zhao
 * @since 0.2.1
 */
public class AuthorityRuleEntity extends AbstractRuleEntity<AuthorityRule> {

    /**
     * 无参构造函数
     */
    public AuthorityRuleEntity() {
    }

    /**
     * 带授权规则的构造函数
     *
     * @param authorityRule 授权规则对象，不能为 null
     * @throws IllegalArgumentException 当 authorityRule 为 null 时抛出
     */
    public AuthorityRuleEntity(AuthorityRule authorityRule) {
        AssertUtil.notNull(authorityRule, "Authority rule should not be null");
        this.rule = authorityRule;
    }

    /**
     * 根据应用信息创建授权规则实体
     *
     * @param app  应用名称
     * @param ip   目标机器 IP 地址
     * @param port 目标机器端口号
     * @param rule 授权规则对象
     * @return 包含完整应用信息和规则的授权规则实体
     */
    public static AuthorityRuleEntity fromAuthorityRule(String app, String ip, Integer port, AuthorityRule rule) {
        AuthorityRuleEntity entity = new AuthorityRuleEntity(rule);
        entity.setApp(app);
        entity.setIp(ip);
        entity.setPort(port);
        return entity;
    }

    /**
     * 获取授权规则限制的来源应用名称
     * <p>使用 @JsonIgnore 和 @JSONField(serialize = false) 标注，表示序列化时忽略此属性，避免重复输出</p>
     *
     * @return 限制的来源应用名称，多个用逗号分隔
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getLimitApp() {
        return rule.getLimitApp();
    }

    /**
     * 获取授权规则保护的资源名称
     * <p>使用 @JsonIgnore 和 @JSONField(serialize = false) 标注，表示序列化时忽略此属性，避免重复输出</p>
     *
     * @return 资源名称
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getResource() {
        return rule.getResource();
    }

    /**
     * 获取授权策略
     * <p>使用 @JsonIgnore 和 @JSONField(serialize = false) 标注，表示序列化时忽略此属性，避免重复输出</p>
     * <ul>
     *   <li>AuthorityRuleConstant.AUTHORITY_WHITE = 0：白名单模式</li>
     *   <li>AuthorityRuleConstant.AUTHORITY_BLACK = 1：黑名单模式</li>
     * </ul>
     *
     * @return 授权策略值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public int getStrategy() {
        return rule.getStrategy();
    }
}
