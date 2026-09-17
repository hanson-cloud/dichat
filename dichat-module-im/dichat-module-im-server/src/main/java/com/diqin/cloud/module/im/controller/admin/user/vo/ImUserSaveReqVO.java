package com.diqin.cloud.module.im.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 用户创建 / 更新 Request VO
 * <p>
 * 用于管理后台；创建时 password 必填，id 必为空；更新时 id 必填，password 可选。
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 用户 Save Request VO")
@Data
@Accessors(chain = true)
public class ImUserSaveReqVO {

    @Schema(description = "用户编号（仅更新时必填）", example = "1024")
    private Long id;

    @Schema(description = "登录账号（仅创建时必填）", example = "zhangsan")
    @NotBlank(message = "登录账号不能为空")
    private String username;

    @Schema(description = "明文密码（仅创建时必填；更新时不传表示不修改）", example = "123456")
    private String password;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像", example = "https://www.diqin.com/1.png")
    private String avatar;

    @Schema(description = "头像缩略图", example = "https://www.diqin.com/1_thumb.png")
    private String avatarThumb;

    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer sex;

    @Schema(description = "个性签名", example = "hello")
    private String signature;

    @Schema(description = "手机号", example = "15601691300")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "账号状态 0:正常 1:停用", example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "是否封禁", example = "false")
    private Boolean banned;

    @Schema(description = "封禁原因")
    private String banReason;
}
