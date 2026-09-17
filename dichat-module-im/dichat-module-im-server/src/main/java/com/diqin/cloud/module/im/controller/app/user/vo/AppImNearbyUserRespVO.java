package com.diqin.cloud.module.im.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * App - 「附近的人」用户卡片 VO
 * <p>在 {@link AppImUserCardRespVO} 基础上附带与查询中心的距离（米）。
 *
 * @author hanson
 */
@Schema(description = "App - 附近的人 - 用户卡片 VO")
@Data
@Accessors(chain = true)
public class AppImNearbyUserRespVO implements Serializable {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "登录账号（微信号）", example = "zhangsan")
    private String username;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户头像", example = "https://www.diqin.com/1.png")
    private String avatar;

    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer sex;

    @Schema(description = "个性签名", example = "hello world")
    private String signature;

    @Schema(description = "与我的距离，单位：米", example = "320")
    private Long distance;
}
