package com.maozi.base.api.impl;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.AbstractBaseDomain;
import com.maozi.base.AbstractBaseDtomain;
import com.maozi.base.api.IBaseMapper;
import com.maozi.base.api.rpc.BaseServiceResult;
import com.maozi.base.enums.Status;
import com.maozi.base.param.PageParam;
import com.maozi.base.param.SaveUpdateBatch;
import com.maozi.base.result.DropDownResult;
import com.maozi.base.result.PageResult;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.exception.BusinessResultException;

import cn.hutool.core.util.StrUtil;

public abstract class BaseServiceImpl<M extends IBaseMapper<T>, T extends AbstractBaseDomain,D> extends MPJBaseServiceImpl<M, T> implements MPJBaseService<T>,BaseServiceResult<D> {

	protected Class<T> doClass;
	
	protected Class<D> dtoClass;
	
	protected abstract String getAbbreviationModelName();
	
	public BaseServiceImpl() {
		
		for(Class<?> superClass = this.getClass();;superClass = superClass.getSuperclass()) {
		
		Type genericSuperclass = superClass.getGenericSuperclass();
		
			if(genericSuperclass instanceof ParameterizedType) {
			
				ParameterizedType type = (ParameterizedType) genericSuperclass;

				doClass = (Class<T>) type.getActualTypeArguments()[1];
			
				dtoClass = (Class<D>) type.getActualTypeArguments()[2];
				
				break ;
			
			}
		
		}
		
	}

    @Override
    public Class<T> currentModelClass() {
        return super.currentModelClass();
    }
    
    public Class<D> getDtoClass(){
    
    	if(dtoClass.getName().equals(Void.class.getName())) {
    		throw new BusinessResultException(500,"服务端未设置返回类型",500);
    	}
    	
    	return dtoClass;
    
    }
    
    protected com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> convertPage(PageParam pageParam){
    	return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<T>(pageParam.getCurrent(),pageParam.getSize());
    }
    
    protected <D> PageResult convertPageResult(Page<T> page,Supplier<D> target) {
		return new PageResult<D>(page.getCurrent(),page.getSize(),page.getTotal(),copyList(page.getRecords(),target));
	}
    
    protected <D> PageResult convertPageResult(IPage page,List<D> responseDatas) {
		return new PageResult<D>(page.getCurrent(),page.getSize(),page.getTotal(),responseDatas);
	}
    
    protected <D> Map<Long,D> toMapByIds(List<D> datas,Function<D, Long> function) {
    	return datas.stream().collect(Collectors.toMap(function, Function.identity()));
	}
    
    protected <D> String getColumn(SFunction<D, ?> sfunction) {
		
    	try {
    		
    		Method method = sfunction.getClass().getDeclaredMethod("writeReplace");
    		
            method.setAccessible(true);
            
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(sfunction);
            
            String fieldWithGet = serializedLambda.getImplMethodName();
            
            return StrUtil.toUnderlineCase(fieldWithGet.substring(3));
    		
    	}catch (Exception e) {
    		
			throwSystemError(e);
			
			return null;
			
		}
    	
	}
    
    protected <D> String[] getColumns(SFunction<D, ?> ... sfunctions) {
    	
    	int i = 0;
    	
    	String [] columns = new String[sfunctions.length];
    	
    	for(SFunction<D, ?> sfunction : sfunctions) {
	        
	        columns[i] = getColumn(sfunction);
	        
	        ++i;
	        
		}
    	
    	return columns;
    	
	}
    
    protected T getById(Long id,String ... columns){
    	
    	isNullThrowError(id, getAbbreviationModelName());
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	return getOne(wrapper);
    	
	}
    
    protected T getById(Long id,SFunction<T, ?> ... columns){
    	
    	isNullThrowError(id, getAbbreviationModelName());
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	return getOne(wrapper);
    	
	}
    
    protected void checkAvailable(Long id){
    	
    	isNullThrowError(id, getAbbreviationModelName());
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	wrapper.select(getColumn(AbstractBaseDomain::getStatus));
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	T domain = getOne(wrapper);
    	
    	isNullThrowError(domain, getAbbreviationModelName());
    	
    	checkBoolThrowError(domain.getStatus() == Status.enable,getAbbreviationModelName()+"已被禁用");
    	
	}
    
    protected void checkAvailable(List<Long> ids){
    	
    	collectionIsEmptyThrowError(ids,getAbbreviationModelName()+"列表");
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin(); 
    	
    	wrapper.select(getColumn(AbstractBaseDomain::getStatus));
    	
    	wrapper.in(getColumn(AbstractBaseDomain::getId),ids);
    	
    	List<T> domains = list(wrapper);
    	
    	domains.parallelStream().forEach((domain)->{
    		
    		if(domain.getStatus() == Status.disable) {
        		throw new BusinessResultException(400, getAbbreviationModelName()+"已被禁用",200);
        	}
    		
    	});
    	
	}
    
    protected T getAvailableById(Long id,String ... columns){
    	
    	isNullThrowError(id, getAbbreviationModelName());
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	wrapper.select(getColumn(AbstractBaseDomain::getStatus));
    	
    	wrapper.select(columns);
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	T domain = getOne(wrapper);
    	
    	isNullThrowError(domain, getAbbreviationModelName());
    	
    	if(domain.getStatus() == Status.disable) {
    		throw new BusinessResultException(400, getAbbreviationModelName()+"已被禁用",200);
    	}
    	
    	return domain;
    	
	}
    
    protected T getAvailableByParam(MPJLambdaWrapper<T> wrapper){
    	
    	wrapper.select(getColumn(AbstractBaseDomain::getStatus));
    	
    	T domain = getOne(wrapper);
    	
    	isNullThrowError(domain, getAbbreviationModelName());
    	
    	if(domain.getStatus() == Status.disable) {
    		throw new BusinessResultException(400, getAbbreviationModelName()+"已被禁用",200);
    	}
    	
    	return domain;
    	
	}
    
    protected void checkHas(Wrapper<T> wrapper) {
    	checkBoolThrowError(count(wrapper) > 0, getAbbreviationModelName()+"不存在");
	}
    
    protected void checkNotHas(Wrapper<T> wrapper) {
    	checkBoolThrowError(count(wrapper) < 1, getAbbreviationModelName()+"已存在");
	}
    
    protected DropDownResult dropDown(Long id){
    	
    	isNullThrowError(id, getAbbreviationModelName());
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	wrapper.select(getColumns(AbstractBaseDomain::getId,AbstractBaseDomain::getName));
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId), id);
    	
    	T domain = getOne(wrapper);
    	
    	isNullThrowError(domain, getAbbreviationModelName());
    	
    	return copy(domain, DropDownResult.class);
    	
	}
    
    protected List<DropDownResult> dropDownList(){
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	wrapper.select(getColumns(AbstractBaseDomain::getId,AbstractBaseDomain::getName));
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getStatus), Status.enable);
    	
    	return copyList(list(wrapper), DropDownResult::new);
    	
	}
    
    protected T getByIdThrowError(Long id,String ... columns){
    	
    	isNullThrowError(id, getAbbreviationModelName());
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	T domain = getOne(wrapper);
    	
    	isNullThrowError(domain,getAbbreviationModelName());
    	
    	return domain;
    	
	}
    
    protected <D> D getByIdThrowError(Wrapper<T> wrapper,Class<D> clazz) {
		
    	T domain = getOne(wrapper);
		
		isNullThrowError(domain, getAbbreviationModelName());
		
		return copy(domain, clazz);
    	
	}
    
    protected <D> T getByIdThrowError(Long id,SFunction<D, ?> ... columns){
    	return getByIdThrowError(id,getColumns(columns));
	}
    
    protected <V,D> V getByIdThrowError(Long id,Class<V> clazz,SFunction<D, ?> ... columns){
    	return copy(getByIdThrowError(id,columns), clazz);
	}
    
    protected List<T> listByIds(Collection<?> ids,String ... columns){
    	
    	collectionIsEmptyThrowError(ids,getAbbreviationModelName()+"列表");
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.in(getColumn(AbstractBaseDomain::getId),ids);
    	
    	return list(wrapper);
    	
	}
    
    protected <D,V> List<V> list(Supplier<V> target,SFunction<D, ?> ... columns) {
    	
    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	if(columns.length > 0) {
    		wrapper.select(columns);
    	}
		
    	List<T> list = list(wrapper);
    	
    	return copyList(list, target);
    	
	}
    
    protected <D> List<D> list(Wrapper<T> wrapper,Supplier<D> target) {
		
    	List<T> list = list(wrapper);
    	
    	return copyList(list, target);
    	
	}
    
    protected <V> PageResult<V> list(PageParam pageParam,Wrapper<T> wrapper,Supplier<V> target) {
		
    	Page<T> page = page(convertPage(pageParam),wrapper);
    	
    	return convertPageResult(page, target);
    	
	}
    
    public boolean saveUpdate(T domain) {
    	
    	isNullThrowError(domain,getAbbreviationModelName());
    	
    	if(isNotNull(domain.getId())) {
    		
    		MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    		
    		wrapper.eq(getColumn(AbstractBaseDomain::getId), domain.getId());
    		
    		checkBoolThrowError(count(wrapper) > 0,getAbbreviationModelName()+"不存在");
    		
    	}
    	
		return super.saveOrUpdate(domain);
		
	}
    
    
	public Long saveUpdate(Long id,AbstractBaseDtomain param) {
	    	
    	isNullThrowError(param,getAbbreviationModelName());
    	
    	T domain = copy(param, doClass);
    	
    	if(isNotNull(id)) {
    		
    		domain.setId(id);
    		
    		MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    		
    		wrapper.eq(getColumn(AbstractBaseDomain::getId), domain.getId());
    		
    		checkBoolThrowError(count(wrapper) > 0,getAbbreviationModelName()+"不存在");
    		
    	}else {
    		validate(param);
    	}
    	
    	super.saveOrUpdate(domain);
    	
    	return domain.getId();
		
	}
	
    public void saveUpdateBatch(List<T> domains) {
    	
    	collectionIsEmptyThrowError(domains,getAbbreviationModelName()+"列表");
    	
    	SecurityContext securityContext = SecurityContextHolder.getContext();
    	
    	domains.parallelStream().forEach((domain)->{
    		
    		SecurityContextHolder.setContext(securityContext);
    		
    		if(isNotNull(domain.getId())) {
        		
    			MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
        		
        		wrapper.eq(getColumn(AbstractBaseDomain::getId), domain.getId());
        		
        		checkBoolThrowError(count(wrapper) > 0,getAbbreviationModelName()+"不存在");
        		
        	}
        	
    		super.saveOrUpdate(domain);
    		
    		clear();
    		
    	});
    	
    }
    
    protected void checkBind(Long id) {};
    public void removeById(Long id) {
    	
    	isNullThrowError(id,getAbbreviationModelName());
		
		checkBind(id);
		
		super.removeById(id);
    	
    }
    
    @Override
    public AbstractBaseResult<D> getByIdResult(Long id,String ... colums) {
		return success(copy(getById(id, colums), getDtoClass()));
	}

    @Override
	public AbstractBaseResult<List<D>> listByIdsResult(List<Long> ids,String ... colums) {

		return success(copyList(listByIds(ids,colums), () -> {

			try {return getDtoClass().getDeclaredConstructor().newInstance();} catch (Exception e) {

				throwSystemError(e);

				return null;

			}

		}));

	}
	
    @Override
	public AbstractBaseResult<Long> getCountByParamResult(D dto){
    	
    	isNullThrowError(dto,getAbbreviationModelName());
    	
		return success(count(MPJWrappers.lambdaJoin(copy(dto, doClass))));
		
	}
	
	@Override
	public <D extends AbstractBaseDtomain> AbstractBaseResult<Long> saveUpdateResult(Long id,D param){
		return success(saveUpdate(id,param));
	}
	
	@Override
	public AbstractBaseResult<Void> saveUpdateBatchResult(List<SaveUpdateBatch> dtos){
		
		List<T> dos = copyList(collectionIsEmptyThrowError(dtos, getAbbreviationModelName()+"列表"), ()->{
			
			try {return doClass.getDeclaredConstructor().newInstance();} catch (Exception e) {
				
				throwSystemError(e);
				
				return null;
				
			}
		});
		
		saveUpdateBatch(dos);
		
		return success(null);
		
	}
	
	@Override
	public AbstractBaseResult<Void> removeByIdResult(Long id) {
		
		removeById(id);
		
		return success(null);
		
	}

	@Override
	public AbstractBaseResult<Void> removeByIdBatchResult(List<Long> ids) {
		
		collectionIsEmptyThrowError(ids, getAbbreviationModelName()+"列表");
		
		ids.parallelStream().forEach((id)->{
			
			checkBind(id);
			
			removeById(id);
			
		});
		
		return success(null);
		
	}
	
	@Override
	public AbstractBaseResult<Void> checkAvailableResult(Long id){
		
		checkAvailable(id);
		
		return success(null);
		
	}
	
	@Override
	public AbstractBaseResult<Void> checkAvailableResult(List<Long> ids){
		
		checkAvailable(ids);
		
		return success(null);
		
	}
	
	@Override
	public AbstractBaseResult<D> getAvailableByIdResult(Long id,String ... columns){
    	
    	T domain = getAvailableById(id, columns);
    	
    	return success(copy(domain, dtoClass));
    	
	}
	
	@Override
	public AbstractBaseResult<DropDownResult> dropDownResult(Long id){
		return success(dropDown(id));
	}
	
	public AbstractBaseResult<List<DropDownResult>> dropDownListResult(){
		return success(dropDownList());
	}
	
	@Override
	public AbstractBaseResult<List<DropDownResult>> dropDownListResult(Collection<Long> ids){
		
		MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
    	
    	wrapper.select(getColumns(AbstractBaseDomain::getId,AbstractBaseDomain::getName));
    	
    	wrapper.in(getColumn(AbstractBaseDomain::getId), ids);
    	
    	return success(copyList(list(wrapper), DropDownResult::new));
		
	}
	
	
	public AbstractBaseResult<Void> updateStatus(Long id){
		
		checkBind(id);
		
		T domain = getByIdThrowError(id,getColumns(AbstractBaseDomain::getStatus));
		
		domain.setId(id);
		domain.setStatus(domain.getStatus() == Status.enable ? Status.disable : Status.enable);
		
		updateById(domain);
		
		return success(null);
		
	}

}
