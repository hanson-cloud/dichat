package com.diqin.cloud.module.im.service.websocket;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.test.core.ut.BaseMockitoUnitTest;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.module.im.service.websocket.dto.ImGroupMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * IM WebSocket 推送 Service 单元测试
 *
 * @author hanson
 */
public class ImWebSocketServiceImplTest extends BaseMockitoUnitTest {

    /**
     * 异步推送实现（@Spy 真实对象 + mock sender）。
     * sendGroupMessage/sendPrivateMessage 真正去调 webSocketMessageSender.sendObject，
     * 这样用例里 verify(webSocketMessageSender).sendObject(...) 才能成立。
     * sender 由 setUp() 通过反射注入，因为 @Spy 不会自动装配 Spring 的 @Resource 字段。
     */
    @Spy
    private ImWebSocketAsyncServiceImpl asyncService;

    @InjectMocks
    private ImWebSocketServiceImpl imWebSocketService;

    @Mock
    private WebSocketMessageSender webSocketMessageSender;

    @BeforeEach
    public void setUp() {
        // 将 mock sender 注入到异步推送实现（@Spy 真实实例），
        // 使 sendGroupMessage/sendPrivateMessage 能真正调用 webSocketMessageSender.sendObject
        ReflectionTestUtils.setField(asyncService, "webSocketMessageSender", webSocketMessageSender);
    }

    @AfterEach
    public void tearDown() {
        // 清理事务同步上下文，避免串扰其它用例
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    // ========== sendPrivateMessageAsync ==========

    @Test
    public void testSendPrivateMessageAsync_noTransactionSendsImmediately() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImWebSocketServiceImpl.class)))
                    .thenReturn(imWebSocketService);

            // 准备
            ImPrivateMessageDTO dto = new ImPrivateMessageDTO().setSenderId(1L).setReceiverId(2L);

            // 调用：无事务，应立即发送
            imWebSocketService.sendPrivateMessageAsync(2L, dto);

            // 断言
            verify(webSocketMessageSender).sendObject(
                    eq(UserTypeEnum.MEMBER.getValue()), eq(2L), eq(ImPrivateMessageDTO.TYPE), eq(dto));
        }
    }

    @Test
    public void testSendPrivateMessageAsync_inTransactionDeferredUntilCommit() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImWebSocketServiceImpl.class)))
                    .thenReturn(imWebSocketService);

            // 准备：开启事务同步
            TransactionSynchronizationManager.initSynchronization();
            try {
                ImPrivateMessageDTO dto = new ImPrivateMessageDTO().setSenderId(1L).setReceiverId(2L);

                // 调用
                imWebSocketService.sendPrivateMessageAsync(2L, dto);

                // 断言：事务未提交，未推送
                verify(webSocketMessageSender, never()).sendObject(anyInt(), anyLong(), anyString(), any());

                // 模拟事务提交
                List<TransactionSynchronization> syncs =
                        TransactionSynchronizationManager.getSynchronizations();
                assertEquals(1, syncs.size());
                syncs.forEach(TransactionSynchronization::afterCommit);

                // 断言：提交后推送
                verify(webSocketMessageSender).sendObject(
                        eq(UserTypeEnum.MEMBER.getValue()), eq(2L), eq(ImPrivateMessageDTO.TYPE), eq(dto));
            } finally {
                TransactionSynchronizationManager.clear();
            }
        }
    }

    // ========== sendGroupMessageAsync ==========

    @Test
    public void testSendGroupMessageAsync_fanOutToAllUsers() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImWebSocketServiceImpl.class)))
                    .thenReturn(imWebSocketService);

            ImGroupMessageDTO dto = new ImGroupMessageDTO();
            dto.setGroupId(10L);
            dto.setSenderId(1L);

            imWebSocketService.sendGroupMessageAsync(ListUtil.of(1L, 2L, 3L), dto);

            verify(webSocketMessageSender).sendObject(
                    eq(UserTypeEnum.MEMBER.getValue()), eq(1L), eq(ImGroupMessageDTO.TYPE), eq(dto));
            verify(webSocketMessageSender).sendObject(
                    eq(UserTypeEnum.MEMBER.getValue()), eq(2L), eq(ImGroupMessageDTO.TYPE), eq(dto));
            verify(webSocketMessageSender).sendObject(
                    eq(UserTypeEnum.MEMBER.getValue()), eq(3L), eq(ImGroupMessageDTO.TYPE), eq(dto));
        }
    }

    @Test
    public void testSendGroupMessageAsync_senderExceptionDoesNotBreakOthers() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImWebSocketServiceImpl.class)))
                    .thenReturn(imWebSocketService);

            ImGroupMessageDTO dto = new ImGroupMessageDTO();
            dto.setGroupId(10L);
            // 给 1 号用户推送时抛异常，不能影响 2/3 号
            doThrow(new RuntimeException("user offline"))
                    .when(webSocketMessageSender).sendObject(anyInt(), eq(1L), anyString(), any());

            imWebSocketService.sendGroupMessageAsync(ListUtil.of(1L, 2L, 3L), dto);

            // 2L 和 3L 也都被推送
            verify(webSocketMessageSender).sendObject(anyInt(), eq(2L), anyString(), any());
            verify(webSocketMessageSender).sendObject(anyInt(), eq(3L), anyString(), any());
        }
    }

    @Test
    public void testSendGroupMessageAsync_emptyUserIds_noSend() {
        ImGroupMessageDTO dto = new ImGroupMessageDTO().setGroupId(10L);

        imWebSocketService.sendGroupMessageAsync(Collections.emptyList(), dto);
        // 显式强转，避免 null 在 Long / Collection<Long> 两个重载间歧义
        imWebSocketService.sendGroupMessageAsync((Collection<Long>) null, dto);

        verifyNoInteractions(webSocketMessageSender);
    }

    @Test
    public void testSendGroupMessageAsync_distinctUserIds() {
        ImGroupMessageDTO dto = new ImGroupMessageDTO().setGroupId(10L);

        imWebSocketService.sendGroupMessageAsync(Arrays.asList(1L, 2L, 1L, null), dto);

        verify(webSocketMessageSender).sendObject(
                eq(UserTypeEnum.MEMBER.getValue()), eq(1L), eq(ImGroupMessageDTO.TYPE), eq(dto));
        verify(webSocketMessageSender).sendObject(
                eq(UserTypeEnum.MEMBER.getValue()), eq(2L), eq(ImGroupMessageDTO.TYPE), eq(dto));
        verifyNoMoreInteractions(webSocketMessageSender);
    }

    @Test
    public void testSendPrivateMessageAsync_exceptionSwallowed() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImWebSocketServiceImpl.class)))
                    .thenReturn(imWebSocketService);

            // 准备：sender 抛异常
            ImPrivateMessageDTO dto = new ImPrivateMessageDTO().setSenderId(1L).setReceiverId(2L);
            doThrow(new RuntimeException("user offline"))
                    .when(webSocketMessageSender).sendObject(anyInt(), anyLong(), anyString(), any());

            // 调用：异常应被吞掉，不向上抛
            imWebSocketService.sendPrivateMessageAsync(2L, dto);

            verify(webSocketMessageSender).sendObject(anyInt(), eq(2L), anyString(), any());
        }
    }

    @Test
    public void testSendGroupMessageAsync_singleUserDefaultOverload() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImWebSocketServiceImpl.class)))
                    .thenReturn(imWebSocketService);

            ImGroupMessageDTO dto = new ImGroupMessageDTO();
            dto.setGroupId(10L);

            imWebSocketService.sendGroupMessageAsync(42L, dto);

            verify(webSocketMessageSender).sendObject(
                    eq(UserTypeEnum.MEMBER.getValue()), eq(42L), eq(ImGroupMessageDTO.TYPE), eq(dto));
        }
    }

}
