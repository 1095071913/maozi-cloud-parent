package com.maozi.base.api;

import java.util.Collection;

import com.github.yulichang.base.MPJBaseMapper;

public interface IBaseMapper<T> extends MPJBaseMapper<T> {
	
    Integer insertBatchSomeColumn(Collection<T> entityList);
    
}