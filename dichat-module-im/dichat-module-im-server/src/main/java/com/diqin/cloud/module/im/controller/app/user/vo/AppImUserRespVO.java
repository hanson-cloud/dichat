package com.diqin.cloud.module.im.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 用户 Response VO（完整版）
 *
 * <p>用于「我的资料」等需要完整信息的场景。
 * <p>搜索结果请使用 {@link AppImUserCardRespVO}，避免泄露手机号 / 邮箱。
 *
 * @author hanson
 */
@Schema(description = "App - IM 用户 Response VO（完整版）")
@Data
@Accessors(chain = true)
public class AppImUserRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "登录账号", example = "zhangsan")
    private String username;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户头像", example = "https://www.diqin.com/1.png")
    private String avatar;

    @Schema(description = "头像缩略图", example = "https://www.diqin.com/1_thumb.png")
    private String avatarThumb;

    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer sex;

    @Schema(description = "个性签名", example = "hello world")
    private String signature;

    @Schema(description = "手机号", example = "15601691300")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "注册时间")
    private String createTime;

    @Schema(description = "是否允许通过手机号找到我", example = "true")
    private Boolean allowFindByMobile;

    @Schema(description = "是否允许通过 DiChatID（账号）找到我", example = "true")
    private Boolean allowFindByUsername;

    @Schema(description = "加我为好友是否需要验证", example = "true")
    private Boolean friendVerify;

    @Schema(description = "向好友公开我的动态", example = "true")
    private Boolean publicMoments;

    @Schema(description = "是否允许被附近的人看到", example = "true")
    private Boolean locationVisible;
}
