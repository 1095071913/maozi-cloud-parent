package com.maozi.health.api.impl.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/application/erroLog")
@Api(tags = "【全局】告警日志")
public class RestErrorLog extends BaseCommon {

	@PostMapping("/removeAll")
	@ApiOperation(value = "清空")
	public AbstractBaseResult<Void> removeAll() {
		adminHealthError.clear();
		return success(null);
	}

	@PostMapping("/{id}/remove")
	@ApiOperation(value = "删除")
	public AbstractBaseResult<Void> remove(@PathVariable String id) {
		adminHealthError.remove(id);
		return success(null);
	}

}
