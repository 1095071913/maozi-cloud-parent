package com.maozi.system.config.api.rest;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.service.annotation.Get;
import com.maozi.system.config.vo.SystemPropertiesVo;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 系统配置服务 REST 接口
 * <p>
 * 提供系统配置信息的 RESTful API 接口定义，
 * 主要用于获取系统级别的属性配置信息，如系统名称、版本号等全局配置。
 * </p>
 */
@Tag(name = "系统模块")
public interface RestSystemService {

	/** 基础路径常量，系统配置模块的统一请求路径前缀 */
	String PATH = "/config";

	/**
	 * 获取系统属性配置详情
	 *
	 * @return 返回系统属性配置信息，包含系统名称、描述等相关配置属性
	 */
	@Get(value = PATH + "/system/get",description = "详情")
	AbstractBaseResult<SystemPropertiesVo> get();

}
