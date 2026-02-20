package com.maozi.base.api.result;

import com.maozi.base.result.EnumResult;
import com.maozi.base.result.ListStringResult;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.ErrorResult;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TemplateResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorResult errorResult;

    private final ListStringResult listStringResult;

    private final AbstractBaseResult<EnumResult> baseResultEnumResult;

}
