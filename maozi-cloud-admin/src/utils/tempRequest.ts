const TEMP_REQUEST_URL_KEY = 'maozi_temp_request_url'

/** 获取临时请求地址（空串表示未启用临时请求） */
export function getTempRequestUrl(): string {
  return localStorage.getItem(TEMP_REQUEST_URL_KEY) || ''
}

/** 启用临时请求：记录临时请求地址 */
export function setTempRequestUrl(url: string): void {
  localStorage.setItem(TEMP_REQUEST_URL_KEY, url)
}

/** 还原临时请求：清除临时请求地址 */
export function clearTempRequestUrl(): void {
  localStorage.removeItem(TEMP_REQUEST_URL_KEY)
}
