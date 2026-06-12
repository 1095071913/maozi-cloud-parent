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

import com.alibaba.csp.sentinel.slots.block.Rule;

import java.util.Date;

/**
 * 规则实体接口
 * <p>
 * 该接口定义了 Sentinel 规则实体的公共契约，所有类型的规则实体都必须实现此接口。
 * 提供了规则标识、应用名称、目标机器 IP 和端口、创建时间等元数据的访问方法，
 * 以及将实体转换为具体规则对象的方法。
 * </p>
 *
 * @author leyou
 */
public interface RuleEntity {

    /**
     * 获取规则的唯一标识 ID
     *
     * @return 规则 ID
     */
    Long getId();

    /**
     * 设置规则的唯一标识 ID
     *
     * @param id 规则 ID
     */
    void setId(Long id);

    /**
     * 获取规则所属的应用名称
     *
     * @return 应用名称
     */
    String getApp();

    /**
     * 获取规则目标机器的 IP 地址
     *
     * @return IP 地址
     */
    String getIp();

    /**
     * 获取规则目标机器的端口号
     *
     * @return 端口号
     */
    Integer getPort();

    /**
     * 获取规则的创建时间
     *
     * @return 创建时间
     */
    Date getGmtCreate();

    /**
     * 将当前实体转换为 Sentinel 规则对象
     *
     * @return Sentinel 规则对象
     */
    Rule toRule();
}
