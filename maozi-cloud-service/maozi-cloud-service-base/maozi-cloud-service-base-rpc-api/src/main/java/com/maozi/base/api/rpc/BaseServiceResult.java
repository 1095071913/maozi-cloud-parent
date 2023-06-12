package com.maozi.base.api.rpc;

import java.util.Collection;
import java.util.List;

import com.maozi.base.AbstractBaseDtomain;
import com.maozi.base.param.SaveUpdateBatch;
import com.maozi.base.result.DropDownResult;
import com.maozi.common.result.AbstractBaseResult;

public interface BaseServiceResult<D> {
	
	AbstractBaseResult<D> getByIdResult(Long id,String ... colums);

	AbstractBaseResult<List<D>> listByIdsResult(List<Long> ids,String ... colums);

	AbstractBaseResult<Long> getCountByParamResult(D dto);

	<D extends AbstractBaseDtomain> AbstractBaseResult<Long> saveUpdateResult(Long id,D param);

	AbstractBaseResult<Void> saveUpdateBatchResult(List<SaveUpdateBatch> dtos);

	AbstractBaseResult<Void> removeByIdResult(Long id);
	
	AbstractBaseResult<Void> removeByIdBatchResult(List<Long> ids);
	
	AbstractBaseResult<Void> checkAvailableResult(Long id);
	
	AbstractBaseResult<Void> checkAvailableResult(List<Long> ids);
	
	AbstractBaseResult<D> getAvailableByIdResult(Long id,String ... columns);
	
	AbstractBaseResult<DropDownResult> dropDownResult(Long id);
	
	AbstractBaseResult<List<DropDownResult>> dropDownListResult(Collection<Long> ids);

}
