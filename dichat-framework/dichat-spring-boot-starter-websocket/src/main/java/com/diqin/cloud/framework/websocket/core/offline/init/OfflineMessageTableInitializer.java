package com.diqin.cloud.framework.websocket.core.offline.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * 离线消息表自动初始化
 *
 * @author hanson
 */
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "dichat.websocket", name = "offline-message-enabled", havingValue = "true")
public class OfflineMessageTableInitializer {

    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void initTable() {
        try (Connection connection = dataSource.getConnection()) {
            // 检查表是否存在
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "ws_offline_message", new String[]{"TABLE"});

            if (!tables.next()) {
                // 表不存在，执行建表SQL
                log.info("[initTable] 开始创建离线消息表 ws_offline_message");
                ClassPathResource resource = new ClassPathResource("sql/ws_offline_message.sql");
                ScriptUtils.executeSqlScript(connection, resource);
                log.info("[initTable] 离线消息表 ws_offline_message 创建成功");
            } else {
                log.info("[initTable] 离线消息表 ws_offline_message 已存在");
            }
        } catch (Exception e) {
            log.error("[initTable] 初始化离线消息表失败", e);
        }
    }
}
