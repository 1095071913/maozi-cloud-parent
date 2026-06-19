package com.maozi.oauth.client.api.rpc;

import com.maozi.base.result.DropDownResult;
import com.maozi.common.result.AbstractBaseResult;

import java.util.Collection;
import java.util.List;

/**
 * 客户端管理 RPC 接口
 * <p>
 * 提供客户端（OAuth2 Client）的远程过程调用（RPC）接口定义，
 * 用于微服务之间的内部调用，包括客户端可用性校验、单个和批量获取客户端下拉信息等功能。
 * </p>
 */
public interface RpcClientService {

	/**
	 * 校验指定客户端是否可用
	 *
	 * @param id 客户端唯一标识 ID
	 * @return 返回空结果，校验通过无异常；不可用时返回错误信息
	 */
	AbstractBaseResult<Void> checkAvailableResult(Long id);

	/**
	 * 根据客户端 ID 获取单个客户端的下拉选项信息
	 *
	 * @param id 客户端唯一标识 ID
	 * @return 返回客户端的下拉选项结果
	 */
	AbstractBaseResult<DropDownResult> dropDownResult(Long id);

	/**
	 * 根据客户端 ID 集合批量获取客户端的下拉选项信息列表
	 *
	 * @param ids 客户端唯一标识 ID 集合
	 * @return 返回客户端下拉选项结果列表
	 */
	AbstractBaseResult<List<DropDownResult>> dropDownListResult(Collection<Long> ids);

}