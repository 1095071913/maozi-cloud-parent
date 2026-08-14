import request from './request'
import type {ApiResponse, TokenResult} from '@/types/api'

/** OAuth2 客户端凭证（Basic Auth） */
const CLIENT_AUTH = { username: 'system', password: '123456' }

const FORM_HEADERS = { 'Content-Type': 'application/x-www-form-urlencoded' }

/**
 * 账号密码登录（密码模式）
 * POST /oauth/oauth2/token
 */
export function login(username: string, password: string) {
  const params = new URLSearchParams({
    grant_type: 'password',
    username,
    password
  })
  return request.post('/oauth/oauth2/token', params, {
    auth: CLIENT_AUTH,
    headers: FORM_HEADERS
  }) as unknown as Promise<ApiResponse<TokenResult>>
}

/**
 * 登出（销毁令牌）
 * POST /oauth/oauth2/revoke
 */
export function revoke(token: string) {
  const params = new URLSearchParams({ token })
  return request.post('/oauth/oauth2/revoke', params, {
    auth: CLIENT_AUTH,
    headers: FORM_HEADERS
  }) as unknown as Promise<ApiResponse<null>>
}

/**
 * 刷新令牌
 * POST /oauth/oauth2/token
 * 标记 _isRefresh：该请求失败时不走响应拦截器的统一错误处理，交由刷新逻辑自行处理。
 */
export function refreshToken(refreshToken: string) {
  const params = new URLSearchParams({
    grant_type: 'refresh_token',
    refresh_token: refreshToken
  })
  return request.post('/oauth/oauth2/token', params, {
    auth: CLIENT_AUTH,
    headers: FORM_HEADERS,
    _isRefresh: true
  }) as unknown as Promise<ApiResponse<TokenResult>>
}
