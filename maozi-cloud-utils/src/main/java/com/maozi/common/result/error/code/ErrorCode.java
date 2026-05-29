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

package com.maozi.common.result.error.code;

import com.maozi.common.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ErrorCode implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;

	private final Integer code;

	private final String message;

	private final String exceptionMessage;
	
	public ErrorCode(String message) {
		this.code = 400;
		this.message = message;
		this.exceptionMessage = message;
	} 

	public ErrorCode(Integer code, String message) {
		this.code = code;
		this.message = message;
		this.exceptionMessage = message;
	}

	public String getExceptionMessage() {
		return ObjectUtil.isNotNullEmpty(this.exceptionMessage) ? this.exceptionMessage : this.message;
	}

}