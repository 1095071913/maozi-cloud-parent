package com.maozi.base.param.plugin;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 排序参数接口
 * <p>
 * 提供通用的排序功能定义，支持多表多字段排序。
 * 通过 {@link #getOrderFieldMap()} 获取前端传入的排序规则，
 * 并结合 {@link #getOrderMainFieldsMap()} 定义的主表可排序字段，
 * 自动将排序字段按升序/降序分组，转换为下划线命名的 SQL 排序字段。
 * </p>
 *
 * @author maozi
 */
public interface OrderParam {

	/**
	 * 获取排序字段映射
	 *
	 * @return key 为排序字段，value 为排序方向（true 降序、false 升序、null 不排序）
	 */
	@Schema(description = "排序组件 key:排序字段 value: true降序、false升序、null不做排序、空字符串字段")
	Map<String,Boolean> getOrderFieldMap();

	/**
	 * 获取主表可排序字段映射
	 *
	 * @return key 为主表别名，value 为该表下可排序的字段及默认排序方向
	 */
	@JsonIgnore
	Map<String,Map<String,Boolean>> getOrderMainFieldsMap();

	/**
	 * 获取升序排序字段映射
	 *
	 * @return key 为主表别名，value 为该表下需要升序排序的字段列表
	 */
	@JsonIgnore
	Map<String,List<String>> getOrderAscFieldsMap();

	/**
	 * 设置升序排序字段映射
	 *
	 * @param orderAscFieldsMap 升序排序字段映射
	 */
	@JsonIgnore
	void setOrderAscFieldsMap(Map<String,List<String>> orderAscFieldsMap);

	/**
	 * 获取降序排序字段映射
	 *
	 * @return key 为主表别名，value 为该表下需要降序排序的字段列表
	 */
	@JsonIgnore
	Map<String,List<String>> getOrderDescFieldsMap();

	/**
	 * 设置降序排序字段映射
	 *
	 * @param orderDescFieldsMap 降序排序字段映射
	 */
	@JsonIgnore
	void setOrderDescFieldsMap(Map<String,List<String>> orderDescFieldsMap);

	/**
	 * 初始化排序参数
	 * <p>
	 * 遍历主表可排序字段，结合前端传入的排序规则，将排序字段按升序/降序分组，
	 * 并转换为 "表别名.下划线字段名" 格式存储。
	 * </p>
	 */
	default void initOrderParam() {

		// 获取主表可排序字段映射（key=表别名, value=该表下的字段及默认排序方向）
		Map<String, Map<String, Boolean>> orderMainFieldsMap = getOrderMainFieldsMap();

		// 初始化升序字段映射（key=表别名, value=该表下需要升序排序的字段列表）
		Map<String,List<String>> orderAscFieldsMap = CollectionUtil.newHashMap();

		// 初始化降序字段映射（key=表别名, value=该表下需要降序排序的字段列表）
		Map<String,List<String>> orderDescFieldsMap = CollectionUtil.newHashMap();

		// 遍历每一张表的可排序字段
		for(String key : orderMainFieldsMap.keySet()) {

			// 获取当前表的所有可排序字段及其默认排序方向
			Map<String,Boolean> orderMainFields = orderMainFieldsMap.get(key);

			// 当前表的升序字段集合
			List<String> orderAscFields = CollectionUtil.newArrayList();

			// 当前表的降序字段集合
			List<String> orderDescFields = CollectionUtil.newArrayList();

			// 遍历当前表的每一个可排序字段
			for(String orderMainField : orderMainFields.keySet()) {

				// 获取前端传入的排序规则
				Map<String, Boolean> orderFieldMap = getOrderFieldMap();

				// 如果前端未传入排序规则，使用空 Map 以避免空指针
				if(ObjectUtil.isNullEmpty(orderFieldMap)){
					orderFieldMap = CollectionUtil.newHashMap();
				}

				// 优先使用前端传入的排序方向，若前端未指定则使用字段默认的排序方向
				Boolean orderMainFieldValue = orderFieldMap.containsKey(orderMainField) ? orderFieldMap.get(orderMainField) : orderMainFields.get(orderMainField);

				// 排序方向不为空时，将该字段加入对应的升序或降序列表
				if(ObjectUtil.isNotNullEmpty(orderMainFieldValue)) {

					// 将表别名转换为下划线格式（驼峰转下划线）
					String underlineCase = StrUtil.toUnderlineCase(key);

					// 表别名非空时，拼接为 "表别名.字段名"；为空时仅保留下划线格式的字段名（无前缀）
					String field = StringUtils.isEmpty(key) ? underlineCase : underlineCase + "." + StrUtil.toUnderlineCase(orderMainField);

					// true 表示降序，false 表示升序
					if(orderMainFieldValue) {
						orderDescFields.add(field);
					}else {
						orderAscFields.add(field);
					}

				}

			}

			// 将当前表的升序和降序字段列表存入映射中
			orderAscFieldsMap.put(key, orderAscFields);

			orderDescFieldsMap.put(key, orderDescFields);

		}

		// 将最终结果写入接口属性，供后续 SQL 构建使用
		setOrderAscFieldsMap(orderAscFieldsMap);

		setOrderDescFieldsMap(orderDescFieldsMap);

	}

}
