package com.diqin.cloud.module.im.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 用户基础信息 VO（管理后台 / 通用）
 *
 * <p>与 {@code ImUserRespDTO}（RPC DTO）字段对齐，前端接口使用本 VO。
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 用户 Response VO")
@Data
@Accessors(chain = true)
public class ImUserRespVO {

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

    @Schema(description = "账号状态 0:正常 1:停用", example = "0")
    private Integer status;

    @Schema(description = "是否被封禁", example = "false")
    private Boolean banned;

    @Schema(description = "封禁原因", example = "违反社区规范")
    private String banReason;

    @Schema(description = "最后登录 IP", example = "192.168.1.1")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private String loginDate;

    @Schema(description = "注册时间")
    private String createTime;

}
