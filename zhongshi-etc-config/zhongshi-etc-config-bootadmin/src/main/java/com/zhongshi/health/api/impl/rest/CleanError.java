package com.zhongshi.health.api.impl.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping("/Application/BootAdmin")
@RestController
@Api("清理Bootadmin心跳错误")
public class CleanError extends BaseResultFactory {

	@PostMapping("/cleanAll")
	@ApiOperation(value = "清理全部错误")
	public AbstractBaseResult<?> cleanAll() {
		BaseResultFactory.adminHealthError.clear();
		return success("清理成功");
	}

	@PostMapping("/cleanId")
	@ApiOperation(value = "Id清理错误")
	public AbstractBaseResult<?> cleanId(Long id) {
		BaseResultFactory.adminHealthError.remove(id);
		return success("清理成功");
	}

}
