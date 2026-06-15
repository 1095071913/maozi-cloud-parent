-- seata undolog
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