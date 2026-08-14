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

import com.alibaba.csp.sentinel.slots.block.AbstractRule;

import java.util.Date;

/**
 * 规则实体的抽象基类
 * <p>
 * 该类实现了 {@link RuleEntity} 接口，为所有 Sentinel 规则实体提供通用的属性和方法。
 * 包含规则的唯一标识 ID、所属应用名称、目标机器 IP、目标机器端口等元数据信息，
 * 以及规则对象的创建时间和修改时间。
 * 泛型参数 <T> 指定具体的规则类型，必须继承自 {@link AbstractRule}。
 * 除 setId（接口定义返回 void）外，其余 setter 方法返回 this，支持链式调用。
 * </p>
 * <p>
 * <b>注：</b>本类为复制自 Sentinel 控制台（sentinel-dashboard）1.8.6 的同名类的本地副本，
 * 代码逻辑未做修改；复制目的：在不整体依赖 sentinel-dashboard 模块的前提下，
 * 为客户端侧的授权规则 JSON 转换器（{@code AuthorityRuleJsonConverter}）提供规则实体定义，
 * 升级 Sentinel 版本时需同步比对上游变更。
 * </p>
 *
 * @param <T> 具体的 Sentinel 规则类型，如 FlowRule、DegradeRule 等
 * @author Eric Zhao
 * @since 0.2.1
 */
public abstract class AbstractRuleEntity<T extends AbstractRule> implements RuleEntity {

    /** 规则的唯一标识 ID */
    protected Long id;

    /** 规则所属的应用名称 */
    protected String app;
    /** 规则目标机器的 IP 地址 */
    protected String ip;
    /** 规则目标机器的端口号 */
    protected Integer port;

    /** 具体的规则对象 */
    protected T rule;

    /** 规则创建时间（格林威治标准时间） */
    private Date gmtCreate;
    /** 规则最后修改时间（格林威治标准时间） */
    private Date gmtModified;

    /**
     * 获取规则的唯一标识 ID
     *
     * @return 规则 ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * 设置规则的唯一标识 ID
     *
     * @param id 规则 ID
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取规则所属的应用名称
     *
     * @return 应用名称
     */
    @Override
    public String getApp() {
        return app;
    }

    /**
     * 设置规则所属的应用名称
     *
     * @param app 应用名称
     * @return 当前实体对象，支持链式调用
     */
    public AbstractRuleEntity<T> setApp(String app) {
        this.app = app;
        return this;
    }

    /**
     * 获取规则目标机器的 IP 地址
     *
     * @return IP 地址
     */
    @Override
    public String getIp() {
        return ip;
    }

    /**
     * 设置规则目标机器的 IP 地址
     *
     * @param ip IP 地址
     * @return 当前实体对象，支持链式调用
     */
    public AbstractRuleEntity<T> setIp(String ip) {
        this.ip = ip;
        return this;
    }

    /**
     * 获取规则目标机器的端口号
     *
     * @return 端口号
     */
    @Override
    public Integer getPort() {
        return port;
    }

    /**
     * 设置规则目标机器的端口号
     *
     * @param port 端口号
     * @return 当前实体对象，支持链式调用
     */
    public AbstractRuleEntity<T> setPort(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * 获取具体的规则对象
     *
     * @return 规则对象
     */
    public T getRule() {
        return rule;
    }

    /**
     * 设置具体的规则对象
     *
     * @param rule 规则对象
     * @return 当前实体对象，支持链式调用
     */
    public AbstractRuleEntity<T> setRule(T rule) {
        this.rule = rule;
        return this;
    }

    /**
     * 获取规则的创建时间
     *
     * @return 创建时间
     */
    @Override
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置规则的创建时间
     *
     * @param gmtCreate 创建时间
     * @return 当前实体对象，支持链式调用
     */
    public AbstractRuleEntity<T> setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
        return this;
    }

    /**
     * 获取规则的最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置规则的最后修改时间
     *
     * @param gmtModified 最后修改时间
     * @return 当前实体对象，支持链式调用
     */
    public AbstractRuleEntity<T> setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
        return this;
    }

    /**
     * 将实体转换为具体的规则对象
     *
     * @return 具体的规则对象
     */
    @Override
    public T toRule() {
        return rule;
    }
}
