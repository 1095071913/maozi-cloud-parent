import request from './request'
import type {
    ApiResponse,
    PermissionDetail,
    PermissionItem,
    PermissionOptionItem,
    PermissionSaveParam
} from '@/types/api'

/**
 * 获取权限列表（全量，扁平结构）
 * GET /system/permission/list
 *
 * 返回项包含 type(0=目录/1=菜单/2=按钮)、mark、parentId 等字段，
 * 前端据此构建目录/菜单/按钮，并按用户自身 permissions 做可见性过滤。
 */
export function getPermissionList() {
  return request.get('/system/permission/list') as unknown as Promise<
    ApiResponse<PermissionItem[]>
  >
}

/**
 * 权限下拉列表（轻量结构，仅含 id、parentId、name、type、level）
 * GET /system/permission/dropDownList
 */
export function getPermissionDropDownList() {
  return request.get('/system/permission/dropDownList') as unknown as Promise<
    ApiResponse<PermissionOptionItem[]>
  >
}

/**
 * 权限详情
 * GET /system/permission/{id}/get
 */
export function getPermissionDetail(id: string | number) {
  return request.get(`/system/permission/${id}/get`) as unknown as Promise<
    ApiResponse<PermissionDetail>
  >
}

/**
 * 新增权限
 * POST /system/permission/save
 */
export function savePermission(data: PermissionSaveParam) {
  return request.post('/system/permission/save', data) as unknown as Promise<
    ApiResponse<string>
  >
}

/**
 * 更新权限
 * POST /system/permission/{id}/update
 */
export function updatePermission(id: string | number, data: PermissionSaveParam) {
  return request.post(
    `/system/permission/${id}/update`,
    data
  ) as unknown as Promise<ApiResponse<string>>
}

/**
 * 删除权限
 * POST /system/permission/{id}/remove
 */
export function removePermission(id: string | number) {
  return request.post(`/system/permission/${id}/remove`) as unknown as Promise<
    ApiResponse<string>
  >
}

