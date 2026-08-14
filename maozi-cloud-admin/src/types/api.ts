/** 统一响应结构 */
export interface ApiResponse<T = unknown> {
  /** 业务内码，200 为成功 */
  code: number
  /** 数据 */
  data: T
  /** 错误信息（code 非 200 时返回） */
  message?: string
}

/** 系统详情（项目信息，/system/config/system/get） */
export interface SystemInfo {
  /** 项目名称 */
  projectName: string
  /** 公司名称 */
  corporationName: string
  /** 项目图标 */
  icon: string
  /** 项目环境 */
  environment: string
  /** 项目描述 */
  description: string
  /** 项目版权 */
  copyright: string
}

/** OAuth2 令牌返回结果 */
export interface TokenResult {
  /** 访问令牌 */
  access_token: string
  /** 刷新令牌 */
  refresh_token: string
  /** 令牌类型，固定 Bearer */
  token_type: string
  /** 访问令牌有效期（秒） */
  expires_in: string
  /** 刷新令牌有效期（秒） */
  refresh_token_expires_in: string
  /** 授权范围 */
  scope: string
}

/** 用户个人详情 */
export interface UserInfo {
  /** 名称 */
  name: string
  /** 头像 */
  icon: string
  /** 权限标识列表（用户自身拥有的 mark 集合） */
  permissions: string[]
}

/** 个人信息更新参数（/system/user/individual/update） */
export interface UserIndividualUpdateParam {
  /** 名称 */
  name: string
  /** 头像 */
  icon: string
  /** 旧密码（修改密码时必填） */
  password?: string
  /** 新密码（与旧密码一同留空则不修改密码） */
  newPassword?: string
}

/** 权限类型：0=目录 1=菜单 2=按钮 */
export enum PermissionType {
  /** 目录 - 一级导航目录 */
  DIRECTORY = 0,
  /** 菜单 - 具体的功能菜单页面 */
  MENU = 1,
  /** 按钮 - 页面中的操作按钮权限 */
  BUTTON = 2
}

/** 权限项（/system/permission/list 返回元素） */
export interface PermissionItem {
  /** 权限 ID */
  id: string | number
  /** 上级权限 ID */
  parentId: string | number
  /** 名称 */
  name: string
  /** 图标 */
  icon: string
  /** 权限唯一标识编码，如 system:user */
  mark: string
  /** 类型（0=目录 1=菜单 2=按钮） */
  type: PermissionType
  /** 排序（升序，越小越靠前） */
  sort?: number
}

/** 权限树节点（带 children） */
export interface PermissionTreeNode extends PermissionItem {
  /** 子节点 */
  children?: PermissionTreeNode[]
}

/** 权限下拉项（/system/permission/dropDownList 返回元素） */
export interface PermissionOptionItem {
  /** 权限 ID */
  id: string | number
  /** 上级权限 ID */
  parentId: string | number
  /** 名称 */
  name: string
  /** 类型（0=目录 1=菜单 2=按钮） */
  type: PermissionType
  /** 排序（升序，越小越靠前） */
  sort?: number
  /** 深度 */
  level?: number
}

/** OAuth2 授权类型：0=授权码 1=客户端 2=刷新令牌 3=密码 */
export enum AuthType {
  /** 授权码模式 */
  AUTHORIZATION_CODE = 0,
  /** 客户端模式 */
  CLIENT_CREDENTIALS = 1,
  /** 刷新令牌模式 */
  REFRESH_TOKEN = 2,
  /** 密码模式 */
  PASSWORD = 3
}

/** 权限详情（/system/permission/{id}/get） */
export interface PermissionDetail extends PermissionItem {
  /** 路由 */
  route?: string
  /** 服务地址 */
  serviceUri?: string
  /** 深度 */
  level?: number
}

/** 权限保存/更新参数 */
export interface PermissionSaveParam {
  /** 名称 */
  name: string
  /** 标识 */
  mark: string
  /** 类型 */
  type: PermissionType
  /** 上级 ID */
  parentId: string | number
  /** 图标 */
  icon?: string
  /** 路由 */
  route?: string
  /** 服务地址 */
  serviceUri?: string
  /** 排序 */
  sort?: number
  /** 深度 */
  level: number
}

/** 角色列表行（/system/role/list 返回元素） */
export interface RoleListItem {
  /** 角色 ID */
  id: string | number
  /** 名称 */
  name: string
  /** 描述 */
  description?: string
  /** 状态（0=禁用 1=启用） */
  status: number
  /** 更新时间 */
  updateTime?: string
}

/** 角色详情（/system/role/{id}/get） */
export interface RoleDetail {
  /** 名称 */
  name: string
  /** 描述 */
  description?: string
  /** 状态 */
  status: number
  /** 已绑定权限 ID 列表 */
  permissionIds?: (string | number)[]
}

/** 角色保存/更新参数 */
export interface RoleSaveParam {
  /** 名称 */
  name: string
  /** 描述 */
  description?: string
  /** 状态 */
  status: number
  /** 绑定权限 ID 列表 */
  bindPermissionIds?: (string | number)[]
  /** 解绑权限 ID 列表 */
  unbindPermissionIds?: (string | number)[]
}

/** 客户端列表行（/oauth/client/list 返回元素） */
export interface ClientListItem {
  /** 主键 ID */
  id: string | number
  /** 客户端 ID */
  clientId: string
  /** 名称 */
  clientName: string
  /** 授权令牌有效期（秒） */
  accessTokenValiditySeconds?: string
  /** 刷新令牌有效期（秒） */
  refreshTokenValiditySeconds?: string
  /** 状态（0=禁用 1=启用） */
  status: number
}

/** 客户端详情（/oauth/client/{id}/get） */
export interface ClientDetail {
  /** 客户端 ID */
  clientId: string
  /** 名称 */
  name: string
  /** 授权令牌有效期（秒） */
  accessTokenValiditySeconds?: string
  /** 刷新令牌有效期（秒） */
  refreshTokenValiditySeconds?: string
  /** 授权类型列表（0/1/2/3） */
  authorizationGrantTypes?: number[]
  /** 备注 */
  remark?: string
  /** 状态 */
  status: number
}

/** 客户端保存/更新参数 */
export interface ClientSaveParam {
  /** 名称 */
  name: string
  /** 客户端密钥 */
  clientSecret?: string
  /** 授权令牌有效期（秒） */
  accessTokenValiditySeconds?: string
  /** 刷新令牌有效期（秒） */
  refreshTokenValiditySeconds?: string
  /** 授权类型列表 */
  authorizationGrantTypes: number[]
  /** 备注 */
  remark?: string
  /** 状态 */
  status: number
}

/** 通用状态：0=禁用 1=启用 */
export enum Status {
  /** 禁用 */
  DISABLE = 0,
  /** 启用 */
  ENABLE = 1
}

/** 下拉选项（客户端/角色等通用） */
export interface OptionItem {
  /** ID */
  id: string | number
  /** 名称 */
  name: string
}

/** 分页结果 */
export interface PageResult<T> {
  /** 当前页 */
  current: string | number
  /** 每页数量 */
  size: string | number
  /** 数据总数 */
  total: string | number
  /** 数据列表 */
  data: T[]
}

/** 用户列表行（/system/user/list 返回元素） */
export interface UserListItem {
  /** 用户 ID */
  id: string | number
  /** 名称 */
  name: string
  /** 所属客户端 */
  client?: OptionItem
  /** 状态（0=禁用 1=启用） */
  status: number
  /** 创建时间 */
  createTime?: string
}

/** 用户详情（/system/user/{id}/get，用于编辑回显） */
export interface UserDetail {
  /** 账号 */
  username: string
  /** 名称 */
  name: string
  /** 图标 */
  icon: string
  /** 状态 */
  status: number
  /** 所属客户端 */
  client?: OptionItem
  /** 已绑定角色 ID 列表 */
  roleIds?: (string | number)[]
}

/** 用户保存/更新参数 */
export interface UserSaveParam {
  /** 账号 */
  username: string
  /** 名称 */
  name: string
  /** 密码 */
  password: string
  /** 图标 */
  icon: string
  /** 状态 */
  status: number
  /** 客户端 ID */
  clientId: string | number
  /** 绑定角色 ID 列表 */
  bindRoleIds?: (string | number)[]
  /** 解绑角色 ID 列表 */
  unbindRoleIds?: (string | number)[]
}
