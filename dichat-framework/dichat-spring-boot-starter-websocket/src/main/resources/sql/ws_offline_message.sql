-- 创建离线消息表
CREATE TABLE IF NOT EXISTS ws_offline_message
(
    id              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    user_type       INT         NOT NULL COMMENT '用户类型',
    user_id         BIGINT      NOT NULL COMMENT '用户ID',
    message_type    VARCHAR(64) NOT NULL COMMENT '消息类型',
    message_content TEXT COMMENT '消息内容',
    expire_time     DATETIME             DEFAULT NULL COMMENT '过期时间',
    sent            TINYINT(1)  NOT NULL DEFAULT '0' COMMENT '是否已发送',
    send_time       DATETIME             DEFAULT NULL COMMENT '发送时间',
    retry_count     INT         NOT NULL DEFAULT '0' COMMENT '重试次数',
    max_retry_count INT         NOT NULL DEFAULT '3' COMMENT '最大重试次数',
    creator         VARCHAR(64)          DEFAULT '' COMMENT '创建者',
    create_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater         VARCHAR(64)          DEFAULT '' COMMENT '更新者',
    update_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id),
    -- ⭐ 核心：拉取某用户未发送离线消息
    KEY idx_pull_user (user_type, user_id, sent, deleted, create_time),
    -- ⭐ 定时清理过期 / 重试失败消息
    KEY idx_expire_retry (sent, expire_time, retry_count)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='WebSocket 离线消息表';
