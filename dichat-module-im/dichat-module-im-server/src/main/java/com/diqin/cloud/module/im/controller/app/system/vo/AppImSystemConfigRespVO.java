package com.diqin.cloud.module.im.controller.app.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "用户 APP - IM 系统配置 Response VO")
@Data
@Accessors(chain = true)
public class AppImSystemConfigRespVO {

    // ==================== 消息模块开关 ====================

    @Schema(description = "是否启用私聊已读功能（关闭后：客户端隐藏已读 / 未读标签）", example = "true")
    private Boolean privateReadEnabled;

    @Schema(description = "是否启用群聊已读功能（含群消息回执）", example = "true")
    private Boolean groupReadEnabled;

    @Schema(description = "消息撤回时间限制（分钟），超过此时间不允许撤回", example = "5")
    private Integer recallTimeoutMinutes;

    @Schema(description = "消息拉取分页大小上限（服务端硬性限制）", example = "1000")
    private Integer maxPullSize;

    @Schema(description = "私聊离线消息最大拉取天数", example = "30")
    private Integer privatePullMaxDays;

    @Schema(description = "群聊离线消息最大拉取天数（退群群也按此窗口）", example = "30")
    private Integer groupPullMaxDays;

    // ==================== 群模块限制 ====================

    @Schema(description = "群最大成员人数", example = "500")
    private Integer groupMaxMember;

    @Schema(description = "单群管理员人数上限", example = "3")
    private Integer groupAdminMaxCount;

    @Schema(description = "单群置顶消息条数上限", example = "5")
    private Integer groupPinMaxCount;

    // ==================== 表情模块限制 ====================

    @Schema(description = "个人表情包最大收藏数量", example = "200")
    private Integer faceUserItemMaxCount;

    // ==================== 实时通话模块 ====================

    @Schema(description = "WebRTC 子配置；前端根据 enabled 决定是否渲染通话按钮")
    private WebrtcConfig webrtc;

    @Data
    @Accessors(chain = true)
    public static class WebrtcConfig {

        @Schema(description = "是否启用实时通话功能；关闭后前端隐藏通话按钮", example = "true")
        private Boolean enabled;

        @Schema(description = "LiveKit Server WebSocket 地址；客户端 connect 时使用",
                example = "ws://127.0.0.1:7880")
        private String livekitUrl;

        @Schema(description = "LiveKit API Key（公开给前端，用于客户端 SDK 初始化）", example = "dichat-im")
        private String apiKey;

        @Schema(description = "群通话最大同时在房成员数；超过 invite 直接拒绝", example = "16")
        private Integer groupMaxParticipants;

    }
}
