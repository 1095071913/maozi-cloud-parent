package com.maozi.oauth.client.param;

import com.maozi.base.param.plugin.OrderParam;
import com.maozi.base.plugin.context.QueryEnvironmentContext;
import com.maozi.base.plugin.query.QueryBaseType;
import com.maozi.base.plugin.query.QueryPlugin;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端列表查询参数
 * <p>
 * 用于OAuth客户端分页列表查询的请求参数，支持按名称模糊搜索和排序功能。
 * </p>
 */
@Data
public class ClientListParam implements OrderParam,Serializable {

    /** 序列化版本号 */
    @Serial
    private static final long serialVersionUID = 1L;

	/** 客户端名称，支持模糊查询 */
	@Schema(description = "名称")
	@QueryPlugin(value = QueryBaseType.LIKE,field = "client_name")
	private String name;

	/** 排序字段映射，key为字段名，value为是否升序 */
	private Map<String,Boolean> orderFieldMap;

    /** 升序排序字段映射，key为分组标识，value为该分组下需要升序排序的字段列表 */
    private Map<String,List<String>> orderAscFieldsMap;

    /** 降序排序字段映射，key为分组标识，value为该分组下需要降序排序的字段列表 */
    private Map<String,List<String>> orderDescFieldsMap;

    /** 主排序字段映射，key为排序分组标识，value为该分组下的排序字段及排序方向配置，默认按创建时间升序排列 */
    private Map<String, Map<String, Boolean>> orderMainFieldsMap = new HashMap<>() {{

        put(QueryEnvironmentContext.DEFAULT_ORDER_KEY, new HashMap<>() {{
            put("createTime", true);
        }});

    }};

}
