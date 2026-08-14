const GRAY_VERSION_KEY = 'maozi_gray_version'

/** 获取灰度标识（空串表示未开启灰度测试） */
export function getGrayVersion(): string {
  return localStorage.getItem(GRAY_VERSION_KEY) || ''
}

/** 开启灰度测试：记录灰度标识 */
export function setGrayVersion(version: string): void {
  localStorage.setItem(GRAY_VERSION_KEY, version)
}

/** 取消灰度测试：清除灰度标识 */
export function clearGrayVersion(): void {
  localStorage.removeItem(GRAY_VERSION_KEY)
}
