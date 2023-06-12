package com.maozi.user;

import com.maozi.base.AbstractBaseDtomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUser extends AbstractBaseDtomain{

	@ApiModelProperty("标识")
	private Long id;
	
	@ApiModelProperty("账号")
	private String username;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("头像")
	private String icon;
	
}
