package com.maozi.oauth.token.constants;


/**
 * OAuth2令牌内省声明（Claim）字段常量
 * <p>
 * 定义RPC令牌内省响应中使用的标准字段名称常量，
 * </p>
 *
 * @author maozi
 */
public final class OAuth2TokenClaimConstants {

	/** 令牌是否活跃 */
	public static final String ACTIVE = "active";

	/** 令牌主体（用户名） */
	public static final String SUB = "sub";

	/** 令牌类型 */
	public static final String TOKEN_TYPE = "token_type";

	/** 用户ID */
	public static final String USER_ID = "user_id";

	/** 客户端ID */
	public static final String CLIENT_ID = "client_id";

	/** 用户权限列表 */
	public static final String AUTHORITIES = "authorities";

	public static final String ISS = "iss";

	/**
	 * 私有构造方法，防止实例化
	 * <p>
	 * 该类仅提供静态常量，不应被实例化
	 * </p>
	 */
	private OAuth2TokenClaimConstants() {
	}

}
