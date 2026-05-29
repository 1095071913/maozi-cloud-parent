/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.maozi.common.result.success;

import com.maozi.common.result.AbstractBaseResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
@Schema(description = "接口成功结果集")
public class SuccessResult<D> extends AbstractBaseResult<D> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	//结果集成功默认码
	public final static Integer RESULT_SUCCESS_DEFAULT_CODE = 200;

	@Override
	public Integer getCode() {
		return RESULT_SUCCESS_DEFAULT_CODE;
	}

	@Getter
	private final D data;
	
	public SuccessResult(D data) {
		this.data = data;
	}
	
}