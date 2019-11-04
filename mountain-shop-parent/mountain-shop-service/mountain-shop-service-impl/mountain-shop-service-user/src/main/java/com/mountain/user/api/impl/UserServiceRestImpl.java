package com.mountain.user.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import com.google.common.collect.Maps;
import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.factory.result.success.SuccessResult;
import com.mountain.tool.MapperUtils;
import com.mountain.tool.OkHttpClientUtil;
import com.mountain.user.UserDo;
import com.mountain.user.api.UserServiceRest;
import com.mountain.uservo.LoginAndRegisterVo;
import com.mountain.uservo.UpdateUserVo;
import okhttp3.Response;


/**
 * 
 * 	功能说明：用户Rest API实现
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-03 ：23:08:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

public class UserServiceRestImpl extends UserServiceImpl implements UserServiceRest {
	
	
	
	
	// 用户注册
	@Override
	public AbstractBaseResult<UserDo> UserRegister(LoginAndRegisterVo loginAndRegisterVo, BindingResult bindingResult) {

		/* 校验参数 Begin */
		AbstractBaseResult result = loginAndRegisterVo.voHasErrors(bindingResult);
		if (!result.ifStatus()) {
			return result;
		}
		/* 校验参数 End */

		/* 查询用户是否存在 Begin */
		Integer userCount = count(UserDo.builder().phone(loginAndRegisterVo.getUsername()).build());
		if (userCount > 0) {
			return error(code(40002));
		}
		/* 查询用户是否存在 End */

		/* Vo转换DO 添加数据库 Begin */
		UserDo user = loginAndRegisterVo.voToDo();
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Integer userInsert = insert(user, "");
		if (userInsert < 1) {
			return error(code(40004));
		}
		/* Vo转换DO 用户添加数据库 End */

		return success("注册成功");
	}

	
	
	
	// 用户登录
	private final String URL_OAUTH_TOKEN = "http://localhost:1000/auth/oauth/token";
	@Override
	public AbstractBaseResult userLogin(LoginAndRegisterVo loginAndRegisterVo, BindingResult bindingResult) {

		/* 校验参数 Begin */
		AbstractBaseResult result = loginAndRegisterVo.voHasErrors(bindingResult);
		if (!result.ifStatus()) {
			return result;
		}
		/* 校验参数 End */

		/* PRC调用SSO登录 Begin */
		AbstractBaseResult userDetailsResult = userDetailsServicePRC
				.loadUserByUsername(loginAndRegisterVo.getUsername());
		if (!userDetailsResult.ifStatus()) {
			return userDetailsResult;
		}
		UserDetails userDetails = (UserDetails) ((SuccessResult) userDetailsResult).getData();
		if (!passwordEncoder.matches(loginAndRegisterVo.getPassword(), userDetails.getPassword())) {
			return error(code(40003));
		}
		/* PRC调用SSO登录 Begin */

		/* 采用OKhttp调用SSO获取Token Begin */
		Map<String, String> params = Maps.newHashMap();
		params.put("username", loginAndRegisterVo.getUsername());
		params.put("password", loginAndRegisterVo.getPassword());
		params.put("grant_type", "password");
		params.put("client_id", "client");
		params.put("client_secret", "secret");
		try {
			Response response = OkHttpClientUtil.getInstance().postData(URL_OAUTH_TOKEN, params);
			String jsonString = Objects.requireNonNull(response.body()).string();
			Map<String, Object> jsonMap = MapperUtils.json2map(jsonString);
			String token = String.valueOf(jsonMap.get("access_token"));
			return success(new HashMap<String, String>() {
				{
					put("token", token);
				}
			});
		} catch (Exception e) {  
			e.printStackTrace();
			return error(code(600));
		}
		/* 采用OKhttp调用SSO获取Token End */

	}

	
	
	
	// 用户退出
	@Override
	public AbstractBaseResult userLogout(HttpServletRequest request) {
		String token = request.getParameter("access_token");
		return userLogoutService.userLogout(token);
	}

	
	
	
	// Rest查询用户
	@Override
	public AbstractBaseResult<UserDo> userInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return selectUserOne(UserDo.builder().username(authentication.getName()).build());
	}

	
	
	
	// 用户修改密码
	@Override
	public AbstractBaseResult userUpdatePassword(String password, String updatePassword, HttpServletRequest request) {

		/* 校验参数 Begin */
		if (isNotNull(password) || isNotNull(updatePassword)) {
			return error(code(40006));
		}
		/* 校验参数 End */

		/* 查询用户信息 Begin */
		AbstractBaseResult<UserDo> UserInfoResult = userInfo();
		if (!UserInfoResult.ifStatus()) {
			return UserInfoResult;
		}
		/* 查询用户信息 End */

		/* 对比旧密码 Begin */
		UserDo userDo = ((SuccessResult<UserDo>) UserInfoResult).getDatabeans().get(0).getData();
		if (!passwordEncoder.matches(password, userDo.getPassword())) {
			return error(code(40005));
		}
		/* 对比旧密码 End */

		/* 更新用户密码 Begin */
		userDo.setPassword(passwordEncoder.encode(updatePassword));
		Integer updateUserResult = update(userDo, userDo.getUsername());
		if (updateUserResult == 0) {
			return error(code(500));
		}
		/* 更新用户密码 End */

		/* 用户退出 Begin */
		userLogout(request);
		/* 用户退出 Begin */

		return success("用户密码修改成功!");
	}

	
	
	
	// 用户更新
	@Override
	public AbstractBaseResult userUpdate(UpdateUserVo userVo, BindingResult bindingResult) {

		/* 参数校验 Begin */
		AbstractBaseResult voHasErrors = userVo.voHasErrors(bindingResult);
		if (!voHasErrors.ifStatus()) {
			return voHasErrors;
		}
		/* 参数校验 End */

		/* 查询用户信息 Begin */
		AbstractBaseResult<UserDo> UserInfoResult = userInfo();
		if (!UserInfoResult.ifStatus()) {
			return UserInfoResult;
		}
		/* 查询用户信息 End */

		/* 更新用户信息 Begin */
		UserDo userDo = ((SuccessResult<UserDo>) UserInfoResult).getDatabeans().get(0).getData();
		UserDo user = userVo.voToDo();
		user.setId(userDo.getId());
		Integer updateUser = update(user, user.getUsername());
		if (updateUser < 1) {
			return error(code(500));
		}
		/* 更新用户信息 End */

		return success(user);
	}

	

}
