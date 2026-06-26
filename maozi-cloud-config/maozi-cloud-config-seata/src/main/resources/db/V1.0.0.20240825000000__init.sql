-- ============================================================
-- Seata AT 模式 undo_log 表初始化脚本
-- ------------------------------------------------------------
-- 用途：Seata AT 模式在分支事务本地数据源中需依赖 undo_log 表
--       记录修改前后的回滚影像，用于全局事务回滚时逆向恢复数据。
-- 执行方式：由 Flyway 在应用启动时按版本号自动执行；
--           库中每个参与 AT 模式的业务库均需独立存在该表。
-- 字段说明详见各列 COMMENT。
-- ============================================================
CREATE TABLE undo_log (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key',
    branch_id BIGINT NOT NULL COMMENT 'branch transaction id',
    xid VARCHAR(255) NOT NULL COMMENT 'global transaction id',
    context VARCHAR(255) COMMENT 'undo_log context, such as serialization',
    rollback_info LONGBLOB COMMENT 'rollback info',
    log_status INT NOT NULL COMMENT '0: normal status, 1: defense status',
    log_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'create datetime',
    log_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modify datetime'
) COMMENT='seata_undolog';