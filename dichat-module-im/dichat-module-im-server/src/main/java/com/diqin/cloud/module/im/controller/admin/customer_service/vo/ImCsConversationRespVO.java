package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 客服工作台会话 Response VO
 *
 * @author 速构构
 */
@Schema(description = "管理后台 - 客服工作台会话 Response VO")
@Data
public class ImCsConversationRespVO {

    @Schema(description = "对端用户编号（im_users.id）", example = "225")
    private Long peerId;

    @Schema(description = "对端用户昵称", example = "张三")
    private String peerNickname;

    @Schema(description = "对端用户头像地址", example = "https://xxx.png")
    private String peerAvatar;

    @Schema(description = "最近一条消息内容摘要", example = "在的吗？")
    private String lastMessageContent;

    @Schema(description = "最近一条消息类型", example = "101")
    private Integer lastMessageType;

    @Schema(description = "最近一条消息发送时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "客服侧是否置顶", example = "false")
    private Boolean pinned;

    @Schema(description = "未读数：用户发来、客服尚未阅读的消息条数", example = "2")
    private Integer unreadCount;

}
