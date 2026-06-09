/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maozi.dubbo.provider;

import org.apache.dubbo.registry.support.DefaultProviderFirstParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.apache.dubbo.common.constants.CommonConstants.DUBBO_VERSION_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.METHODS_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.RELEASE_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.TAG_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.TIMESTAMP_KEY;

/**
 * 服务提供者优先参数配置
 * <p>
 * 继承 Dubbo 的 {@link DefaultProviderFirstParams}，定义服务提供者端
 * 优先使用的参数集合。这些参数以服务提供者的值为准，不会被消费者覆盖。
 * 包括 Dubbo 版本、发布版本、方法列表、时间戳、标签和应用版本。
 * </p>
 *
 * @author maozi
 */
public class ProviderFirstParams extends DefaultProviderFirstParams {

    /** 服务提供者优先参数集合 */
    private final static Set<String> PARAMS = Collections.unmodifiableSet(new HashSet<>() {{
        addAll(Arrays.asList(RELEASE_KEY, DUBBO_VERSION_KEY, METHODS_KEY, TIMESTAMP_KEY, TAG_KEY,"application.version"));
    }});

    /**
     * 获取服务提供者优先参数集合
     *
     * @return 不可变的参数名集合
     */
    @Override
    public Set<String> params() {
        return PARAMS;
    }

}
