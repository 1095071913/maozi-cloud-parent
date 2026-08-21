package com.maozi.system.config.api;

import com.maozi.system.config.vo.ConfigDropDownResult;

/**
 * 全局配置服务接口
 * <p>
 * 定义全局配置模块的内部业务方法，供本服务内部及 REST/RPC 实现层复用。
 * </p>
 */
public interface ConfigService {

	/**
	 * 根据配置名称获取配置信息
	 * <p>
	 * 配置名称为全局唯一键，配置不存在时抛出业务异常。
	 * </p>
	 *
	 * @param name 配置名称
	 * @return 配置信息，包含配置 ID、名称、别名与配置值
	 */
	ConfigDropDownResult getConfigByName(String name);

}
