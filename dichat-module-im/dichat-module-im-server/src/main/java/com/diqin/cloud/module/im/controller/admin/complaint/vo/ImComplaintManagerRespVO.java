package com.diqin.cloud.module.im.controller.admin.complaint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 用户投诉（举报） Response VO")
@Data
public class ImComplaintManagerRespVO {

    @Schema(description = "投诉编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "投诉人用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long complainantId;

    @Schema(description = "投诉人昵称", example = "张三")
    private String complainantNickname;

    @Schema(description = "被投诉人用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long respondentId;

    @Schema(description = "被投诉人昵称", example = "李四")
    private String respondentNickname;

    @Schema(description = "投诉类别（1=骚扰 2=诈骗 3=色情 4=虚假信息 5=其他违规）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer category;

    @Schema(description = "投诉详细描述")
    private String content;

    @Schema(description = "证据截图 / 文件 URL（逗号分隔）")
    private String evidenceUrls;

    @Schema(description = "关联的消息编号", example = "4096")
    private Long sourceMsgId;

    @Schema(description = "关联的会话编号", example = "5120")
    private Long sourceChatId;

    @Schema(description = "处理状态（0=待处理 1=处理中 2=已处理 3=已驳回）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "处理结果描述")
    private String handleResult;

    @Schema(description = "处罚措施（1=警告 2=禁言 3=封号 4=无处罚）", example = "3")
    private Integer punishment;

    @Schema(description = "处理人管理员编号", example = "1")
    private Long handleUserId;

    @Schema(description = "处理完成时间")
    private LocalDateTime handleTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
