package com.diqin.cloud.module.im.api.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hanson
 */
@Data
@Schema(description = "用户 APP - 好友申请记录 Response VO")
public class ImMessageRespVO {

    @Schema(description = "来源ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11541")
    private Long fromUserId;

    @Schema(description = "来源昵称", example = "芋道")
    private String fromUserNickname;

    @Schema(description = "来源别名")
    private String alias;

    @Schema(description = "来源头像", example = "https://www.iocoder.cn/xxx.jpg")
    private String fromUserAvatar;

    @Schema(description = "来源性别", example = "0")
    private Integer sex;

    @Schema(description = "消息内容", example = "不香")
    private String content;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "发送时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime sendTime;

}

