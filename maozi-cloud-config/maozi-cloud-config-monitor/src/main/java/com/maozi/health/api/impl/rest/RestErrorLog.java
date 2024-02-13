package com.maozi.health.api.impl.rest;

import com.maozi.base.annotation.Post;
import com.maozi.base.annotation.RestService;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@RestService
@Tag(name = "【全局】告警日志")
public class RestErrorLog extends BaseCommon {

	private static final String PATH = "/application/error/log";

	@Post(value = PATH + "/{id}/remove",description = "删除")
	public AbstractBaseResult<Void> remove(@PathVariable String id) {
		adminHealthError.remove(id);
		return success(null);
	}

	@Post(value = PATH + "/removeAll",description = "清空")
	public AbstractBaseResult<Void> removeAll() {
		adminHealthError.clear();
		return success(null);
	}

}
