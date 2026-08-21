package com.maozi.system.config.mapper;

import com.maozi.service.api.IBaseMapper;
import com.maozi.system.config.domain.ConfigDo;

/**
 * 全局配置 Mapper 接口
 * <p>
 * 继承自 IBaseMapper，提供全局配置实体（ConfigDo）的数据库访问操作。
 * 基于 MyBatis-Plus 框架，自动提供基础的 CRUD 操作方法。
 * </p>
 */
public interface ConfigMapper extends IBaseMapper<ConfigDo>{}
