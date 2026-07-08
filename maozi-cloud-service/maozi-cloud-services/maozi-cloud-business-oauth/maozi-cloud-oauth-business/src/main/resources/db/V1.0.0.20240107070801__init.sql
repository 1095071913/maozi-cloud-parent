/*
 Navicat Premium Dump SQL

 Source Server         : 本地环境mysql
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : maozi-cloud-oauth-localhost-db

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 04/06/2026 10:45:14
*/

-- 设置字符集为utf8mb4，支持完整的Unicode字符（包括emoji表情）
SET NAMES utf8mb4;
-- 禁用外键检查，避免建表时因表之间的依赖关系导致报错
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 表结构：oauth2_registered_client（OAuth2已注册客户端表）
-- 用于存储OAuth2授权服务器中注册的客户端应用信息
-- ----------------------------

-- 客户端表
DROP TABLE IF EXISTS `oauth2_registered_client`;
CREATE TABLE `oauth2_registered_client` (
                                            `id` bigint NOT NULL AUTO_INCREMENT,                                              -- 主键ID，自增长
                                            `client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL, -- 客户端标识，用于唯一标识一个客户端应用
                                            `client_id_issued_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,                  -- 客户端ID签发时间，默认为当前时间
                                            `client_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL, -- 客户端密钥，用于客户端认证（通常使用bcrypt加密存储）
                                            `client_secret_expires_at` datetime DEFAULT NULL,                                  -- 客户端密钥过期时间，为NULL表示永不过期
                                            `client_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL, -- 客户端名称，用于描述客户端应用的名称
                                            `remark` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',              -- 备注信息，用于对客户端的附加说明
                                            `client_authentication_methods` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'client_secret_basic', -- 客户端认证方式，如client_secret_basic、client_secret_post等
                                            `authorization_grant_types` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '', -- 授权类型，如authorization_code、client_credentials、refresh_token、password等
                                            `redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '', -- 重定向URI列表，授权码模式下授权成功后的回调地址
                                            `post_logout_redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '', -- 登出后重定向URI列表，用户注销后的跳转地址
                                            `scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '', -- 授权范围列表，定义客户端可以请求的权限范围
                                            `client_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL, -- 客户端配置设置（JSON格式），包含是否需要授权确认等配置
                                            `token_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL, -- 令牌配置设置（JSON格式），包含令牌有效期、刷新策略、签名算法等配置
                                            `deleted` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0', -- 逻辑删除标识，0表示未删除，非0表示已删除
                                            `status` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1', -- 状态标识，1表示启用，其他值表示禁用
                                            `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,                        -- 创建时间，记录更新时自动更新时间
                                            PRIMARY KEY (`id`)                                                                 -- 主键索引
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- 表结构：oauth2_authorization_consent（用户授权确认表）
-- 用于记录用户对某个客户端应用的授权确认信息（即用户同意授权的权限范围）
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
CREATE TABLE oauth2_authorization_consent
(
    registered_client_id varchar(100)  NOT NULL, -- 关联的已注册客户端ID，标识哪个客户端应用
    principal_name       varchar(200)  NOT NULL, -- 授权用户的主体名称（通常是用户名或用户唯一标识）
    authorities          varchar(1000) NOT NULL, -- 用户授予的权限列表，记录用户同意授权的具体权限
    PRIMARY KEY (registered_client_id, principal_name) -- 联合主键，确保同一用户对同一客户端只有一条授权记录
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- 表结构：oauth2_authorization（OAuth2授权信息表）
-- 用于存储OAuth2授权过程中的完整授权信息，包括授权码、访问令牌、刷新令牌等
-- 是Spring Authorization Server的核心表，记录整个授权流程中的令牌数据
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization`;
CREATE TABLE oauth2_authorization
(
    id                            varchar(100) NOT NULL,  -- 授权记录唯一标识
    registered_client_id          varchar(100) NOT NULL,  -- 关联的已注册客户端ID
    principal_name                varchar(200) NOT NULL,  -- 授权用户的主体名称（用户名或唯一标识）
    authorization_grant_type      varchar(100) NOT NULL,  -- 授权类型，如authorization_code、client_credentials、refresh_token、password等
    authorized_scopes             varchar(1000) DEFAULT NULL, -- 已授权的范围列表
    attributes                    blob          DEFAULT NULL, -- 授权附加属性（序列化存储）
    state                         varchar(500)  DEFAULT NULL, -- 授权状态参数，用于防止CSRF攻击

    -- 以下为授权码（Authorization Code）相关字段
    authorization_code_value      blob          DEFAULT NULL, -- 授权码的值
    authorization_code_issued_at  DATETIME      DEFAULT NULL, -- 授权码签发时间
    authorization_code_expires_at DATETIME      DEFAULT NULL, -- 授权码过期时间
    authorization_code_metadata   blob          DEFAULT NULL, -- 授权码元数据（序列化存储）

    -- 以下为访问令牌（Access Token）相关字段
    access_token_value            blob          DEFAULT NULL, -- 访问令牌的值
    access_token_issued_at        DATETIME      DEFAULT NULL, -- 访问令牌签发时间
    access_token_expires_at       DATETIME      DEFAULT NULL, -- 访问令牌过期时间
    access_token_metadata         blob          DEFAULT NULL, -- 访问令牌元数据（序列化存储）
    access_token_type             varchar(100)  DEFAULT NULL, -- 访问令牌类型，如Bearer
    access_token_scopes           varchar(1000) DEFAULT NULL, -- 访问令牌包含的权限范围

    -- 以下为OIDC ID令牌（OpenID Connect ID Token）相关字段
    oidc_id_token_value           blob          DEFAULT NULL, -- OIDC ID令牌的值
    oidc_id_token_issued_at       DATETIME      DEFAULT NULL, -- OIDC ID令牌签发时间
    oidc_id_token_expires_at      DATETIME      DEFAULT NULL, -- OIDC ID令牌过期时间
    oidc_id_token_metadata        blob          DEFAULT NULL, -- OIDC ID令牌元数据（序列化存储）

    -- 以下为刷新令牌（Refresh Token）相关字段
    refresh_token_value           blob          DEFAULT NULL, -- 刷新令牌的值
    refresh_token_issued_at       DATETIME      DEFAULT NULL, -- 刷新令牌签发时间
    refresh_token_expires_at      DATETIME      DEFAULT NULL, -- 刷新令牌过期时间
    refresh_token_metadata        blob          DEFAULT NULL, -- 刷新令牌元数据（序列化存储）

    -- 以下为用户码（User Code）相关字段，用于设备授权流程
    user_code_value               blob          DEFAULT NULL, -- 用户码的值
    user_code_issued_at           DATETIME      DEFAULT NULL, -- 用户码签发时间
    user_code_expires_at          DATETIME      DEFAULT NULL, -- 用户码过期时间
    user_code_metadata            blob          DEFAULT NULL, -- 用户码元数据（序列化存储）

    -- 以下为设备码（Device Code）相关字段，用于设备授权流程
    device_code_value             blob          DEFAULT NULL, -- 设备码的值
    device_code_issued_at         DATETIME      DEFAULT NULL, -- 设备码签发时间
    device_code_expires_at        DATETIME      DEFAULT NULL, -- 设备码过期时间
    device_code_metadata          blob          DEFAULT NULL, -- 设备码元数据（序列化存储）

    PRIMARY KEY (id) -- 主键索引
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- 初始数据：oauth2_registered_client（OAuth2客户端初始数据）
-- 插入系统内置客户端配置，包含客户端认证方式、授权类型、令牌策略等完整配置
-- ----------------------------
BEGIN;
-- 插入系统内置客户端：client_id为system，支持refresh_token、client_credentials、password三种授权类型
-- 访问令牌有效期为7200秒（2小时），刷新令牌有效期为604800秒（7天），授权码有效期为300秒（5分钟）
INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, `client_name`, `remark`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, `post_logout_redirect_uris`, `scopes`, `client_settings`, `token_settings`, `deleted`, `status`, `create_time`) VALUES (1, 'system', '2026-03-06 16:28:46', '{bcrypt}$2a$10$N94nFJjiGDJDC2D2lkj8nOWWHrxyl8KhtsnmvkW2vH0z0EGagM6Ne', NULL, '系统内置', '', 'client_secret_basic', 'refresh_token,client_credentials,password', '', '', '', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",7200.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"reference\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",604800.000000000],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}', '0', '1', '2026-06-04 02:27:21');
COMMIT;

-- 恢复外键检查，确保后续数据库操作的参照完整性
SET FOREIGN_KEY_CHECKS = 1;
