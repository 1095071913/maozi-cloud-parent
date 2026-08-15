-- ============================================================
-- Seata AT 模式 undo_log 表初始化脚本
-- ------------------------------------------------------------
-- 用途：Seata AT 模式在分支事务本地数据源中需依赖 undo_log 表
--       记录修改前后的回滚影像，用于全局事务回滚时逆向恢复数据。
-- 执行方式：由 Flyway 在应用启动时按版本号自动执行；
--           库中每个参与 AT 模式的业务库均需独立存在该表。
-- 字段说明详见各列 COMMENT。
-- ============================================================
CREATE TABLE `undo_log` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `branch_id` bigint(20) NOT NULL,
                            `xid` varchar(100) NOT NULL,
                            `context` varchar(128) NOT NULL,
                            `rollback_info` longblob NOT NULL,
                            `log_status` int(11) NOT NULL,
                            `log_created` datetime NOT NULL,
                            `log_modified` datetime NOT NULL,
                            `ext` varchar(100) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;