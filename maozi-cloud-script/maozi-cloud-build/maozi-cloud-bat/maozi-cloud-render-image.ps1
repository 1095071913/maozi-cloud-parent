# ============================================================
# 镜像渲染器 (PowerShell worker, 由 maozi-cloud-render-image.bat 调用)
# ------------------------------------------------------------
# 对应 shell 版: maozi-cloud-shell/maozi-cloud-render-image.sh
# ------------------------------------------------------------
# 职责:
#   1. 读 maozi-cloud-services.json, 取本服务配置 + defaults
#   2. 读 maozi-cloud-service-image.template, 占位符替换
#   3. 渲染结果写到 ${service_name}-image
#   4. 生成 ${service_name}-build-docker.bat:
#      copy jar / buildx / compose up -d / del jar / del Dockerfile / del 自身
# 服务不在 JSON 里 -> 退出码 1, bat 据此 skip
# ============================================================

param(
    [Parameter(Mandatory=$true)][string]$ServiceName,
    [Parameter(Mandatory=$true)][string]$ConfigFile,
    [Parameter(Mandatory=$true)][string]$TemplateFile,
    [Parameter(Mandatory=$true)][string]$ImageFile,
    [Parameter(Mandatory=$true)][string]$ImageDirectory,
    [Parameter(Mandatory=$true)][string]$DockerDirectory,
    [Parameter(Mandatory=$true)][string]$RepoRoot,
    [Parameter(Mandatory=$true)][string]$ModuleDir
)
$ErrorActionPreference = "Stop"

# ---- 读 JSON, 找本服务配置 ----
$cfg = Get-Content -Raw -Encoding UTF8 -Path $ConfigFile | ConvertFrom-Json
$defaults = $cfg.defaults
$svc = $cfg.services | Where-Object { $_.service_name -eq $ServiceName } | Select-Object -First 1
if (-not $svc) {
    [Console]::Error.WriteLine("[render] skip $ServiceName : not in $ConfigFile")
    exit 1
}

# ---- 确定开关与配置值 ----
$hasDubbo = ($svc.PSObject.Properties.Name -contains "dubbo_port")
$otel     = [bool]$svc.opentelemetry

function Resolve-Val($obj, $name, $fallback) {
    if (($obj.PSObject.Properties.Name -contains $name) -and $obj.$name) { return $obj.$name }
    return $fallback
}
$jvm      = Resolve-Val $svc "jvm_params" $defaults.jvm_params
$base     = Resolve-Val $svc "base_image" $defaults.base_image
$dubboIp  = $defaults.dubbo_ip_to_registry

# OTel / Dubbo / add-opens 标志全部从 JSON 读, 改参数不用动部署脚本
# 服务块里同名键会整体覆盖 defaults 里的列表 (不合并)
$dubboFlag = Resolve-Val $svc "dubbo_flag" (Resolve-Val $defaults "dubbo_flag" "")
$otelFlags = Resolve-Val $svc "otel_flags" (Resolve-Val $defaults "otel_flags" @())
$addOpens  = Resolve-Val $svc "add_opens"  (Resolve-Val $defaults "add_opens"  @())

# ---- 计算占位符实际值 ----
$lf = "`n"
$dubboPortLine = if ($hasDubbo) { ", Dubbo " + $svc.dubbo_port } else { "" }
if ($hasDubbo) {
    $dubboEnvBlock = "ENV DUBBO_IP_TO_REGISTRY=" + $dubboIp + $lf + "ENV APPLICATION_DUBBO_PORT=" + $svc.dubbo_port
    $dubboExposeLine = $lf + "EXPOSE `${APPLICATION_DUBBO_PORT}"
    $dubboPortStr = "$($svc.dubbo_port)"
} else {
    $dubboEnvBlock = ""
    $dubboExposeLine = ""
    $dubboPortStr = ""
}

# CMD 行: java -server [+ Dubbo 标志] [+ OTel block] [jvm] [add-opens] -jar
# 用 " \<LF>  " 连接, Dockerfile CMD 续行, OTel 关闭时不会遗留孤立反斜杠
# cmdParts 里每个元素对应 CMD 一行, add_opens / otel_flags 的每个数组元素
# 都独立占一行, 便于在 Dockerfile 里阅读与定位
$cmdParts = New-Object System.Collections.Generic.List[string]
$cmdParts.Add("java -server")
if ($hasDubbo -and $dubboFlag) {
    $cmdParts.Add($dubboFlag)
}
if ($otel -and $otelFlags) {
    foreach ($p in $otelFlags) { $cmdParts.Add($p) | Out-Null }
}
$cmdParts.Add($jvm) | Out-Null
if ($addOpens) {
    foreach ($p in $addOpens) { $cmdParts.Add($p) | Out-Null }
}
$cmdParts.Add("-jar `${APPLICATION_NAME}.jar") | Out-Null
$joiner = " " + [char]92 + $lf + "  "   # 单个反斜杠 + LF + 两空格
$cmdLine = "CMD " + ($cmdParts -join $joiner)

$otelYes  = if ($otel)      { "yes" } else { "no" }
$dubboYes = if ($hasDubbo)  { "yes" } else { "no" }

# ---- 读模板, 替换占位符, 写渲染结果 ----
$tpl = Get-Content -Raw -Encoding UTF8 -Path $TemplateFile
$map = @{
    "__BASE_IMAGE__"        = $base
    "__SERVICE_NAME__"      = $ServiceName
    "__SERVICE_PORT__"      = "$($svc.service_port)"
    "__DUBBO_PORT_LINE__"   = $dubboPortLine
    "__DUBBO_PORT__"        = $dubboPortStr
    "__OTEL_YES_NO__"       = $otelYes
    "__DUBBO_YES_NO__"      = $dubboYes
    "__DUBBO_ENV_BLOCK__"   = $dubboEnvBlock
    "__DUBBO_EXPOSE_LINE__" = $dubboExposeLine
    "__CMD_LINE__"          = $cmdLine
}
foreach ($k in $map.Keys) { $tpl = $tpl.Replace($k, $map[$k]) }
# 父目录可能被误删 (例如静态 Dockerfile 清理后空目录被系统 / IDE 自动清理),
# 写入前确保存在, 否则 .NET WriteAllText 会抛 DirectoryNotFoundException
$imageParent = Split-Path -Parent $ImageFile
if (-not (Test-Path $imageParent)) {
    New-Item -ItemType Directory -Force -Path $imageParent | Out-Null
}

# .NET WriteAllText 默认 UTF-8 无 BOM, 不做行尾转换 (保留 LF)
[System.IO.File]::WriteAllText($ImageFile, $tpl)

# ---- 生成 build-docker.bat ----
# 顺序: 必须先 buildx 再 compose, del Dockerfile 必须在 buildx 之后
# 用 ${bs} 显式分隔变量名, 否则 $bs 后跟字母 t 会被 PowerShell 解析为 `t (TAB) 转义
$bs   = [char]92
$jarSrc = "$RepoRoot${bs}$ModuleDir${bs}target${bs}$ServiceName.jar"
$buildBat = Join-Path $ImageDirectory "$ServiceName-build-docker.bat"
$lines = @(
    "@echo off",
    "copy `"$jarSrc`" `"$ImageDirectory$bs`"",
    "cd /d `"$ImageDirectory`"",
    "docker buildx build -f `"$ImageFile`" -t ${ServiceName}:laster .",
    "docker-compose -f `"$DockerDirectory${bs}docker-compose.yml`" up -d $ServiceName",
    "del `"$ImageDirectory${bs}$ServiceName.jar`"",
    "del `"$ImageFile`"",
    "del `"%~f0`""
)
# bat 文件用 CRLF (Windows 默认), Set-Content -Encoding ASCII 与现有脚本一致
Set-Content -Path $buildBat -Value $lines -Encoding ASCII

[Console]::Out.WriteLine("[deploy] $ServiceName : rendering image, building and starting container")
exit 0
