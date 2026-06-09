package com.maozi.base.api;

import com.github.yulichang.base.MPJBaseMapper;

/**
 * 基础 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus-Join 的 {@link MPJBaseMapper}，提供基础的 CRUD 和关联查询能力。
 * 所有业务 Mapper 接口均需继承此接口。
 * </p>
 *
 * @param <T> 实体类型
 * @author maozi
 */
public interface IBaseMapper<T> extends MPJBaseMapper<T> {

}
