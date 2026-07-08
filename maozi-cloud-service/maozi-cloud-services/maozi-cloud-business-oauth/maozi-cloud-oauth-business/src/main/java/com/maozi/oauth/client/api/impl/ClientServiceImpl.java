package com.maozi.oauth.client.api.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.param.PageParam;
import com.maozi.base.result.PageResult;
import com.maozi.common.ObjectUtil;
import com.maozi.oauth.client.api.ClientService;
import com.maozi.oauth.client.domain.ClientDo;
import com.maozi.oauth.client.mapper.ClientMapper;
import com.maozi.oauth.client.param.ClientListParam;
import com.maozi.oauth.client.param.ClientSaveUpdateParam;
import com.maozi.oauth.client.vo.ClientInfoVo;
import com.maozi.oauth.client.vo.ClientListVo;
import com.maozi.service.api.impl.BaseServiceImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OAuth2客户端服务实现类。
 * <p>
 * 继承 BaseServiceImpl，实现 ClientService 接口。
 * 提供客户端的增删改查、Token设置、客户端设置等核心业务逻辑，
 * 包括客户端密钥加密、令牌有效期配置、OAuth2相关设置的构建等功能。
 * </p>
 */
@Service
public class ClientServiceImpl extends BaseServiceImpl<ClientMapper,ClientDo,Void> implements ClientService {

	/** 资源名称标识，用于日志和异常提示 */
	private static final String RESOURCE_NAME = "客户端";

	/** 请求令牌（Access Token）默认有效期：2小时 */
	protected static final Long DEFAULT_ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 2L;

	/** 刷新令牌（Refresh Token）默认有效期：7天 */
	protected static final Long DEFAULT_REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 7L;

	/** OAuth2专用的JSON序列化/反序列化对象，注册了Spring Security OAuth2的Jackson模块 */
	public final static ObjectMapper oauthObjectMapper;

	/** 密码编码器，用于对客户端密钥进行加密存储 */
	protected final static PasswordEncoder passwordEncoder;

	/*
	  静态初始化块。
	  初始化OAuth2专用的ObjectMapper（注册OAuth2授权服务器和安全模块）
	  以及密码编码器（使用委托式密码编码器，支持多种加密算法）。
	 */
	static {

		oauthObjectMapper = new ObjectMapper();
		oauthObjectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
		oauthObjectMapper.registerModules(SecurityJackson2Modules.getModules(RegisteredClientRepository.class.getClassLoader()));

		passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	}


	/**
	 * 获取资源名称。
	 *
	 * @return 资源名称字符串
	 */
	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}

	/**
	 * 客户端新增或更新的统一处理方法。
	 * <p>
	 * 新增时：自动生成clientId，设置默认令牌有效期，构建默认客户端设置（不需要确认授权）。
	 * 更新时：清除clientId防止被修改。
	 * 通用处理：对客户端密钥进行加密，构建令牌设置（包括匿名令牌格式、授权码存活时间、
	 * 设备码存活时间、是否重用刷新令牌、Access Token和Refresh Token存活时间等）。
	 * </p>
	 *
	 * @param id    客户端主键ID，新增时为null
	 * @param param 客户端保存/更新参数
	 * @return 保存后的客户端主键ID
	 */
	protected Long restSaveUpdate(Long id, ClientSaveUpdateParam param) {

		if(ObjectUtil.isNullEmpty(id)) {

			param.setClientId(String.valueOf(IdWorker.getId()));

			if(ObjectUtil.isNullEmpty(param.getAccessTokenValiditySeconds())){
				param.setAccessTokenValiditySeconds(DEFAULT_ACCESS_TOKEN_VALIDITY_SECONDS);
			}

			if(ObjectUtil.isNullEmpty(param.getRefreshTokenValiditySeconds())){
				param.setRefreshTokenValiditySeconds(DEFAULT_REFRESH_TOKEN_VALIDITY_SECONDS);
			}

			//不需要确认授权
			ClientSettings clientSettings = ClientSettings.builder().requireAuthorizationConsent(Boolean.FALSE).build();
			param.setClientSettings(clientSettings.getSettings());

		}else{
			param.setClientId(null);
		}
		
		if(ObjectUtil.isNotNullEmpty(param.getClientSecret())) {
			param.setClientSecret(passwordEncoder.encode(param.getClientSecret()));
		}

		Long accessTokenValiditySeconds = param.getAccessTokenValiditySeconds();
		Long refreshTokenValiditySeconds = param.getRefreshTokenValiditySeconds();
		if(ObjectUtil.isNotNullEmpty(accessTokenValiditySeconds) && ObjectUtil.isNotNullEmpty(refreshTokenValiditySeconds)) {

			TokenSettings.Builder builder = TokenSettings.builder();

			//匿名令牌
			builder.accessTokenFormat(OAuth2TokenFormat.REFERENCE);
			// 授权码存活时间：5分钟
			builder.authorizationCodeTimeToLive(Duration.ofSeconds(300));
			// 设备码存活时间：5分钟
			builder.deviceCodeTimeToLive(Duration.ofSeconds(300));
			// 刷新 Access Token 后是否重用 Refresh Token
			builder.reuseRefreshTokens(Boolean.FALSE);


			// Access Token 存活时间
			if(ObjectUtil.isNotNullEmpty(accessTokenValiditySeconds)){
				builder.accessTokenTimeToLive(Duration.ofSeconds(accessTokenValiditySeconds));
			}

			// Refresh Token 存活时间
			if(ObjectUtil.isNotNullEmpty(refreshTokenValiditySeconds)) {
				builder.refreshTokenTimeToLive(Duration.ofSeconds(refreshTokenValiditySeconds));
			}

			param.setTokenSettings(builder.build().getSettings());

		}
		
		return saveUpdate(id, param);
		
	}

	/**
	 * 根据主键ID获取客户端详细信息。
	 * <p>
	 * 查询客户端基础信息后，从TokenSettings中提取Access Token和Refresh Token的有效期（秒），
	 * 封装到ClientInfoVo中返回。
	 * </p>
	 *
	 * @param id 客户端主键ID
	 * @return 客户端详细信息VO对象，不存在时返回null
	 */
	protected ClientInfoVo superRestGet(Long id) {

		ClientDo domain = getAvailableById(id, ClientDo::getClientId, ClientDo::getName, ClientDo::getAuthorizationGrantTypes, ClientDo::getTokenSettings, ClientDo::getRemark, ClientDo::getStatus);
		if(ObjectUtil.isNullEmpty(domain)){
			return null;
		}

		ClientInfoVo response = CglibUtil.copy(domain, ClientInfoVo.class);

		TokenSettings tokenSettings = TokenSettings.withSettings(domain.getTokenSettings()).build();
		response.setAccessTokenValiditySeconds(tokenSettings.getAccessTokenTimeToLive().getSeconds());
		response.setRefreshTokenValiditySeconds(tokenSettings.getRefreshTokenTimeToLive().getSeconds());

		return response;

	}

	/**
	 * 分页查询客户端列表。
	 * <p>
	 * 根据查询参数构建查询条件，执行关联分页查询，
	 * 并将每条记录的TokenSettings解析为Access Token和Refresh Token的有效期（秒）。
	 * </p>
	 *
	 * @param pageParam 分页查询参数，包含查询条件和分页信息
	 * @return 分页结果，包含客户端列表数据
	 */
	protected PageResult<ClientListVo> superRestList(PageParam<ClientListParam> pageParam){

		Class<ClientListVo> responseClass = ClientListVo.class;
		MPJLambdaWrapper<ClientDo> wrapper = buildQueryWrapper(pageParam.getData(), responseClass);
		Page<ClientListVo> page = selectJoinListPage(convertPage(pageParam), responseClass,wrapper);

		page.getRecords().forEach(item -> {

			TokenSettings tokenSettings = TokenSettings.withSettings(item.getTokenSettings()).build();
			item.setAccessTokenValiditySeconds(tokenSettings.getAccessTokenTimeToLive().getSeconds());
			item.setRefreshTokenValiditySeconds(tokenSettings.getRefreshTokenTimeToLive().getSeconds());

		});

		return convertPageResult(page);
	}

	/**
	 * 根据客户端主键ID获取客户端标识（clientId）。
	 *
	 * @param id 客户端主键ID
	 * @return 客户端标识字符串
	 */
	@Override
	public String getClientId(Long id) {
		return getByIdThrowError(id, ClientDo::getClientId).getClientId();
	}

	/**
	 * 根据客户端主键ID集合批量获取客户端ID与clientId的映射关系。
	 *
	 * @param ids 客户端主键ID集合
	 * @return 键为主键ID、值为clientId的映射Map
	 */
	@Override
	public Map<Long,String> getClientIdsMap(Collection<Long> ids) {

		LambdaQueryWrapper<ClientDo> wrapper = Wrappers.lambdaQuery();

		wrapper.select(ClientDo::getId,ClientDo::getClientId);
		
		wrapper.in(ClientDo::getId, ids);
		
		return list(wrapper).stream().collect(Collectors.toMap(ClientDo::getId, ClientDo::getClientId));
		
	}

}