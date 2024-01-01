package com.maozi.base.api.rpc;

import com.maozi.base.AbstractBaseDtomain;
import com.maozi.base.param.SaveUpdateBatch;
import com.maozi.base.result.DropDownResult;
import com.maozi.common.result.AbstractBaseResult;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BaseServiceResult<D> {
	
	AbstractBaseResult<D> getByIdResult(Long id,String ... columns);

	AbstractBaseResult<Map<Long,D>> listByIdsResult(Collection<Long> ids,String ... columns);	
	
	AbstractBaseResult<List<D>> listByRelationIdResult(Long id,String relationField,String ... columns);
	
	AbstractBaseResult<Map<Long,List<D>>> listByRelationIdsResult(Collection<Long> ids,String relationField,String ... columns);

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
