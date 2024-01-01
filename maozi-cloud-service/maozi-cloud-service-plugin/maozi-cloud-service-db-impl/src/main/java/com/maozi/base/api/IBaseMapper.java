package com.maozi.base.api;

import com.github.yulichang.base.MPJBaseMapper;
import java.util.Collection;

public interface IBaseMapper<T> extends MPJBaseMapper<T> {
	
    Integer insertBatchSomeColumn(Collection<T> entityList);
    
}