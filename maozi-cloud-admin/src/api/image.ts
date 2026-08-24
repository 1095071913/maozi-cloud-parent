import request from './request'
import type {ApiResponse, ImageUploadResult} from '@/types/api'

/**
 * 图片上传
 * POST /system/image/upload（multipart/form-data）
 * @param path 保存路径（对象存储目录前缀）
 * @param files 上传的图片文件列表
 */
export function uploadImage(path: string, files: File[]) {
  const formData = new FormData()
  formData.append('path', path)
  files.forEach((file) => formData.append('files', file))
  return request.post('/system/image/upload', formData) as unknown as Promise<
    ApiResponse<ImageUploadResult[]>
  >
}
