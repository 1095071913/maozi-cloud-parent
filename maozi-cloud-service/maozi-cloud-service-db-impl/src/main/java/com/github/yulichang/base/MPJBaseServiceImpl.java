package com.github.yulichang.base;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import com.maozi.base.Page;

/**
 * @author yulichang
 * @see ServiceImpl
 */
@SuppressWarnings("unused")
public abstract class MPJBaseServiceImpl<M extends MPJBaseMapper<T>, T> extends ServiceImpl<M, T> implements MPJBaseService<T> {

    @Override
    public Class<T> currentModelClass() {
        return super.currentModelClass();
    }
    
    protected com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> convertPage(Page pageParam){
    	return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<T>(pageParam.getCurrent(),pageParam.getSize());
    }
    
    protected <D> Page convertPage(IPage<T> page,Supplier<D> target) {
		return new Page<List<D>>(page.getCurrent(),page.getSize(),page.getTotal(),copyList(page.getRecords(),target));
	}
    
    protected <D> Page convertPage(IPage page,List<D> responseDatas) {
		return new Page<List<D>>(page.getCurrent(),page.getSize(),page.getTotal(),responseDatas);
	}
    
    protected <D> Map<Long,D> toMapByIds(List<D> datas,Function<D, Long> function) {
    	return datas.stream().collect(Collectors.toMap(function, Function.identity()));
	}
    
    protected <D> String[] getColumns(SFunction<D, ?> ... sfunctions) throws Exception {
    	
    	int i = 0;
    	
    	String [] columns = new String[sfunctions.length];
    	
    	for(SFunction<D, ?> fn : sfunctions) {
			
			Method method = fn.getClass().getDeclaredMethod("writeReplace");
			
	        method.setAccessible(true);
	        
	        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(fn);
	        
	        String fieldWithGet = serializedLambda.getImplMethodName();
	        
	        columns[i] = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldWithGet.substring(3));
	        
	        ++i;
	        
		}
    	
    	return columns;
    	
	}
    
    protected T getById(Long id,String ... columns){
    	
    	QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
    	
    	if(columns.length > 0) {
    		queryWrapper.select(columns);
    	}
    	
    	return getOne(queryWrapper.eq("id", id));
    	
	}
    
    protected List<T> listByIds(Collection<?> ids,String ... columns){
    	
    	QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
    	
    	if(columns.length > 0) {
    		queryWrapper.select(columns);
    	}
    	
    	return list(queryWrapper.in("id", ids));
    	
	}
    
}
