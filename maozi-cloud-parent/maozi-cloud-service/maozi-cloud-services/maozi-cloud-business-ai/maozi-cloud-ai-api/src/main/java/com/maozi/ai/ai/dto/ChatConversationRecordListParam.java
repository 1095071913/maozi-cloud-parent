package com.maozi.ai.ai.dto;

import com.maozi.base.param.plugin.OrderParam;
import com.maozi.base.plugin.context.QueryEnvironmentContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/8/17 09:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationRecordListParam implements OrderParam, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 排序字段映射，key为字段名，value为排序方向（true 降序、false 升序、null 不排序） */
    private Map<String, Boolean> orderFieldMap;

    /** 升序排序字段映射 */
    private Map<String, List<String>> orderAscFieldsMap;

    /** 降序排序字段映射 */
    private Map<String, List<String>> orderDescFieldsMap;

    /** 主排序字段映射，默认按创建时间倒序排列 */
    private final Map<String, Map<String, Boolean>> orderMainFieldsMap = new HashMap<>() {{

        put(QueryEnvironmentContext.DEFAULT_ORDER_KEY, new HashMap<>() {{
            put("createTime", true);
        }});

    }};

}
