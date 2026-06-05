package com.maozi.base.api.impl.rest;

import com.maozi.base.BaseEnum;
import com.maozi.base.annotation.Get;
import com.maozi.base.annotation.RestService;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.mvc.config.enums.EnumConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;


@RestService
@Tag(name = "公用模块/枚举常量")
public class RestApplicationCode {

	private static final String PATH = "/application/enum";

	@Get(value = PATH + "/list",description = "列表")
	public AbstractBaseResult<Map<String, List<BaseEnum>>> list() {
		return ResultUtil.success(EnumConfig.enums);
	}

	@Get(value = PATH + "/{name}/get",description = "详情")
	public AbstractBaseResult<List<BaseEnum>> get(@PathVariable("name") String name) {
		return ResultUtil.success(EnumConfig.enums.get(name));
	}

}
