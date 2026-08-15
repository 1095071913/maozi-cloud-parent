package com.maozi.system.user.vo;

import com.maozi.base.plugin.mapping.QueryMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户个人信息视图对象
 * <p>
 * 用于展示当前登录用户的个人信息，包括用户名称、头像
 * 以及该用户拥有的权限标识列表。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIndividualInfoVo implements Serializable {

	/** 序列化版本号 */
	@Serial
    private static final long serialVersionUID = 1L;

	/** 用户名称 */
	@Schema(description = "名称")
	private String name;

	/** 用户头像图标 */
	@Schema(description = "头像")
	private String icon;

	/** 用户拥有的权限标识列表，通过远程服务查询获取 */
	@Schema(description = "权限列表")
	@QueryMapping(isService = true,serviceName = "userServiceImpl",functionName = "getCurrentPermissions")
	private List<String> permissions;

}
