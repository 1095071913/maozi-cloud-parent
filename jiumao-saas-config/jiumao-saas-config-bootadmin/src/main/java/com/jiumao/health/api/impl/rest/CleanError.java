package com.jiumao.health.api.impl.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jiumao.factory.BaseResultFactory;
import com.jiumao.factory.result.AbstractBaseResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping("/application/bootAdmin")
@RestController
@Api(tags = "【全局】BootAdmin错误日志管理")
public class CleanError extends BaseResultFactory {

	@PostMapping("/cleanAll")
	@ApiOperation(value = "清理全部错误")
	public AbstractBaseResult<Void> cleanAll() {
		BaseResultFactory.adminHealthError.clear();
		return success(null);
	}

	@PostMapping("/cleanId")
	@ApiOperation(value = "id清理错误")
	public AbstractBaseResult<Void> cleanId(@RequestParam String id) {
		BaseResultFactory.adminHealthError.remove(id);
		return success(null);
	}

}
