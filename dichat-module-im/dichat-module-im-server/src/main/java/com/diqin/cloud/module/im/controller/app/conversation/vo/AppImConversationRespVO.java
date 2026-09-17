package com.diqin.cloud.module.im.controller.app.conversation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * APP 端 - 私聊会话列表 Response VO
 * <p>
 * 会话聚合表 {@code im_conversation} 已按参与方配对维护末条消息 / 未读 / 置顶；
 * 本 VO 直接回填对端（用户 / 机器人 / 人工客服）昵称与头像，供移动端会话列表渲染。
 * {@code peerId} 返回规范 id（机器人=im_robot.id / 客服=im_customer_service.id / 用户=im_users.id），
 * 前端可原样作为私聊的 receiverId 使用（发送 / 已读 / 历史接口均兼容规范 id 与绑定 userId 两种形态）。
 *
 * @author 速构构
 */
@Schema(description = "APP 端 - 私聊会话列表 Response VO")
@Data
public class AppImConversationRespVO {

    @Schema(description = "会话编号（im_conversation.id）", example = "1001")
    private Long conversationId;

    @Schema(description = "对端参与方类型：1-用户 2-机器人 3-人工客服", example = "3")
    private Integer peerType;

    @Schema(description = "对端参与方编号（规范 id）", example = "225")
    private Long peerId;

    @Schema(description = "对端昵称（机器人/客服取各自昵称，用户取 im_users.nickname）", example = "在线客服小美")
    private String peerNickname;

    @Schema(description = "对端头像地址", example = "https://xxx.png")
    private String peerAvatar;

    @Schema(description = "最近一条消息内容摘要", example = "您好，有什么可以帮您？")
    private String lastMessageContent;

    @Schema(description = "最近一条消息类型", example = "101")
    private Integer lastMessageType;

    @Schema(description = "最近一条消息发送时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "当前用户视角未读数（对端发来且未读）", example = "2")
    private Integer unreadCount;

    @Schema(description = "当前用户是否置顶该会话", example = "false")
    private Boolean pinned;

}
