package com.maozi.base.api.impl.rest;

import com.maozi.base.annotation.Get;
import com.maozi.base.annotation.RestService;
import com.maozi.base.api.result.TemplateResult;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestService
@Tag(name = "【全局】/【模版接口】")
public class RestApplicationBase extends BaseCommon {

    private static final String PATH = "/application";

    @Get(value = PATH + "/template",description = "模版接口")
    public AbstractBaseResult<TemplateResult> template() {
        return success();
    }

}
