package com.diqin.cloud.module.im.controller.app.customer_service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 当前可接入客服 Response VO
 * <p>
 * 仅暴露移动端「联系客服」打开 IM 私聊所需的最小字段。
 * 方案 C：客服是独立参与方，私聊以 {@code (participantType=3, csId)} 作为唯一身份定位，
 * 不再依赖 im_users.id。客户端据此以 csId + participantType 打开 / 发送「客服会话」。
 *
 * @author 速构构
 */
@Schema(description = "用户 APP - 当前可接入客服 Response VO")
@Data
public class AppImCustomerServiceCurrentRespVO {

    @Schema(description = "客服身份编号（im_customer_service.id，方案 C 下客服作为独立参与方的身份定位）", example = "12")
    private Long csId;

    @Schema(description = "参与方类型：1-用户 2-机器人 3-人工客服（固定为 3，见 ImMessageParticipantTypeEnum）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer participantType;

    @Schema(description = "客服昵称", example = "小张")
    private String nickname;

    @Schema(description = "头像地址", example = "https://xxx.png")
    private String avatar;

    @Schema(description = "状态：0-离线 1-在线 2-忙碌", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "欢迎语", example = "您好，请问有什么可以帮您？")
    private String welcomeMsg;

}
