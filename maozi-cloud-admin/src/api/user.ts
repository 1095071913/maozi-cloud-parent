import request from './request'
import type {
    ApiResponse,
    PageResult,
    UserDetail,
    UserIndividualUpdateParam,
    UserInfo,
    UserListItem,
    UserSaveParam
} from '@/types/api'

/**
 * 获取当前登录用户个人详情
 * GET /system/user/individual/get
 */
export function getUserInfo() {
  return request.get('/system/user/individual/get') as unknown as Promise<
    ApiResponse<UserInfo>
  >
}

/**
 * 更新当前登录用户个人信息（名称/头像，密码可通过旧密码+新密码修改）
 * POST /system/user/individual/update
 */
export function updateUserInfo(data: UserIndividualUpdateParam) {
  return request.post(
    '/system/user/individual/update',
    data
  ) as unknown as Promise<ApiResponse<void>>
}

/**
 * 用户列表查询参数
 */
export interface UserListParams {
  /** 当前页 */
  current?: number
  /** 每页数量 */
  size?: number
  /** 名称（模糊查询） */
  name?: string
}

/**
 * 用户分页列表
 * POST /system/user/list
 */
export function getUserList(params: UserListParams) {
  return request.post('/system/user/list', {
    current: params.current ?? 1,
    size: params.size ?? 10,
    data: { name: params.name || undefined }
  }) as unknown as Promise<ApiResponse<PageResult<UserListItem>>>
}

/**
 * 用户详情
 * GET /system/user/{id}/get
 */
export function getUserDetail(id: string | number) {
  return request.get(`/system/user/${id}/get`) as unknown as Promise<
    ApiResponse<UserDetail>
  >
}

/**
 * 新增用户
 * POST /system/user/save
 */
export function saveUser(data: UserSaveParam) {
  return request.post('/system/user/save', data) as unknown as Promise<
    ApiResponse<string>
  >
}

/**
 * 更新用户
 * POST /system/user/{id}/update
 */
export function updateUser(id: string | number, data: UserSaveParam) {
  return request.post(`/system/user/${id}/update`, data) as unknown as Promise<
    ApiResponse<string>
  >
}

/**
 * 更新用户状态（0=禁用 1=启用）
 * POST /system/user/{id}/updateStatus
 */
export function updateUserStatus(id: string | number, status: number) {
  return request.post(`/system/user/${id}/updateStatus`, {
    data: status
  }) as unknown as Promise<ApiResponse<string>>
}

/**
 * 删除用户
 * POST /system/user/{id}/remove
 */
export function removeUser(id: string | number) {
  return request.post(`/system/user/${id}/remove`) as unknown as Promise<
    ApiResponse<string>
  >
}
