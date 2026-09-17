package com.diqin.cloud.module.im.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 用户卡片 VO（搜索 / 好友列表 / 群成员列表 使用）
 *
 * <p>精简版，不暴露手机号、邮箱等敏感字段，对应微信的「用户卡片」。
 *
 * @author hanson
 */
@Schema(description = "App - IM 用户卡片 VO（搜索结果）")
@Data
@Accessors(chain = true)
public class AppImUserCardRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "登录账号（微信号），搜索精确匹配时返回", example = "zhangsan")
    private String username;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户头像", example = "https://www.diqin.com/1.png")
    private String avatar;

    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer sex;

    @Schema(description = "个性签名", example = "hello world")
    private String signature;

}
