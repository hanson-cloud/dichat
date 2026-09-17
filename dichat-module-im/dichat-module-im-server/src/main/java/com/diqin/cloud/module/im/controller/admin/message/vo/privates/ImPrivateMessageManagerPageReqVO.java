package com.diqin.cloud.module.im.controller.admin.message.vo.privates;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.validation.InEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 私聊消息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImPrivateMessageManagerPageReqVO extends PageParam {

    @Schema(description = "发送人编号", example = "1024")
    private Long senderId;

    @Schema(description = "接收人编号", example = "2048")
    private Long receiverId;

    @Schema(description = "发送方类型：1-用户 2-机器人 3-人工客服", example = "1")
    @InEnum(value = ImMessageParticipantTypeEnum.class, message = "发送方类型必须是 {value}")
    private Integer senderType; // 参见 ImMessageParticipantTypeEnum 枚举类

    @Schema(description = "接收方类型：1-用户 2-机器人 3-人工客服", example = "1")
    @InEnum(value = ImMessageParticipantTypeEnum.class, message = "接收方类型必须是 {value}")
    private Integer receiverType; // 参见 ImMessageParticipantTypeEnum 枚举类

    @Schema(description = "消息类型", example = "1")
    private Integer type; // 参见 ImMessageTypeEnum 枚举类

    @Schema(description = "消息内容", example = "你好")
    private String content;

    @Schema(description = "消息状态", example = "0")
    private Integer status; // 参见 ImMessageStatusEnum 枚举类

    @Schema(description = "发送时间", example = "[2026-04-01 00:00:00, 2026-04-30 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] sendTime;

}
