package com.maozi.base.api.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.cglib.CglibUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.base.MPJBaseService;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.AbstractBaseDomain;
import com.maozi.base.AbstractBaseNameDomain;
import com.maozi.base.api.IBaseMapper;
import com.maozi.base.api.rpc.BaseServiceResult;
import com.maozi.base.constant.ResultFunName;
import com.maozi.base.enums.Status;
import com.maozi.base.enums.StoreClassType;
import com.maozi.base.param.PageParam;
import com.maozi.base.param.SaveUpdateBatch;
import com.maozi.base.param.plugin.OrderParam;
import com.maozi.base.param.plugin.TimeParam;
import com.maozi.base.plugin.StoreClass;
import com.maozi.base.plugin.context.QueryEnvironmentContext;
import com.maozi.base.plugin.join.JoinBaseType;
import com.maozi.base.plugin.join.JoinPlugins;
import com.maozi.base.plugin.mapping.QueryMapping;
import com.maozi.base.plugin.query.QueryBaseType;
import com.maozi.base.plugin.query.QueryPlugin;
import com.maozi.base.plugin.type.JoinType;
import com.maozi.base.plugin.type.QueryType;
import com.maozi.base.result.DropDownResult;
import com.maozi.base.result.PageResult;
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ReflectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.SerializeUtil;
import com.maozi.common.ValidatorUtil;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<M extends IBaseMapper<T>, T extends AbstractBaseDomain,D> extends ServiceImpl<M, T> implements MPJBaseService<T>,BaseServiceResult<D> {

	protected Class<T> domainClass;
	
	protected Class<D> resultClass;
	
	protected abstract String getResourceName();

	@SuppressWarnings("unchecked")
	public BaseServiceImpl() {

		for(Class<?> superClass = this.getClass() ;; superClass = superClass.getSuperclass()) {

		Type genericSuperclass = superClass.getGenericSuperclass();

			if(genericSuperclass instanceof ParameterizedType type) {

				domainClass = (Class<T>) type.getActualTypeArguments()[1];

				resultClass = (Class<D>) type.getActualTypeArguments()[2];

				break;

			}

		}

	}

    @Override
    public Class<T> currentModelClass() {
        return super.currentModelClass();
    }
    
    public Class<D> getResultClass(){

		if(resultClass.getName().equals(Void.class.getName())) {
			throw new BusinessResultException(SystemErrorCode.NOT_SET_RESPONSE_ERROR).setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);
		}
    	
    	return resultClass;
    
    }
    
    protected <R> com.baomidou.mybatisplus.extension.plugins.pagination.Page<R> convertPage(PageParam<?> pageParam){
    	return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageParam.getCurrent(),pageParam.getSize());
    }
    
    protected <R> PageResult<R> convertPageResult(Page<T> page,Supplier<R> target) {
		return new PageResult<>(page.getCurrent(),page.getSize(),page.getTotal(),CglibUtil.copyList(page.getRecords(),target));
	}
    
    protected <R> PageResult<R> convertPageResult(Page<T> page,List<R> responseData) {
		return new PageResult<>(page.getCurrent(),page.getSize(),page.getTotal(),responseData);
	}

	protected <R> PageResult<R> convertPageResult(Page<R> page) {
		return new PageResult<>(page.getCurrent(),page.getSize(),page.getTotal(),page.getRecords());
	}
    
    protected <R> Map<Long,R> toMapByIds(List<R> collection,Function<R, Long> function) {
    	return collection.stream().collect(Collectors.toMap(function, Function.identity()));
	}

	@SneakyThrows
    protected <O> String getColumn(SFunction<O, ?> sfunction) {

		Method method = sfunction.getClass().getDeclaredMethod(SerializeUtil.WRITE_REPLACE_FIELD_NAME);

		method.setAccessible(true);

		SerializedLambda serializedLambda = (SerializedLambda) method.invoke(sfunction);

		String fieldWithGet = serializedLambda.getImplMethodName();

		String propertyName = StrUtil.toUnderlineCase(fieldWithGet.substring(3));

		TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
		ObjectUtil.checkConditionThrowError(ObjectUtil.isNotNullEmpty(tableInfo),"领域模型映射关系不存在");

		if (tableInfo.getKeyProperty().equals(propertyName)) {
			return tableInfo.getKeyColumn();
		}

		for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
			if (fieldInfo.getProperty().equals(propertyName)) {
				return fieldInfo.getColumn();
			}
		}

		return propertyName;

	}


    @SafeVarargs
    protected final <O> String[] getColumns(SFunction<O, ?>... functions) {

    	String [] columns = new String[functions.length];

		int i = 0;
    	for(SFunction<?, ?> sfunction : functions) {

			columns[i] = getColumn(sfunction);

	        ++i;

		}

    	return columns;

	}

	protected String[] getColumns(Class<?> clazz) {

		List<String> columns = CollectionUtil.newArrayList();

		for( ; !Object.class.getName().equals(clazz.getName()); clazz = clazz.getSuperclass()){

			Field [] fields = clazz.getDeclaredFields();
			for(Field field : fields) {

				if(SerializeUtil.SERIAL_VERSION_UID_FIELD_NAME.equals(field.getName())){
					continue;
				}

				QueryMapping annotation = field.getAnnotation(QueryMapping.class);
				if(ObjectUtil.isNullEmpty(annotation)) {
					columns.add(QueryEnvironmentContext.DEFAULT_ORDER_KEY + "." + StrUtil.toUnderlineCase(field.getName()));
				}else if(annotation.ignore() && (StringUtils.isNotBlank(annotation.field()) || StringUtils.isNotBlank(annotation.tableName()))){

					String tableName = StringUtils.isNotBlank(annotation.field()) ? annotation.field() : QueryEnvironmentContext.DEFAULT_ORDER_KEY;

					String fieldName = StringUtils.isNotBlank(annotation.tableName()) ?
							annotation.tableName()
							:
							StrUtil.toUnderlineCase(field.getName());

					columns.add(fieldName + "." + tableName);

				}

			}

		}

		return columns.toArray(new String[0]);

	}
    
    protected T getById(Long id,String ... columns){

		ObjectUtil.isNullEmptyThrowError(id, getResourceName());

		QueryWrapper<T> wrapper = Wrappers.query();
		if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	return getOne(wrapper);
    	
	}
    
    @SafeVarargs
    protected final T getById(Long id, SFunction<T, ?>... columns){

		ObjectUtil.isNullEmptyThrowError(id, getResourceName());

		LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
		if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.eq(AbstractBaseDomain::getId,id);
    	
    	return getOne(wrapper);
    	
	}
    
    protected void checkAvailable(Long id){

		ObjectUtil.isNullEmptyThrowError(id, getResourceName());

		QueryWrapper<T> wrapper = Wrappers.query();

		wrapper.select(getColumn(AbstractBaseDomain::getStatus));
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain, getResourceName());

		if(domain.getStatus() == Status.DISABLE) {
			throw new BusinessResultException(SystemErrorCode.FORBIDDEN_ERROR)
					.setResource(getResourceName());
		}
    	
	}
    
    protected void checkAvailable(List<Long> ids){
    	
    	CollectionUtil.collectionIsEmptyThrowError(ids,getResourceName() + "列表");

		QueryWrapper<T> wrapper = Wrappers.query();

		wrapper.select(getColumn(AbstractBaseDomain::getStatus));
    	
    	wrapper.in(getColumn(AbstractBaseDomain::getId),ids);
    	
    	List<T> domains = list(wrapper);
    	domains.parallelStream().forEach((domain)->{
    		
    		if(domain.getStatus() == Status.DISABLE) {
				throw new BusinessResultException(SystemErrorCode.FORBIDDEN_ERROR)
						.setResource(getResourceName());
        	}
    		
    	});
    	
	}

	@SafeVarargs
    protected final T getAvailableById(Long id, SFunction<T, ?>... columns){
		return getAvailableById(id,getColumns(columns));
	}
    
    protected T getAvailableById(Long id,String ... columns){

		ObjectUtil.isNullEmptyThrowError(id, getResourceName());

		QueryWrapper<T> wrapper = Wrappers.query();

		String[] newColumns = new String[columns.length + 1];
		System.arraycopy(columns, 0, newColumns, 0, columns.length);
		newColumns[columns.length] = getColumn(AbstractBaseDomain::getStatus);
    	wrapper.select(newColumns);
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain, getResourceName());
    	
    	if(domain.getStatus() == Status.DISABLE) {
			throw new BusinessResultException(SystemErrorCode.FORBIDDEN_ERROR)
						.setResource(getResourceName());
    	}
    	
    	return domain;
    	
	}
    
    protected T getAvailableByParam(MPJLambdaWrapper<T> wrapper){

		wrapper.selectAsClass(domainClass,domainClass);
    	wrapper.select(getColumn(AbstractBaseDomain::getStatus));
    	
    	T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain, getResourceName());
    	
    	if(domain.getStatus() == Status.DISABLE) {
			throw new BusinessResultException(SystemErrorCode.FORBIDDEN_ERROR)
					.setResource(getResourceName());
    	}
    	
    	return domain;
    	
	}
    
    protected void checkHas(Wrapper<T> wrapper) {
		ErrorCode errorCode = SystemErrorCode.DATA_NOT_EXIST_ERROR;
		ObjectUtil.checkConditionThrowError(count(wrapper) > 0, errorCode, getResourceName() + errorCode.getMessage());
	}
    
    protected void checkNotHas(Wrapper<T> wrapper) {
		ErrorCode errorCode = SystemErrorCode.DATA_EXIST_ERROR;
		ObjectUtil.checkConditionThrowError(count(wrapper) < 1, errorCode, getResourceName() + errorCode.getMessage());
	}
    
    protected DropDownResult dropDown(Long id){

		ObjectUtil.isNullEmptyThrowError(id, getResourceName());

		QueryWrapper<T> wrapper = Wrappers.query();

		wrapper.select(getColumns(AbstractBaseNameDomain::getId,AbstractBaseNameDomain::getName));
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId), id);
    	
    	T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain, getResourceName());
    	
    	return CglibUtil.copy(domain, DropDownResult.class);
    	
	}
    
    protected List<DropDownResult> dropDownList(){

		QueryWrapper<T> wrapper = Wrappers.query();

		wrapper.select(getColumns(AbstractBaseNameDomain::getId,AbstractBaseNameDomain::getName));
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getStatus), Status.ENABLE);
    	
    	return CglibUtil.copyList(list(wrapper), DropDownResult::new);
    	
	}
    
    protected T getByIdThrowError(Long id,String ... columns){

		ObjectUtil.isNullEmptyThrowError(id, getResourceName());

		QueryWrapper<T> wrapper = Wrappers.query();

		if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.eq(getColumn(AbstractBaseDomain::getId),id);
    	
    	T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain,getResourceName());
    	
    	return domain;
    	
	}

	protected T getByIdThrowError(Long id,MPJLambdaWrapper<T> wrapper){

		ObjectUtil.isNullEmptyThrowError(id, getResourceName());

		wrapper.eq(getColumn(AbstractBaseDomain::getId),id);

		T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain,getResourceName());

		return domain;

	}
    
    protected <R> R getByParamThrowError(Wrapper<T> wrapper,Class<R> clazz) {
		
    	T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain, getResourceName());
		
		return CglibUtil.copy(domain, clazz);
    	
	}
    
    protected <R> R getByParamThrowErrorRelation(Wrapper<T> wrapper,Class<R> clazz) {
		
    	T domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain, getResourceName());
		
		R response = CglibUtil.copy(domain, clazz);
		
		setRelationData(response, clazz);
		
		return response;
    	
	}
    
    @SafeVarargs
    protected final <R> T getByIdThrowError(Long id, SFunction<R, ?> ... columns) {
    	return getByIdThrowError(id,getColumns(columns));
	}
    
    @SafeVarargs
    protected final <V,R> V getByIdThrowError(Long id, Class<V> clazz, SFunction<R, ?> ... columns){
    	return CglibUtil.copy(getByIdThrowError(id,columns), clazz);
	}
    
    protected <V> V getByIdThrowErrorRelation(Long id,Class<V> clazz) {

		V response = CglibUtil.copy(getByIdThrowError(id,getColumns(clazz)), clazz);
    	
    	setRelationData(response, clazz);
    	
    	return response;
    	
	}

	protected <V,R> V getByParamThrowErrorRelation(R dto,Class<V> clazz) {

		ObjectUtil.isNullEmptyThrowError(dto, getResourceName());

		MPJLambdaWrapper<T> wrapper = buildQueryWrapper(dto,clazz);

		wrapper.select(getColumns(clazz));

		V response = CglibUtil.copy(getOne(wrapper), clazz);

		ObjectUtil.isNullEmptyThrowError(response, getResourceName());

		setRelationData(response, clazz);

		return response;

	}
    
    protected List<T> listByIds(Collection<?> ids,String ... columns) {

		CollectionUtil.collectionIsEmptyThrowError(ids,getResourceName() + "列表");

		QueryWrapper<T> wrapper = Wrappers.query();
		if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	
    	wrapper.in(getColumn(AbstractBaseDomain::getId),ids);
    	
    	return list(wrapper);
    	
	}
    
    @SafeVarargs
    protected final <R,V> List<V> list(Class<V> voClass, SFunction<R, ?>... columns) {

    	MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
		wrapper.selectAsClass(domainClass,voClass);

		if(columns.length > 0) {
    		wrapper.select(columns);
    	}

		return selectJoinList(voClass, wrapper);
    	
	}
    
    protected <R> List<R> list(Wrapper<T> wrapper,Supplier<R> target) {
		
    	List<T> list = list(wrapper);
    	
    	return CglibUtil.copyList(list, target);
    	
	}

	protected <V> List<V> list(D dto,Supplier<V> target) {

		ObjectUtil.isNullEmptyThrowError(dto, getResourceName());

		V vo = target.get();

		List<T> list = list(buildQueryWrapper(dto, vo.getClass()));

		return CglibUtil.copyList(list, () -> vo);

	}

	protected <V> List<V> listRelation(D dto,Supplier<V> target) {

		ObjectUtil.isNullEmptyThrowError(dto, getResourceName());

		V vo = target.get();

		List<T> list = list(buildQueryWrapper(dto,vo.getClass()));

		setRelationData(dto,vo.getClass());

		return CglibUtil.copyList(list, () -> vo);

	}
    
    protected <V> PageResult<V> list(PageParam<?> pageParam,Wrapper<T> wrapper,Supplier<V> target) {
		
    	Page<T> page = page(convertPage(pageParam),wrapper);
    	
    	return convertPageResult(page, target);
    	
	}
    
    protected <V> PageResult<V> list(PageParam<?> pageParam,Class<V> clazz) {

		Page<V> page = selectJoinListPage(convertPage(pageParam),clazz,buildQueryWrapper(pageParam.getData(),clazz));
    	
    	return convertPageResult(page);
    	
	}
    
    protected Page<T> list(PageParam<?> pageParam,Wrapper<T> wrapper) {
		return page(convertPage(pageParam),wrapper);
	}
    
    protected <V> PageResult<V> listRelation(PageParam<?> pageParam,Class<V> clazz) {
    	
    	PageResult<V> page = list(pageParam,clazz);
		
		setRelationData(page, clazz);
		
		return page;
		
	}

	protected <P> Long getCountByParam(P param){

		ObjectUtil.isNullEmptyThrowError(param,getResourceName());

		return count(buildQueryWrapper(param));

	}
    
    public boolean saveUpdate(T domain) {

		ObjectUtil.isNullEmptyThrowError(domain,getResourceName());
    	
    	if(ObjectUtil.isNotNullEmpty(domain.getId())) {

			QueryWrapper<T> wrapper = Wrappers.query();

			wrapper.eq(getColumn(AbstractBaseDomain::getId), domain.getId());

			ErrorCode errorCode = SystemErrorCode.DATA_NOT_EXIST_ERROR;
			ObjectUtil.checkConditionThrowError(count(wrapper) > 0, errorCode, getResourceName() + errorCode.getMessage());

    	}
    	
		return super.saveOrUpdate(domain);
		
	}
    
	public Long saveUpdate(Long id,Object param) {

		ObjectUtil.isNullEmptyThrowError(param,getResourceName());
    	
    	T domain = CglibUtil.copy(param, domainClass);
    	
    	if(ObjectUtil.isNotNullEmpty(id)) {
    		
    		domain.setId(id);

			QueryWrapper<T> wrapper = Wrappers.query();
			wrapper.eq(getColumn(AbstractBaseDomain::getId), domain.getId());

			ErrorCode errorCode = SystemErrorCode.DATA_NOT_EXIST_ERROR;
			ObjectUtil.checkConditionThrowError(count(wrapper) > 0, errorCode, getResourceName() + errorCode.getMessage());

    	}else {
    		ValidatorUtil.validate(param);
    	}
    	
    	super.saveOrUpdate(domain);
    	
    	return domain.getId();
		
	}
	
    public void saveUpdateBatch(List<T> domains) {

		CollectionUtil.collectionIsEmptyThrowError(domains,getResourceName() + "列表");

		Consumer<T> consumer = ApplicationLinkContext.wrapConsumer((domain)->{

			if(ObjectUtil.isNotNullEmpty(domain.getId())) {

				QueryWrapper<T> wrapper = Wrappers.query();
				wrapper.eq(getColumn(AbstractBaseDomain::getId), domain.getId());

				ErrorCode errorCode = SystemErrorCode.DATA_NOT_EXIST_ERROR;
				ObjectUtil.checkConditionThrowError(count(wrapper) > 0, errorCode, getResourceName() + errorCode.getMessage());

			}

			super.saveOrUpdate(domain);

		});

		domains.parallelStream().forEach(Objects.requireNonNull(consumer));

    }
    
    protected void checkBind(Long id) {};
    
    protected void unbind(Long id) {};
    
    public void removeById(Long id) {

		ObjectUtil.isNullEmptyThrowError(id,getResourceName());
		
		checkBind(id);
		
		unbind(id);
		
		super.removeById(id);
    	
    }





























//	========================== 结果集封装 ==========================

    @Override
    public AbstractBaseResult<D> getByIdResult(Long id,String ... columns) {
		T domain = getById(id, columns);
		return ResultUtil.success(CglibUtil.copy(domain, getResultClass()));
	}

    @Override
	public AbstractBaseResult<Map<Long,D>> listByIdsResult(Collection<Long> ids,String ... columns) {

		List<T> domains = listByIds(ids, columns);

		List<D> responseItems = CglibUtil.copyList(domains, () -> ReflectUtil.newInstance(getResultClass()));

		return ResultUtil.success(

			responseItems.stream().collect(

				Collectors.toMap(
					ReflectUtil::invokeFunGetId
					,
					Function.identity()
				)

			)
		);

	}
    
    @Override
	public AbstractBaseResult<Map<Long,List<D>>> listByRelationIdsResult(Collection<Long> ids,String relationField,String ... columns) {

		CollectionUtil.collectionIsEmptyThrowError(ids,getResourceName() + "列表");

		ObjectUtil.isNullEmptyThrowError(relationField, getResourceName() + "分组");

		QueryWrapper<T> wrapper = Wrappers.query();
		if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	wrapper.in(relationField,ids);

		List<T> domains = list(wrapper);
		if(ObjectUtil.isNullEmpty(domains)) {
			return ResultUtil.success(CollectionUtil.newHashMap());
		}

		List<D> responseItems = CglibUtil.copyList(domains, () -> ReflectUtil.newInstance(getResultClass()));
		return ResultUtil.success(

			responseItems.stream().collect(
				Collectors.groupingBy((resultData) ->
					ReflectUtil.invokeGet(resultData, relationField), Collectors.toList()
				)
			)

		);

	}
    
    @Override
	public AbstractBaseResult<List<D>> listByRelationIdResult(Long id,String relationField,String ... columns) {

		ObjectUtil.isNullEmptyThrowError(id,getResourceName());

		ObjectUtil.isNullEmptyThrowError(relationField, getResourceName() + "分组");

		QueryWrapper<T> wrapper = Wrappers.query();
		if(columns.length > 0) {
    		wrapper.select(columns);
    	}
    	wrapper.eq(relationField,id);

		return ResultUtil.success(CglibUtil.copyList(list(wrapper), () -> ReflectUtil.newInstance(getResultClass())));

	}
	
    @Override
	public AbstractBaseResult<Long> getCountByParamResult(D dto){
		return ResultUtil.success(getCountByParam(dto));
	}
	
	@Override
	public <P> AbstractBaseResult<Long> saveUpdateResult(Long id, P param){
		return ResultUtil.success(saveUpdate(id,param));
	}
	
	@Override
	public AbstractBaseResult<Void> saveUpdateBatchResult(List<SaveUpdateBatch> collection){

		CollectionUtil.collectionIsEmptyThrowError(collection, getResourceName() + "列表");

		List<T> domains = CglibUtil.copyList(collection, ()-> ReflectUtil.newInstance(domainClass));
		saveUpdateBatch(domains);

		return ResultUtil.success();
		
	}
	
	@Override
	public AbstractBaseResult<Void> removeByIdResult(Long id) {
		removeById(id);
		return ResultUtil.success();
	}

	@Override
	public AbstractBaseResult<Void> removeByIdBatchResult(List<Long> ids) {
		
		CollectionUtil.collectionIsEmptyThrowError(ids, getResourceName() + "列表");
		
		ids.parallelStream().forEach(this::removeById);
		
		return ResultUtil.success();
		
	}
	
	@Override
	public AbstractBaseResult<Void> checkAvailableResult(Long id){
		checkAvailable(id);
		return ResultUtil.success();
	}
	
	@Override
	public AbstractBaseResult<Void> checkAvailableResult(List<Long> ids){
		checkAvailable(ids);
		return ResultUtil.success();
	}
	
	@Override
	public AbstractBaseResult<D> getAvailableByIdResult(Long id,String ... columns){
    	
    	T domain = getAvailableById(id, columns);
    	
    	return ResultUtil.success(CglibUtil.copy(domain, getResultClass()));
    	
	}
	
	@Override
	public AbstractBaseResult<DropDownResult> dropDownResult(Long id){
		return ResultUtil.success(dropDown(id));
	}
	
	public AbstractBaseResult<List<DropDownResult>> dropDownListResult(){
		return ResultUtil.success(dropDownList());
	}
	
	@Override
	public AbstractBaseResult<List<DropDownResult>> dropDownListResult(Collection<Long> ids){

		QueryWrapper<T> wrapper = Wrappers.query();

		wrapper.select(getColumns(AbstractBaseNameDomain::getId,AbstractBaseNameDomain::getName));
    	
    	wrapper.in(getColumn(AbstractBaseDomain::getId), ids);
    	
    	return ResultUtil.success(CglibUtil.copyList(list(wrapper), DropDownResult::new));
		
	}

	@SneakyThrows
	public AbstractBaseResult<Void> updateStatus(Long id, Status status){

		ObjectUtil.isNullEmptyThrowError(status,SystemErrorCode.PARAM_ERROR,"状态");
		
		checkBind(id);

		T domain = domainClass.getDeclaredConstructor().newInstance();

		domain.setId(id);
		domain.setStatus(status);
		
		updateById(domain);

		return ResultUtil.success();
		
	}










//	====================== 工具方法 ======================

	private void paramFieldSetWrapper(String tableName, Object param, MPJLambdaWrapper<T> wrapper) {

		Class<?> paramClass = param.getClass();

		if (paramClass.isAnnotationPresent(JoinPlugins.class)) {

			JoinPlugins annotations = paramClass.getAnnotation(JoinPlugins.class);

			if(ObjectUtil.isNotNullEmpty(annotations.value())){

				Arrays.stream(annotations.value()).forEach(annotation -> {

					JoinBaseType value = annotation.value();

					ObjectUtil.isNullEmptyThrowError(value, getResourceName() + "连接类型");

					ObjectUtil.isNullEmptyThrowError(value.getType(), getResourceName() + "连接类型");

					JoinType joinType = JoinType.get(value.getType());

					ObjectUtil.isNullEmptyThrowError(joinType, getResourceName() + "连接类型");

					Objects.requireNonNull(joinType).getJoinPlugin().apply(getResourceName(), wrapper, annotation);

				});

			}

		}

		Field [] fields = ReflectUtil.getFields(param.getClass());

		for(Field field : fields) {

			if(SerializeUtil.SERIAL_VERSION_UID_FIELD_NAME.equals(field.getName())){
				continue;
			}

			Object data = ReflectUtil.invokeGet(param, field.getName());
			if(ObjectUtil.isNullEmpty(data)) {continue;}

			QueryPlugin annotation = field.getAnnotation(QueryPlugin.class);

			String fieldName = ObjectUtil.isNotNullEmpty(annotation) && StringUtils.isNotBlank(annotation.field()) ?
					annotation.field()
					:
					field.getName();

			if(ObjectUtil.isNotNullEmpty(annotation)) {

				if(annotation.nest()) {

					paramFieldSetWrapper(annotation.tableName(),field,wrapper);

					continue;

				}

				QueryBaseType value = annotation.value();

				ObjectUtil.isNullEmptyThrowError(value, getResourceName() + "查询类型");

				ObjectUtil.isNullEmptyThrowError(value.getType(), getResourceName() + "查询类型");

				QueryType queryType = QueryType.get(value.getType());

				ObjectUtil.isNullEmptyThrowError(queryType, getResourceName() + "查询类型");

                if(StringUtils.isNotBlank(annotation.tableName())){
                    tableName = annotation.tableName();
                }

				if(StringUtils.isNotBlank(tableName)){
					fieldName = tableName + "." + fieldName;
				}

				Objects.requireNonNull(queryType).getQueryPlugin().apply(wrapper, fieldName, data);

			}

			if(data instanceof TimeParam timeParam) {

				if(ObjectUtil.isNotNullEmpty(timeParam.getStartTime())) {
					QueryType.GE.getQueryPlugin().apply(wrapper, fieldName, timeParam.getStartTime());
				}

				if(ObjectUtil.isNotNullEmpty(timeParam.getEndTime())) {
					QueryType.LE.getQueryPlugin().apply(wrapper, fieldName, timeParam.getEndTime());
				}

			}


		}

	}

	protected void setQueryMappingWrapper(Class<?> clazz, MPJLambdaWrapper<T> wrapper){

		for(;!Object.class.getName().equals(clazz.getName());clazz = clazz.getSuperclass()){

			Field [] fields = clazz.getDeclaredFields();

			for(Field field : fields) {

				QueryMapping annotation = field.getAnnotation(QueryMapping.class);
				if(ObjectUtil.isNotNullEmpty(annotation) && !annotation.ignore() && !annotation.isService()) {

					Class<?> type = field.getType();

					String tableName = annotation.tableName();
					ObjectUtil.checkConditionThrowError(ObjectUtil.isNotNullEmpty(tableName),getResourceName() + "映射关系名称");

					Class<?> relationClazz = StoreClass.storeClassMap.get(StoreClassType.DB).get(tableName);
					ObjectUtil.checkConditionThrowError(ObjectUtil.isNotNullEmpty(relationClazz),getResourceName() + "映射关系不存在");

					ObjectUtil.checkConditionThrowError(!type.isPrimitive(),getResourceName() + "映射关系类型错误");

					if(type.equals(List.class)){
						wrapper.selectCollection(relationClazz,(response) -> ReflectUtil.invokeGet(response, field.getName()));
					}else{
						wrapper.selectAssociation(relationClazz,(response) -> ReflectUtil.invokeGet(response, field.getName()));
					}

				}

			}

		}

	}

	public MPJLambdaWrapper<T> buildQueryWrapper(Object param,Class<?> fieldsClass) {

		if(param instanceof PageParam<?> pageParam) {
			param = pageParam.getData();
		}

		ObjectUtil.isNullEmptyThrowError(param, getResourceName() + "参数");

		MPJLambdaWrapper<T> wrapper = MPJWrappers.lambdaJoin();
		wrapper.selectAsClass(domainClass, fieldsClass);

		if(ObjectUtil.isNotNullEmpty(fieldsClass)) {

			wrapper.select(getColumns(fieldsClass));

//			setQueryMappingWrapper(fieldsClass,wrapper);

		}

		paramFieldSetWrapper(null,param,wrapper);

		if(param instanceof OrderParam orderParam) {

			orderParam.initOrderParam();

			List<String> asc = orderParam.getOrderAscFieldsMap().get(QueryEnvironmentContext.DEFAULT_ORDER_KEY);

			List<String> desc = orderParam.getOrderDescFieldsMap().get(QueryEnvironmentContext.DEFAULT_ORDER_KEY);

			wrapper.orderByAscStr(ObjectUtil.isNotNullEmpty(asc),asc);

			wrapper.orderByDescStr(ObjectUtil.isNotNullEmpty(desc),desc);

		}

		return wrapper;

	}

	public MPJLambdaWrapper<T> buildQueryWrapper(Object param) {
		return buildQueryWrapper(param,null);
	}

	public void setRelationData(Object data,Class<?> clazz){

		if(data instanceof PageResult<?> pageResult) {
			data = pageResult.getData();
		}

		if(data instanceof List<?> responses) {

			if(ObjectUtil.isNotNullEmpty(responses)) {

				Field [] fields = ReflectUtil.getFields(clazz);
				for(Field field : fields) {

					QueryMapping annotation = field.getAnnotation(QueryMapping.class);
					if(ObjectUtil.isNullEmpty(annotation) || annotation.ignore()) {
						continue;
					}

					String functionName = annotation.functionName();
					String relationFieldName = annotation.relationField();
					ObjectUtil.checkConditionThrowError(ObjectUtil.isNotNullEmpty(relationFieldName) || ObjectUtil.isNotNullEmpty(functionName),getResourceName() + "关系字段未设置");

					if(annotation.isService()) {

						Object service = SpringUtil.getBean(annotation.serviceName());
						ObjectUtil.checkConditionThrowError(ObjectUtil.isNotNullEmpty(service),getResourceName() + "服务不存在");

						Class<?> type = field.getType();
						if(StringUtils.isNotBlank(functionName)) {

							Set<Object> param = CollectionUtil.newHashSet();
							for(Object response : responses) {
								param.add(ReflectUtil.invokeGet(response,relationFieldName));
							}

							if(ObjectUtil.isNotNullEmpty(param)) {

								Map<Long,Object> result = ReflectUtil.invoke(service,functionName,param,relationFieldName);
								for(Object response : responses) {

									Long id = ReflectUtil.invokeGet(response,relationFieldName);

									Object object = result.get(id);
									if(ObjectUtil.isNotNullEmpty(object)) {
										ReflectUtil.invokeSet(response,field.getName(),object);
									}

								}

							}

						}else if(type.equals(List.class)) {

							Set<Long> relationIds = CollectionUtil.newHashSet();
							for(Object response : responses) {
								relationIds.add(ReflectUtil.invokeFunGetId(response));
							}

							if(ObjectUtil.isNotNullEmpty(relationIds)) {

								AbstractBaseResult<Map<Long,List<?>>> rpcResult = ReflectUtil.invoke(service, ResultFunName.LIST_BY_RELATION_IDS_RESULT, relationIds, relationFieldName,getColumns(field.getClass()));

								Map<Long,List<?>> resultData = rpcResult.getResultDataThrowError();
								for(Object response : responses) {

									Long id = ReflectUtil.invokeFunGetId(response);

									List<?> list = resultData.get(id);
									if(ObjectUtil.isNotNullEmpty(list)) {
										ReflectUtil.invokeSet(response,field.getName(),list);
									}

								}

							}

						}else if(type.equals(DropDownResult.class)) {

							Set<Long> relationIds = CollectionUtil.newHashSet();
							for(Object response : responses) {
								relationIds.add(ReflectUtil.invokeGet(response,relationFieldName));
							}

							if(ObjectUtil.isNotNullEmpty(relationIds)) {

								AbstractBaseResult<List<DropDownResult>> rpcResult = ReflectUtil.invoke(service, ResultFunName.DROP_DOWN_LIST_RESULT,relationIds);

								List<DropDownResult> resultData = rpcResult.getResultDataThrowError();

								Map<Long, DropDownResult> relationsMap = toMapByIds(resultData,DropDownResult::getId);
								for(Object response : responses) {

									Long id = ReflectUtil.invokeGet(response,relationFieldName);;

									DropDownResult relation = relationsMap.get(id);
									if(ObjectUtil.isNotNullEmpty(relation)) {
										ReflectUtil.invokeSet(response,field.getName(),relation);
									}

								}

							}

						}else {

							Set<Long> relationIds = CollectionUtil.newHashSet();
							for(Object response : responses) {
								ReflectUtil.invokeGet(response,relationFieldName);
							}

							if(ObjectUtil.isNotNullEmpty(relationIds)) {

								AbstractBaseResult<Map<Long,Object>> rpcResult = ReflectUtil.invoke(service,ResultFunName.LIST_BY_IDS_RESULT,relationIds,getColumns(field.getClass()));

								Map<Long,Object> resultData = rpcResult.getResultDataThrowError();
								for(Object response : responses) {

									Long id = ReflectUtil.invokeGet(response,relationFieldName);

									Object relation =  resultData.get(id);
									if(ObjectUtil.isNotNullEmpty(relation)) {
										ReflectUtil.invokeSet(response,field.getName(),relation);
									}

								}

							}

						}

					}

				}

			}

		}else {

			Field [] fields = ReflectUtil.getFields(clazz);
			for(Field field : fields) {

				QueryMapping annotation = field.getAnnotation(QueryMapping.class);
				if(ObjectUtil.isNullEmpty(annotation) || annotation.ignore()) {
					continue;
				}

				String functionName = annotation.functionName();
				String relationFieldName = annotation.relationField();
				ObjectUtil.checkConditionThrowError(ObjectUtil.isNotNullEmpty(relationFieldName) || ObjectUtil.isNotNullEmpty(functionName), getResourceName() + "关系字段未设置");

				if(annotation.isService()) {

					Object service = SpringUtil.getBean(annotation.serviceName());
					ObjectUtil.checkConditionThrowError(ObjectUtil.isNotNullEmpty(service), getResourceName() + "服务不存在");

					Class<?> type = field.getType();
					if(StringUtils.isNotBlank(functionName)) {

						Object result = null;
						if(StringUtils.isNotBlank(relationFieldName)) {

							var param = ReflectUtil.invokeGet(data, relationFieldName);

							result = ReflectUtil.invoke(service,functionName,param);

						}else{
							result = ReflectUtil.invoke(service,functionName);
						}

						if(ObjectUtil.isNotNullEmpty(result)) {
							ReflectUtil.invokeSet(data, field.getName(),result);
						}

					}else if(type.equals(List.class)) {

						Long id = ReflectUtil.invokeFunGetId(data);
						AbstractBaseResult<List<?>> rpcResult = ReflectUtil.invoke(service, ResultFunName.LIST_BY_RELATION_ID_RESULT,id,relationFieldName,getColumns(field.getClass()));

						List<?> resultData = rpcResult.getResultDataThrowError();
						if(ObjectUtil.isNotNullEmpty(resultData)) {
							ReflectUtil.invokeSet(data, field.getName(),resultData);
						}

					}else if(type.equals(DropDownResult.class)) {

						Long relationId = ReflectUtil.invokeGet(data, relationFieldName);
						if(ObjectUtil.isNotNullEmpty(relationId)) {

							AbstractBaseResult<DropDownResult> rpcResult = ReflectUtil.invoke(service, ResultFunName.DROP_DOWN_RESULT,relationId);

							DropDownResult resultData = rpcResult.getResultDataThrowError();
							if(ObjectUtil.isNotNullEmpty(resultData)) {
								ReflectUtil.invokeSet(data, field.getName(),resultData);
							}

						}

					}else {

						Long relationId = ReflectUtil.invokeGet(data, relationFieldName);
						if(ObjectUtil.isNotNullEmpty(relationId)) {

							AbstractBaseResult<Object> rpcResult = ReflectUtil.invoke(service,ResultFunName.GET_BY_ID_RESULT, relationId,getColumns(field.getClass()));

							Object resultData = rpcResult.getResultDataThrowError();
							if(ObjectUtil.isNotNullEmpty(resultData)) {
								ReflectUtil.invokeSet(data, field.getName(),resultData);
							}

						}

					}

				}

			}

		}

	}
	
}