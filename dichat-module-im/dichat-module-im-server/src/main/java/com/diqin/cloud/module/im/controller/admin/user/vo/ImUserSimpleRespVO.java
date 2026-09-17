package com.diqin.cloud.module.im.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 用户精简信息 VO（供表单下拉选择，如机器人关联用户、客服绑定 IM 身份）
 *
 * <p>仅暴露安全字段：编号 / 账号 / 昵称 / 头像，不含手机号、密码、签名等敏感信息。
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 用户精简信息 Response VO（表单下拉选择）")
@Data
@Accessors(chain = true)
public class ImUserSimpleRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "登录账号", example = "zhangsan")
    private String username;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户头像", example = "https://www.diqin.com/1.png")
    private String avatar;

}
