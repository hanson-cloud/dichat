package com.diqin.cloud.module.im.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 用户自己修改资料 Request VO
 *
 * <p>仅允许修改昵称 / 头像 / 签名 / 性别，字段与 admin 包相同。
 *
 * @author hanson
 */
@Schema(description = "App - IM 用户 - 修改自己资料 Request VO")
@Data
@Accessors(chain = true)
public class AppImUserUpdateReqVO {

    @Schema(description = "昵称", example = "新昵称")
    private String nickname;

    @Schema(description = "头像", example = "https://www.diqin.com/1.png")
    private String avatar;

    @Schema(description = "头像缩略图", example = "https://www.diqin.com/1_thumb.png")
    private String avatarThumb;

    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer sex;

    @Schema(description = "个性签名", example = "hello world")
    private String signature;

    @Schema(description = "是否允许被「附近的人」看到；true:允许（默认） false:不允许", example = "true")
    private Boolean locationVisible;
}
