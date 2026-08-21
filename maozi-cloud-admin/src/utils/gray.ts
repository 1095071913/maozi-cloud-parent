const GRAY_VERSION_KEY = 'maozi_gray_version'

/**
 * 内存副本：内嵌的同源页面（如经代理加载的 Nacos 控制台）登出时会执行 localStorage.clear()，
 * 一并清空本站数据；storage 事件捕获整体清空（事件 key 为 null）后自动回写。
 */
let grayVersionCache = localStorage.getItem(GRAY_VERSION_KEY) || ''

function persistGrayVersion(): void {
  if (grayVersionCache) {
    localStorage.setItem(GRAY_VERSION_KEY, grayVersionCache)
  } else {
    localStorage.removeItem(GRAY_VERSION_KEY)
  }
}

window.addEventListener('storage', (e) => {
  if (e.key === null) {
    persistGrayVersion()
  }
})

/** 获取灰度标识（空串表示未开启灰度测试） */
export function getGrayVersion(): string {
  return grayVersionCache
}

/** 开启灰度测试：记录灰度标识 */
export function setGrayVersion(version: string): void {
  grayVersionCache = version
  persistGrayVersion()
}

/** 取消灰度测试：清除灰度标识 */
export function clearGrayVersion(): void {
  grayVersionCache = ''
  persistGrayVersion()
}
