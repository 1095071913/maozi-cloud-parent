import request from './request'
import type {ApiResponse, RoleDetail, RoleListItem, RoleSaveParam} from '@/types/api'

/**
 * 角色列表（全量，扁平数组）
 * GET /system/role/list
 */
export function getRoleList() {
  return request.get('/system/role/list') as unknown as Promise<
    ApiResponse<RoleListItem[]>
  >
}

/**
 * 角色详情
 * GET /system/role/{id}/get
 */
export function getRoleDetail(id: string | number) {
  return request.get(`/system/role/${id}/get`) as unknown as Promise<
    ApiResponse<RoleDetail>
  >
}

/**
 * 新增角色
 * POST /system/role/save
 */
export function saveRole(data: RoleSaveParam) {
  return request.post('/system/role/save', data) as unknown as Promise<
    ApiResponse<string>
  >
}

/**
 * 更新角色
 * POST /system/role/{id}/update
 */
export function updateRole(id: string | number, data: RoleSaveParam) {
  return request.post(`/system/role/${id}/update`, data) as unknown as Promise<
    ApiResponse<string>
  >
}

/**
 * 更新角色状态（0=禁用 1=启用）
 * POST /system/role/{id}/updateStatus
 */
export function updateRoleStatus(id: string | number, status: number) {
  return request.post(`/system/role/${id}/updateStatus`, {
    data: status
  }) as unknown as Promise<ApiResponse<string>>
}

/**
 * 删除角色
 * POST /system/role/{id}/remove
 */
export function removeRole(id: string | number) {
  return request.post(`/system/role/${id}/remove`) as unknown as Promise<
    ApiResponse<string>
  >
}
