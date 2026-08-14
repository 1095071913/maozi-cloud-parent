import request from './request'
import type {ApiResponse, OptionItem} from '@/types/api'

/**
 * 客户端下拉列表
 * GET /oauth/client/dropDownList
 */
export function getClientDropdown() {
  return request.get('/oauth/client/dropDownList') as unknown as Promise<
    ApiResponse<OptionItem[]>
  >
}

/**
 * 角色下拉列表
 * GET /system/role/dropDownList
 */
export function getRoleDropdown() {
  return request.get('/system/role/dropDownList') as unknown as Promise<
    ApiResponse<OptionItem[]>
  >
}
