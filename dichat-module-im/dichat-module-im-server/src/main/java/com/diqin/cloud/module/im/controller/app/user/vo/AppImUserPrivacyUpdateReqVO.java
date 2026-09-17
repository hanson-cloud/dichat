package com.diqin.cloud.module.im.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 用户 - 修改隐私设置 Request VO
 *
 * @author hanson
 */
@Schema(description = "App - IM 用户 - 修改隐私设置 Request VO")
@Data
@Accessors(chain = true)
public class AppImUserPrivacyUpdateReqVO {

    @Schema(description = "加我为好友是否需要验证", example = "true")
    private Boolean friendVerify;

    @Schema(description = "是否允许通过手机号找到我", example = "true")
    private Boolean allowFindByMobile;

    @Schema(description = "是否允许通过 DiChatID（账号）找到我", example = "true")
    private Boolean allowFindByUsername;

    @Schema(description = "向好友公开我的动态", example = "true")
    private Boolean publicMoments;

    @Schema(description = "是否允许被「附近的人」看到", example = "true")
    private Boolean locationVisible;

}
