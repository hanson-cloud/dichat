package com.diqin.cloud.module.im.service.group;

import cn.hutool.extra.spring.SpringUtil;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.exception.ServiceException;
import com.diqin.cloud.framework.test.core.ut.BaseMockitoUnitTest;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupManagerBanReqVO;
import com.diqin.cloud.module.im.controller.app.group.vo.*;
import com.diqin.cloud.module.im.controller.app.group.vo.member.AppImGroupMemberInviteReqVO;
import com.diqin.cloud.module.im.controller.app.group.vo.member.AppImGroupMemberRemoveReqVO;
import com.diqin.cloud.module.im.dal.dataobject.friend.ImFriendDO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupDO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupMemberDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.group.ImGroupMapper;
import com.diqin.cloud.module.im.enums.group.ImGroupAddSourceEnum;
import com.diqin.cloud.module.im.enums.group.ImGroupMemberRoleEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import com.diqin.cloud.module.im.service.friend.ImFriendService;
import com.diqin.cloud.module.im.service.message.ImGroupMessageService;
import com.diqin.cloud.module.im.service.message.dto.ImGroupMessageSendDTO;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.diqin.cloud.module.im.service.websocket.ImWebSocketService;
import com.diqin.cloud.module.im.service.websocket.dto.ImGroupMessageDTO;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * IM 群 Service 单元测试
 *
 * @author hanson
 */
public class ImGroupServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ImGroupServiceImpl groupService;

    @Mock
    private ImGroupMapper groupMapper;
    @Mock
    private ImGroupMemberService groupMemberService;
    @Mock
    private ImGroupMessageService groupMessageService;
    @Mock
    private ImWebSocketService webSocketService;
    @Mock
    private ImFriendService friendService;
    @Mock
    private ImGroupRequestService groupRequestService;
    @Mock
    private ImUserService userService;

    /** 用真实实例避免 NPE；默认值与生产保持一致（maxMember=500、adminMaxCount=3、pinMaxCount=5） */
    @Spy
    private ImProperties imProperties = new ImProperties();

    // ========== createGroup ==========

    @Test
    public void testCreateGroup_success() {
        // 准备：仅创建者，无初始成员
        AppImGroupCreateReqVO reqVO = new AppImGroupCreateReqVO();
        reqVO.setName("测试群");
        when(groupMapper.insert(any(ImGroupDO.class))).thenAnswer(invocation -> {
            ImGroupDO group = invocation.getArgument(0);
            group.setId(100L);
            return 1;
        });

        // 调用
        ImGroupDO result = groupService.createGroup(reqVO, 1L);

        // 断言：群主 + 状态
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getOwnerUserId());
        assertEquals(CommonStatusEnum.ENABLE.getStatus(), result.getStatus());
        // 验证：群主加入群（带 OWNER role）+ 不调批量加成员（无初始成员）
        verify(groupMemberService).addGroupMember(100L, 1L, ImGroupMemberRoleEnum.OWNER.getRole());
        verify(groupMemberService, never()).addGroupMembers(anyLong(), anyCollection());
        // 验证：推送 GROUP_CREATE 通知（payload memberUserIds 含创建者自己）
        ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
        verify(groupMessageService).sendGroupMessage(eq(1L), anyCollection(), dtoCaptor.capture());
        assertEquals(ImMessageTypeEnum.GROUP_CREATE.getType(), dtoCaptor.getValue().getType());
    }

    @Test
    public void testCreateGroup_withInitialMembers() {
        // 准备：创建者 + 2 个初始成员，都是好友
        AppImGroupCreateReqVO reqVO = new AppImGroupCreateReqVO();
        reqVO.setName("测试群");
        reqVO.setMemberUserIds(new ArrayList<>(List.of(2L, 3L)));
        when(groupMapper.insert(any(ImGroupDO.class))).thenAnswer(invocation -> {
            ImGroupDO group = invocation.getArgument(0);
            group.setId(100L);
            return 1;
        });
        when(friendService.getActiveFriendList(eq(1L), anyCollection())).thenReturn(List.of(
                ImFriendDO.builder().userId(1L).friendUserId(2L)
                        .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                ImFriendDO.builder().userId(1L).friendUserId(3L)
                        .status(CommonStatusEnum.ENABLE.getStatus()).build()
        ));

        // 调用
        ImGroupDO result = groupService.createGroup(reqVO, 1L);

        // 断言：群创建成功 + 创建者 + 初始成员都加入
        assertEquals(100L, result.getId());
        verify(groupMemberService).addGroupMember(100L, 1L, ImGroupMemberRoleEnum.OWNER.getRole());
        verify(groupMemberService).addGroupMembers(eq(100L), anyCollection(),
                eq(ImGroupAddSourceEnum.INVITE.getSource()), eq(1L));
        // 验证：推送 GROUP_CREATE 通知，payload memberUserIds 含全员（创建者 + 邀请）
        ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
        verify(groupMessageService).sendGroupMessage(eq(1L), anyCollection(), dtoCaptor.capture());
        assertEquals(ImMessageTypeEnum.GROUP_CREATE.getType(), dtoCaptor.getValue().getType());
    }

    @Test
    public void testCreateGroup_initialMemberNotFriend() {
        // 准备：初始成员里有非好友
        AppImGroupCreateReqVO reqVO = new AppImGroupCreateReqVO();
        reqVO.setName("测试群");
        reqVO.setMemberUserIds(new ArrayList<>(List.of(2L, 3L)));
        // 只有 2 是好友，3 不是
        when(friendService.getActiveFriendList(eq(1L), anyCollection())).thenReturn(List.of(
                ImFriendDO.builder().userId(1L).friendUserId(2L)
                        .status(CommonStatusEnum.ENABLE.getStatus()).build()));
        ImUserDO u3 = new ImUserDO();
        u3.setId(3L);
        u3.setNickname("李四");
        when(userService.getUserMap(anyCollection())).thenReturn(Map.of(3L, u3));

        // 调用并断言
        ServiceException exception = assertThrows(ServiceException.class,
                () -> groupService.createGroup(reqVO, 1L));
        assertEquals(GROUP_INVITE_NOT_FRIEND.code(), exception.getCode());
        verify(groupMapper, never()).insert(any(ImGroupDO.class));
    }

    // ========== updateGroup ==========

    @Test
    public void testUpdateGroup_notOwner() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备：当前用户不是群主
            ImGroupDO group = ImGroupDO.builder().id(10L).name("群").ownerUserId(99L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            AppImGroupUpdateReqVO reqVO = new AppImGroupUpdateReqVO();
            reqVO.setId(10L);
            reqVO.setName("新名字");

            // 调用并断言
            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.updateGroup(reqVO, 1L));
            assertEquals(GROUP_NOT_OWNER.code(), exception.getCode());
        }
    }

    @Test
    public void testUpdateGroup_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备
            ImGroupDO group = ImGroupDO.builder().id(10L).name("旧名字").ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            AppImGroupUpdateReqVO reqVO = new AppImGroupUpdateReqVO();
            reqVO.setId(10L);
            reqVO.setName("新名字");

            // 调用
            ImGroupDO result = groupService.updateGroup(reqVO, 1L);

            // 断言：更新了数据库
            verify(groupMapper).updateById(any(ImGroupDO.class));
            assertEquals("新名字", result.getName());
            // 推送 GROUP_NAME_UPDATE 通知给全员
            ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
            verify(groupMessageService).sendGroupMessage(eq(1L), anyCollection(), dtoCaptor.capture());
            assertEquals(ImMessageTypeEnum.GROUP_NAME_UPDATE.getType(), dtoCaptor.getValue().getType());
        }
    }

    // ========== dissolveGroup ==========

    @Test
    public void testDissolveGroup_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备
            ImGroupDO group = ImGroupDO.builder().id(10L).name("群").ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            // 调用
            groupService.dissolveGroup(10L, 1L);

            // 断言：群状态变为 DISABLE + 群成员全部移除 + 清理已读缓存
            ArgumentCaptor<ImGroupDO> captor = ArgumentCaptor.forClass(ImGroupDO.class);
            verify(groupMapper).updateById(captor.capture());
            assertEquals(CommonStatusEnum.DISABLE.getStatus(), captor.getValue().getStatus());
            assertNotNull(captor.getValue().getDissolvedTime());
            verify(groupMemberService).removeGroupMembersByGroupId(10L);
            verify(groupMessageService).deleteReadMaxMessageIdMap(10L);
            // 推送 GROUP_DISSOLVE 通知（send-before-remove，sendGroupMessage 内部查 active 自动覆盖全员，含群主多端同步）
            ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
            verify(groupMessageService).sendGroupMessage(eq(1L), dtoCaptor.capture());
            assertEquals(ImMessageTypeEnum.GROUP_DISSOLVE.getType(), dtoCaptor.getValue().getType());
        }
    }

    @Test
    public void testDissolveGroup_banned_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备：已封禁但未解散的群，群主仍可解散
            ImGroupDO group = ImGroupDO.builder().id(10L).name("群").ownerUserId(1L)
                    .banned(true).status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            // 调用
            groupService.dissolveGroup(10L, 1L);

            // 断言：封禁状态不阻止解散
            verify(groupMapper).updateById(argThat((ImGroupDO update) ->
                    CommonStatusEnum.DISABLE.getStatus().equals(update.getStatus())));
            verify(groupMemberService).removeGroupMembersByGroupId(10L);
        }
    }

    @Test
    public void testDissolveGroupByManager_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备：管理员解散封禁群，不要求管理员是群主
            ImGroupDO group = ImGroupDO.builder().id(10L).name("群").ownerUserId(1L)
                    .banned(true).status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            // 调用
            groupService.dissolveGroupByManager(99L, 10L);

            // 断言：使用管理员编号发通知并完成清理
            ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
            verify(groupMessageService).sendGroupMessage(eq(99L), dtoCaptor.capture());
            assertEquals(ImMessageTypeEnum.GROUP_DISSOLVE.getType(), dtoCaptor.getValue().getType());
            verify(groupMemberService).removeGroupMembersByGroupId(10L);
            verify(groupMessageService).deleteReadMaxMessageIdMap(10L);
        }
    }

    @Test
    public void testDissolveGroup_notOwner() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(99L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.dissolveGroup(10L, 1L));
            assertEquals(GROUP_NOT_OWNER.code(), exception.getCode());
        }
    }

    // ========== inviteGroupMember ==========

    @Test
    public void testInviteGroupMember_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备：群存在 + joinApproval=false（自由进群） + 当前用户是群主
            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .joinApproval(false)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build());

            // 当前成员只有群主
            List<ImGroupMemberDO> activeMembers = new ArrayList<>();
            activeMembers.add(ImGroupMemberDO.builder().groupId(10L).userId(1L)
                    .role(ImGroupMemberRoleEnum.OWNER.getRole())
                    .status(CommonStatusEnum.ENABLE.getStatus()).build());
            when(groupMemberService.getActiveGroupMemberListByGroupId(10L)).thenReturn(activeMembers);

            // 被邀请人 2 和 3 都是好友
            AppImGroupMemberInviteReqVO reqVO = new AppImGroupMemberInviteReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(new ArrayList<>(List.of(2L, 3L)));
            List<ImFriendDO> friends = List.of(
                    ImFriendDO.builder().userId(1L).friendUserId(2L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImFriendDO.builder().userId(1L).friendUserId(3L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()
            );
            when(friendService.getActiveFriendList(eq(1L), anyCollection())).thenReturn(friends);

            // 调用
            groupService.inviteGroupMember(1L, reqVO);

            // 断言：校验群成员 + 批量添加成员（带 INVITE 来源 + 邀请人）+ 推送 GROUP_MEMBER_INVITE
            verify(groupMemberService).validateMemberInGroup(10L, 1L);
            verify(groupMemberService).addGroupMembers(eq(10L), anyCollection(),
                    eq(ImGroupAddSourceEnum.INVITE.getSource()), eq(1L));
            verify(groupRequestService, never()).createInviteRequestList(anyLong(), anyLong(), anyCollection());
            verify(webSocketService, never()).sendGroupMessageAsync(anyCollection(), any(ImGroupMessageDTO.class));
            ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
            verify(groupMessageService).sendGroupMessage(eq(1L), anyCollection(), dtoCaptor.capture());
            assertEquals(ImMessageTypeEnum.GROUP_MEMBER_INVITE.getType(), dtoCaptor.getValue().getType());
        }
    }

    @Test
    public void testInviteGroupMember_approval_normalRoutesToApproval() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备：joinApproval=true，开启审批；普通成员邀请走审批，落 group_request
            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(99L)
                    .joinApproval(true)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build());
            when(groupMemberService.getActiveGroupMemberListByGroupId(10L)).thenReturn(List.of(
                    ImGroupMemberDO.builder().groupId(10L).userId(99L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            AppImGroupMemberInviteReqVO reqVO = new AppImGroupMemberInviteReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(new ArrayList<>(List.of(2L, 3L)));
            when(friendService.getActiveFriendList(eq(1L), anyCollection())).thenReturn(List.of(
                    ImFriendDO.builder().userId(1L).friendUserId(2L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImFriendDO.builder().userId(1L).friendUserId(3L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            // 调用
            groupService.inviteGroupMember(1L, reqVO);

            // 断言：走审批分支：调 createInviteRequestList；不写群成员、不推 1509
            verify(groupRequestService).createInviteRequestList(eq(10L), eq(1L), anyCollection());
            verify(groupMemberService, never()).addGroupMembers(anyLong(), anyCollection(), any(), any());
            verify(groupMessageService, never()).sendGroupMessage(anyLong(), any(ImGroupMessageSendDTO.class));
        }
    }

    @Test
    public void testInviteGroupMember_approval_ownerBypassesApproval() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备：joinApproval=true，但邀请人是群主；视同已审批，直进群
            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .joinApproval(true)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build());
            when(groupMemberService.getActiveGroupMemberListByGroupId(10L)).thenReturn(List.of(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            AppImGroupMemberInviteReqVO reqVO = new AppImGroupMemberInviteReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(new ArrayList<>(List.of(2L, 3L)));
            when(friendService.getActiveFriendList(eq(1L), anyCollection())).thenReturn(List.of(
                    ImFriendDO.builder().userId(1L).friendUserId(2L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImFriendDO.builder().userId(1L).friendUserId(3L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            // 调用
            groupService.inviteGroupMember(1L, reqVO);

            // 断言：绕过审批，直接 addGroupMembers + 推 1509；不落 group_request
            verify(groupRequestService, never()).createInviteRequestList(anyLong(), anyLong(), anyCollection());
            verify(groupMemberService).addGroupMembers(eq(10L), anyCollection(),
                    eq(ImGroupAddSourceEnum.INVITE.getSource()), eq(1L));
            ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
            verify(groupMessageService).sendGroupMessage(eq(1L), anyCollection(), dtoCaptor.capture());
            assertEquals(ImMessageTypeEnum.GROUP_MEMBER_INVITE.getType(), dtoCaptor.getValue().getType());
        }
    }

    @Test
    public void testInviteGroupMember_notFriend() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            when(groupMemberService.getActiveGroupMemberListByGroupId(10L))
                    .thenReturn(List.of(ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            AppImGroupMemberInviteReqVO reqVO = new AppImGroupMemberInviteReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(new ArrayList<>(List.of(2L, 3L)));
            // 只有 2 是好友，3 不是
            when(friendService.getActiveFriendList(eq(1L), anyCollection())).thenReturn(List.of(
                    ImFriendDO.builder().userId(1L).friendUserId(2L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));
            ImUserDO u3 = new ImUserDO(); u3.setId(3L); u3.setNickname("李四");
            when(userService.getUserMap(anyCollection())).thenReturn(Map.of(3L, u3));

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.inviteGroupMember(1L, reqVO));
            assertEquals(GROUP_INVITE_NOT_FRIEND.code(), exception.getCode());
        }
    }

    @Test
    public void testInviteGroupMember_memberExceed() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备：群 10 已有 499 人（逼近 MAX_GROUP_MEMBER=500），再邀请 2 人 → 501 超限
            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            List<ImGroupMemberDO> activeMembers = new ArrayList<>();
            for (long i = 1; i <= 499; i++) {
                activeMembers.add(ImGroupMemberDO.builder().groupId(10L).userId(i)
                        .status(CommonStatusEnum.ENABLE.getStatus()).build());
            }
            when(groupMemberService.getActiveGroupMemberListByGroupId(10L)).thenReturn(activeMembers);

            AppImGroupMemberInviteReqVO reqVO = new AppImGroupMemberInviteReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(new ArrayList<>(List.of(600L, 601L)));
            // 被邀请人都是好友
            when(friendService.getActiveFriendList(eq(1L), anyCollection())).thenReturn(List.of(
                    ImFriendDO.builder().userId(1L).friendUserId(600L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImFriendDO.builder().userId(1L).friendUserId(601L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()
            ));

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.inviteGroupMember(1L, reqVO));
            assertEquals(GROUP_MEMBER_EXCEED.code(), exception.getCode());
            // 断言：不加成员、不推送
            verify(groupMemberService, never()).addGroupMembers(anyLong(), anyCollection());
        }
    }

    @Test
    public void testInviteGroupMember_skipsMembersAlreadyInGroup() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            // 准备
            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            // 用户 2 已在群中
            when(groupMemberService.getActiveGroupMemberListByGroupId(10L)).thenReturn(List.of(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImGroupMemberDO.builder().groupId(10L).userId(2L)
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()
            ));

            AppImGroupMemberInviteReqVO reqVO = new AppImGroupMemberInviteReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(new ArrayList<>(List.of(2L))); // 只邀请 2，他已在群中

            // 调用
            groupService.inviteGroupMember(1L, reqVO);

            // 断言：不会触发添加、不会推送
            verify(groupMemberService, never()).addGroupMembers(anyLong(), anyCollection());
            verify(webSocketService, never()).sendGroupMessageAsync(anyCollection(), any(ImGroupMessageDTO.class));
        }
    }

    // ========== quitGroup ==========

    @Test
    public void testQuitGroup_ownerCannotQuit() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.quitGroup(10L, 1L));
            assertEquals(GROUP_OWNER_CANNOT_QUIT.code(), exception.getCode());
        }
    }

    @Test
    public void testQuitGroup_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(99L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            groupService.quitGroup(10L, 1L);

            verify(groupMemberService).removeGroupMember(10L, 1L);
            verify(groupMessageService).deleteReadMaxMessageId(10L, 1L);
            // 推送 GROUP_MEMBER_QUIT 通知给全员（含 quitter，前端自判清群）
            ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
            verify(groupMessageService).sendGroupMessage(eq(1L), dtoCaptor.capture());
            assertEquals(ImMessageTypeEnum.GROUP_MEMBER_QUIT.getType(), dtoCaptor.getValue().getType());
        }
    }

    @Test
    public void testQuitGroup_bannedGroupAllowed() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(99L)
                    .banned(true).status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            groupService.quitGroup(10L, 1L);

            verify(groupMemberService).removeGroupMember(10L, 1L);
            verify(groupMessageService).deleteReadMaxMessageId(10L, 1L);
            verify(groupMessageService).sendGroupMessage(eq(1L), any(ImGroupMessageSendDTO.class));
        }
    }

    // ========== removeGroupMember ==========

    @Test
    public void testRemoveGroupMember_cannotRemoveSelf() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole()).build());

            AppImGroupMemberRemoveReqVO reqVO = new AppImGroupMemberRemoveReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(List.of(1L, 2L));

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.removeGroupMember(1L, reqVO));
            assertEquals(GROUP_CANNOT_REMOVE_SELF.code(), exception.getCode());
        }
    }

    @Test
    public void testRemoveGroupMember_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            // 操作者：群主
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole()).build());
            // 目标：两个普通成员
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().groupId(10L).userId(2L)
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImGroupMemberDO.builder().groupId(10L).userId(3L)
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            AppImGroupMemberRemoveReqVO reqVO = new AppImGroupMemberRemoveReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(List.of(2L, 3L));

            groupService.removeGroupMember(1L, reqVO);

            verify(groupMemberService).removeGroupMembers(eq(10L), anyCollection());
            verify(groupMessageService).deleteReadMaxMessageIds(eq(10L), anyCollection());
            // 推送 GROUP_MEMBER_KICK 通知给全员（含被踢者，前端自判清群）
            ArgumentCaptor<ImGroupMessageSendDTO> dtoCaptor = ArgumentCaptor.forClass(ImGroupMessageSendDTO.class);
            verify(groupMessageService).sendGroupMessage(eq(1L), dtoCaptor.capture());
            assertEquals(ImMessageTypeEnum.GROUP_MEMBER_KICK.getType(), dtoCaptor.getValue().getType());
        }
    }

    @Test
    public void testRemoveGroupMember_adminCannotRemoveAdmin() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(99L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            // 操作者：管理员
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.ADMIN.getRole()).build());
            // 目标：另一个管理员
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().groupId(10L).userId(2L)
                            .role(ImGroupMemberRoleEnum.ADMIN.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            AppImGroupMemberRemoveReqVO reqVO = new AppImGroupMemberRemoveReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(List.of(2L));

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.removeGroupMember(1L, reqVO));
            assertEquals(GROUP_REMOVE_ADMIN_DENIED.code(), exception.getCode());
            verify(groupMemberService, never()).removeGroupMembers(anyLong(), anyCollection());
        }
    }

    @Test
    public void testRemoveGroupMember_ownerCannotBeRemoved() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(99L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            // 操作者：群主（不能踢自己；这里换成另一个 userId 的群主语义 — 用 ADMIN 操作群主）
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole()).build());
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().groupId(10L).userId(99L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build()));

            AppImGroupMemberRemoveReqVO reqVO = new AppImGroupMemberRemoveReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(List.of(99L));

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.removeGroupMember(1L, reqVO));
            assertEquals(GROUP_REMOVE_OWNER_DENIED.code(), exception.getCode());
        }
    }

    @Test
    public void testRemoveGroupMember_skipInactiveTargets() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            // 操作者：群主
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.OWNER.getRole()).build());
            // 目标：2L 有效普通成员；3L 已退群（DISABLE）的历史管理员，应被跳过而非拦截整批
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().groupId(10L).userId(2L)
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole())
                            .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                    ImGroupMemberDO.builder().groupId(10L).userId(3L)
                            .role(ImGroupMemberRoleEnum.ADMIN.getRole())
                            .status(CommonStatusEnum.DISABLE.getStatus()).build()));

            AppImGroupMemberRemoveReqVO reqVO = new AppImGroupMemberRemoveReqVO();
            reqVO.setGroupId(10L);
            reqVO.setMemberUserIds(List.of(2L, 3L));

            groupService.removeGroupMember(1L, reqVO);

            // 仅有效成员 2L 进入移除 / 已读清理，已退群的 3L 被跳过
            ArgumentCaptor<Collection> removeCaptor = ArgumentCaptor.forClass(Collection.class);
            verify(groupMemberService).removeGroupMembers(eq(10L), removeCaptor.capture());
            assertEquals(Set.of(2L), Set.copyOf(removeCaptor.getValue()));
            ArgumentCaptor<Collection> readCaptor = ArgumentCaptor.forClass(Collection.class);
            verify(groupMessageService).deleteReadMaxMessageIds(eq(10L), readCaptor.capture());
            assertEquals(Set.of(2L), Set.copyOf(readCaptor.getValue()));
        }
    }

    // ========== addGroupAdmin ==========

    @Test
    public void testAddGroupAdmin_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);
            // 目标 3 是普通成员
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().userId(3L).status(CommonStatusEnum.ENABLE.getStatus())
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole()).build()));
            // 群里已有 1 个 ADMIN，1 + 1 ≤ 3 不超上限
            when(groupMemberService.getGroupMemberCountByRole(10L, ImGroupMemberRoleEnum.ADMIN.getRole()))
                    .thenReturn(1L);
            when(groupMemberService.updateGroupMemberRole(eq(10L), anyCollection(),
                    eq(ImGroupMemberRoleEnum.ADMIN.getRole()))).thenReturn(1);

            AppImGroupAdminAddReqVO reqVO = new AppImGroupAdminAddReqVO();
            reqVO.setId(10L);
            reqVO.setUserIds(List.of(3L));

            groupService.addGroupAdmin(1L, reqVO);

            verify(groupMemberService).updateGroupMemberRole(eq(10L), argThat((Set<Long> ids) ->
                            ids.size() == 1 && ids.contains(3L)),
                    eq(ImGroupMemberRoleEnum.ADMIN.getRole()));
        }
    }

    @Test
    public void testAddGroupAdmin_exceedsLimit() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().userId(5L).status(CommonStatusEnum.ENABLE.getStatus())
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole()).build()));
            // 群里已有 3 个 ADMIN（达到上限），再加 1 会超
            when(groupMemberService.getGroupMemberCountByRole(10L, ImGroupMemberRoleEnum.ADMIN.getRole()))
                    .thenReturn(3L);

            AppImGroupAdminAddReqVO reqVO = new AppImGroupAdminAddReqVO();
            reqVO.setId(10L);
            reqVO.setUserIds(List.of(5L));

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.addGroupAdmin(1L, reqVO));
            assertEquals(GROUP_ADMIN_MAX_LIMIT.code(), exception.getCode());
            verify(groupMemberService, never()).updateGroupMemberRole(anyLong(), anyCollection(), anyInt());
        }
    }

    @Test
    public void testAddGroupAdmin_targetIsOwner() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().userId(1L).status(CommonStatusEnum.ENABLE.getStatus())
                            .role(ImGroupMemberRoleEnum.OWNER.getRole()).build()));

            AppImGroupAdminAddReqVO reqVO = new AppImGroupAdminAddReqVO();
            reqVO.setId(10L);
            reqVO.setUserIds(List.of(1L));

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.addGroupAdmin(1L, reqVO));
            assertEquals(GROUP_ADMIN_TARGET_IS_OWNER.code(), exception.getCode());
        }
    }

    @Test
    public void testAddGroupAdmin_idempotentSkipWhenAlreadyAdmin() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);
            // 目标已是 ADMIN：再加无需操作
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().userId(2L).status(CommonStatusEnum.ENABLE.getStatus())
                            .role(ImGroupMemberRoleEnum.ADMIN.getRole()).build()));

            AppImGroupAdminAddReqVO reqVO = new AppImGroupAdminAddReqVO();
            reqVO.setId(10L);
            reqVO.setUserIds(List.of(2L));

            groupService.addGroupAdmin(1L, reqVO);

            verify(groupMemberService, never()).updateGroupMemberRole(anyLong(), anyCollection(), anyInt());
            verify(groupMemberService, never()).getGroupMemberCountByRole(anyLong(), anyInt());
        }
    }

    // ========== removeGroupAdmin ==========

    @Test
    public void testRemoveGroupAdmin_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().userId(2L).status(CommonStatusEnum.ENABLE.getStatus())
                            .role(ImGroupMemberRoleEnum.ADMIN.getRole()).build()));
            when(groupMemberService.updateGroupMemberRole(eq(10L), anyCollection(),
                    eq(ImGroupMemberRoleEnum.NORMAL.getRole()))).thenReturn(1);

            AppImGroupAdminRemoveReqVO reqVO = new AppImGroupAdminRemoveReqVO();
            reqVO.setId(10L);
            reqVO.setUserIds(List.of(2L));

            groupService.removeGroupAdmin(1L, reqVO);

            verify(groupMemberService).updateGroupMemberRole(eq(10L), argThat((Set<Long> ids) ->
                            ids.size() == 1 && ids.contains(2L)),
                    eq(ImGroupMemberRoleEnum.NORMAL.getRole()));
        }
    }

    @Test
    public void testRemoveGroupAdmin_idempotentSkipWhenAlreadyMember() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);
            // 目标已是 MEMBER：撤销无需操作
            when(groupMemberService.getGroupMembers(eq(10L), anyCollection())).thenReturn(List.of(
                    ImGroupMemberDO.builder().userId(2L).status(CommonStatusEnum.ENABLE.getStatus())
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole()).build()));

            AppImGroupAdminRemoveReqVO reqVO = new AppImGroupAdminRemoveReqVO();
            reqVO.setId(10L);
            reqVO.setUserIds(List.of(2L));

            groupService.removeGroupAdmin(1L, reqVO);

            verify(groupMemberService, never()).updateGroupMemberRole(anyLong(), anyCollection(), anyInt());
        }
    }

    // ========== transferGroupOwner ==========

    @Test
    public void testTransferGroupOwner_success() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);
            when(groupMemberService.validateMemberInGroup(10L, 2L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(2L)
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole()).build());
            when(groupMemberService.updateGroupMemberRole(eq(10L), eq(Set.of(2L)),
                    eq(ImGroupMemberRoleEnum.OWNER.getRole()))).thenReturn(1);
            when(groupMemberService.updateGroupMemberRole(eq(10L), eq(Set.of(1L)),
                    eq(ImGroupMemberRoleEnum.NORMAL.getRole()))).thenReturn(1);

            AppImGroupTransferOwnerReqVO reqVO = new AppImGroupTransferOwnerReqVO();
            reqVO.setId(10L);
            reqVO.setNewOwnerUserId(2L);

            groupService.transferGroupOwner(1L, reqVO);

            // 群表 owner 切换
            ArgumentCaptor<ImGroupDO> groupCaptor = ArgumentCaptor.forClass(ImGroupDO.class);
            verify(groupMapper).updateById(groupCaptor.capture());
            assertEquals(2L, groupCaptor.getValue().getOwnerUserId());
            // 旧群主 → MEMBER；新群主 → OWNER
            verify(groupMemberService).updateGroupMemberRole(eq(10L), eq(Set.of(1L)),
                    eq(ImGroupMemberRoleEnum.NORMAL.getRole()));
            verify(groupMemberService).updateGroupMemberRole(eq(10L), eq(Set.of(2L)),
                    eq(ImGroupMemberRoleEnum.OWNER.getRole()));
        }
    }

    @Test
    public void testTransferGroupOwner_toSelf() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(1L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);

            AppImGroupTransferOwnerReqVO reqVO = new AppImGroupTransferOwnerReqVO();
            reqVO.setId(10L);
            reqVO.setNewOwnerUserId(1L);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.transferGroupOwner(1L, reqVO));
            assertEquals(GROUP_TRANSFER_OWNER_TO_SELF.code(), exception.getCode());
            verify(groupMapper, never()).updateById(any(ImGroupDO.class));
        }
    }

    @Test
    public void testTransferGroupOwner_notOwner() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).ownerUserId(99L)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectByIdForUpdate(10L)).thenReturn(group);

            AppImGroupTransferOwnerReqVO reqVO = new AppImGroupTransferOwnerReqVO();
            reqVO.setId(10L);
            reqVO.setNewOwnerUserId(2L);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.transferGroupOwner(1L, reqVO));
            assertEquals(GROUP_NOT_OWNER.code(), exception.getCode());
        }
    }

    // ========== banGroup ==========

    @Test
    public void testBanGroup_dissolved() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).status(CommonStatusEnum.DISABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            ImGroupManagerBanReqVO reqVO = new ImGroupManagerBanReqVO();
            reqVO.setId(10L).setReason("违规");

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.banGroup(1L, reqVO));
            assertEquals(GROUP_DISSOLVED.code(), exception.getCode());
            verify(groupMapper, never()).updateById(any(ImGroupDO.class));
            verify(groupMessageService, never()).sendGroupMessage(anyLong(), any(ImGroupMessageSendDTO.class));
        }
    }

    @Test
    public void testBanGroup_alreadyBannedSkip() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).banned(true)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            ImGroupManagerBanReqVO reqVO = new ImGroupManagerBanReqVO();
            reqVO.setId(10L).setReason("违规");

            groupService.banGroup(1L, reqVO);

            verify(groupMapper, never()).updateById(any(ImGroupDO.class));
            verify(groupMessageService, never()).sendGroupMessage(anyLong(), any(ImGroupMessageSendDTO.class));
        }
    }

    // ========== muteMember ==========

    @Test
    public void testMuteMember_normalCannotMuteAdmin() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);
            when(groupMemberService.validateMemberInGroup(10L, 1L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(1L)
                            .role(ImGroupMemberRoleEnum.NORMAL.getRole()).build());
            when(groupMemberService.validateMemberInGroup(10L, 2L)).thenReturn(
                    ImGroupMemberDO.builder().groupId(10L).userId(2L)
                            .role(ImGroupMemberRoleEnum.ADMIN.getRole()).build());

            AppImGroupMuteMemberReqVO reqVO = new AppImGroupMuteMemberReqVO();
            reqVO.setId(10L).setUserId(2L).setMutedSeconds(60);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.muteMember(1L, reqVO));
            assertEquals(GROUP_NOT_OWNER_OR_ADMIN.code(), exception.getCode());
            verify(groupMemberService, never()).updateGroupMemberMuteEndTime(anyLong(), anyLong(), any());
            verify(groupMessageService, never()).sendGroupMessage(anyLong(), any(ImGroupMessageSendDTO.class));
        }
    }

    // ========== validateGroupExists ==========

    @Test
    public void testValidateGroupExists_notExists() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            when(groupMapper.selectById(10L)).thenReturn(null);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.validateGroupExists(10L));
            assertEquals(GROUP_NOT_EXISTS.code(), exception.getCode());
        }
    }

    @Test
    public void testValidateGroupExists_banned() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L).banned(true)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.validateGroupExists(10L));
            assertEquals(GROUP_BANNED.code(), exception.getCode());
        }
    }

    @Test
    public void testValidateGroupExists_dissolved() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(ImGroupServiceImpl.class)))
                    .thenReturn(groupService);

            ImGroupDO group = ImGroupDO.builder().id(10L)
                    .status(CommonStatusEnum.DISABLE.getStatus()).build();
            when(groupMapper.selectById(10L)).thenReturn(group);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> groupService.validateGroupExists(10L));
            assertEquals(GROUP_DISSOLVED.code(), exception.getCode());
        }
    }

    // ========== getMyGroupList ==========

    @Test
    public void testGetMyGroupList_noMembers() {
        when(groupMemberService.getActiveGroupMemberListByUserId(1L)).thenReturn(new ArrayList<>());

        List<ImGroupDO> result = groupService.getMyGroupList(1L, null);
        assertTrue(result.isEmpty());
        verify(groupMapper, never()).selectByIds(anyCollection());
    }

    @Test
    public void testGetMyGroupList_success() {
        // 活跃群成员
        when(groupMemberService.getActiveGroupMemberListByUserId(1L)).thenReturn(new ArrayList<>(List.of(
                ImGroupMemberDO.builder().groupId(10L).userId(1L)
                        .status(CommonStatusEnum.ENABLE.getStatus()).build(),
                ImGroupMemberDO.builder().groupId(20L).userId(1L)
                        .status(CommonStatusEnum.ENABLE.getStatus()).build()
        )));
        List<ImGroupDO> groups = List.of(
                ImGroupDO.builder().id(10L).status(CommonStatusEnum.ENABLE.getStatus()).build(),
                ImGroupDO.builder().id(20L).status(CommonStatusEnum.ENABLE.getStatus()).build(),
                ImGroupDO.builder().id(30L).status(CommonStatusEnum.ENABLE.getStatus()).build()
        );
        when(groupMapper.selectByIds(anyCollection())).thenReturn(groups);

        List<ImGroupDO> result = groupService.getMyGroupList(1L, null);
        assertEquals(3, result.size());
    }

}
