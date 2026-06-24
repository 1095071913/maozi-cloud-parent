package com.maozi.db.constant;

/**
 * 数据库表字段名常量
 * <p>
 * 集中维护跨模块复用的数据库列名，避免 SQL / MyBatis-Plus 查询中硬编码字符串。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/16 18:33
 */
public final class TableColumnName {

    /** OAuth2 客户端表的外键列名，对应用户与客户端的关联字段 */
    public final static String CLIENT_ID = "client_id";

}
