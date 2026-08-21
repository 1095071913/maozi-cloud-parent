const TEMP_REQUEST_URL_KEY = 'maozi_temp_request_url'

/**
 * 内存副本：内嵌的同源页面（如经代理加载的 Nacos 控制台）登出时会执行 localStorage.clear()，
 * 一并清空本站数据；storage 事件捕获整体清空（事件 key 为 null）后自动回写。
 */
let tempRequestUrlCache = localStorage.getItem(TEMP_REQUEST_URL_KEY) || ''

function persistTempRequestUrl(): void {
  if (tempRequestUrlCache) {
    localStorage.setItem(TEMP_REQUEST_URL_KEY, tempRequestUrlCache)
  } else {
    localStorage.removeItem(TEMP_REQUEST_URL_KEY)
  }
}

window.addEventListener('storage', (e) => {
  if (e.key === null) {
    persistTempRequestUrl()
  }
})

/** 获取临时请求地址（空串表示未启用临时请求） */
export function getTempRequestUrl(): string {
  return tempRequestUrlCache
}

/** 启用临时请求：记录临时请求地址 */
export function setTempRequestUrl(url: string): void {
  tempRequestUrlCache = url
  persistTempRequestUrl()
}

/** 还原临时请求：清除临时请求地址 */
export function clearTempRequestUrl(): void {
  tempRequestUrlCache = ''
  persistTempRequestUrl()
}
