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
package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Util class for Nacos rule persistence.
 * <p>
 * DataId of each rule type is {@code appName + DATA_ID_POSTFIX} with a {@code .json}
 * suffix, e.g. {@code foo-app-flow-rules.json}. Config content is pretty-printed
 * JSON and is published with config type {@code json}.
 *
 * @author maozi
 */
public final class NacosConfigUtil {

    /**
     * TTL of the publish snapshot: {@link ConfigService#getConfig} serves the local
     * client cache which is refreshed by a server push, so reading immediately after
     * publishing may return the stale previous content (the frontend reloads the rule
     * list right after saving, which made changes look "not applied" until a manual
     * refresh). Within this short window after a successful publish, the snapshot of
     * the just-published content is served instead.
     */
    private static final long SNAPSHOT_TTL_MS = 5000;

    private static final Map<String, PublishSnapshot> PUBLISH_SNAPSHOTS = new ConcurrentHashMap<>();

    private static final class PublishSnapshot {
        private final String content;
        private final long publishTime;

        private PublishSnapshot(String content, long publishTime) {
            this.content = content;
            this.publishTime = publishTime;
        }
    }

    public static final String GROUP_ID = "SENTINEL_GROUP";

    public static final String FLOW_DATA_ID_POSTFIX = "-flow-rules.json";
    public static final String DEGRADE_DATA_ID_POSTFIX = "-degrade-rules.json";
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "-param-rules.json";
    public static final String SYSTEM_DATA_ID_POSTFIX = "-system-rules.json";
    public static final String AUTHORITY_DATA_ID_POSTFIX = "-authority-rules.json";
    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "-gw-flow-rules.json";
    public static final String GATEWAY_API_DATA_ID_POSTFIX = "-gw-api-rules.json";
    public static final String CLUSTER_MAP_DATA_ID_POSTFIX = "-cluster-map";

    /**
     * cc for `cluster-client`
     */
    public static final String CLIENT_CONFIG_DATA_ID_POSTFIX = "-cc-config";
    /**
     * cs for `cluster-server`
     */
    public static final String SERVER_TRANSPORT_CONFIG_DATA_ID_POSTFIX = "-cs-transport-config";
    public static final String SERVER_FLOW_CONFIG_DATA_ID_POSTFIX = "-cs-flow-config";
    public static final String SERVER_NAMESPACE_SET_DATA_ID_POSTFIX = "-cs-namespace-set";

    /**
     * Publish rules to {@code appName + dataIdPostfix} as pretty-printed JSON,
     * with the Nacos config type marked as {@code json}.
     */
    public static void publishJsonConfig(ConfigService configService, String appName,
                                         String dataIdPostfix, String group, Object rules) throws NacosException {
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        String dataId = appName + dataIdPostfix;
        String content = JSON.toJSONString(rules, SerializerFeature.PrettyFormat);
        configService.publishConfig(dataId, group, content, ConfigType.JSON.getType());
        PUBLISH_SNAPSHOTS.put(snapshotKey(dataId, group), new PublishSnapshot(content, System.currentTimeMillis()));
    }

    /**
     * Read config content: serve the snapshot of the just-published content while it
     * is fresh (see {@link #SNAPSHOT_TTL_MS}), otherwise fall back to
     * {@link ConfigService#getConfig}.
     */
    public static String readFreshConfig(ConfigService configService, String appName,
                                         String dataIdPostfix, String group, long timeoutMs) throws NacosException {
        String dataId = appName + dataIdPostfix;
        long now = System.currentTimeMillis();
        PublishSnapshot snapshot = PUBLISH_SNAPSHOTS.get(snapshotKey(dataId, group));
        if (snapshot != null && now - snapshot.publishTime < SNAPSHOT_TTL_MS) {
            return snapshot.content;
        }
        return configService.getConfig(dataId, group, timeoutMs);
    }

    private static String snapshotKey(String dataId, String group) {
        return dataId + "@" + group;
    }

    private NacosConfigUtil() {}
}
