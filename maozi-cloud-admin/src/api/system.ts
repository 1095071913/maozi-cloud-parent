import request from './request'
import type {ApiResponse, SystemInfo} from '@/types/api'

/**
 * 获取系统详情（项目信息）
 * GET /system/config/system/get
 */
export function getSystemInfo() {
  return request.get('/system/config/system/get') as unknown as Promise<
    ApiResponse<SystemInfo>
  >
}
