package com.diqin.cloud.module.im.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 加群申请 Response VO")
@Data
public class ImGroupRequestManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "群编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long groupId;

    @Schema(description = "群名称", example = "技术交流群")
    private String groupName;

    @Schema(description = "申请人 / 被邀请人用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "申请人昵称", example = "张三")
    private String userNickname;

    @Schema(description = "邀请人用户编号（NULL 表示用户主动申请）", example = "2048")
    private Long inviterUserId;

    @Schema(description = "邀请人昵称", example = "王五")
    private String inviterNickname;

    @Schema(description = "申请理由", example = "求拉群")
    private String applyContent;

    @Schema(description = "加入来源（枚举 ImGroupAddSourceEnum）", example = "1")
    private Integer addSource;

    @Schema(description = "处理结果（ImGroupRequestHandleResultEnum：0 未处理 / 1 同意 / 2 拒绝）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer handleResult;

    @Schema(description = "处理人用户编号", example = "3072")
    private Long handleUserId;

    @Schema(description = "处理人昵称", example = "群主")
    private String handleNickname;

    @Schema(description = "处理理由（拒绝时可选填）", example = "不符合条件")
    private String handleContent;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
