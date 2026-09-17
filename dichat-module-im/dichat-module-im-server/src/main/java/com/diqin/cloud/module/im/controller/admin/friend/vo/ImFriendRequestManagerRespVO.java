package com.diqin.cloud.module.im.controller.admin.friend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 好友申请 Response VO")
@Data
public class ImFriendRequestManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "发起方用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long fromUserId;

    @Schema(description = "发起方昵称", example = "张三")
    private String fromNickname;

    @Schema(description = "接收方用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long toUserId;

    @Schema(description = "接收方昵称", example = "李四")
    private String toNickname;

    @Schema(description = "申请理由", example = "我是老王")
    private String applyContent;

    @Schema(description = "发起方对接收方的备注", example = "老同学")
    private String displayName;

    @Schema(description = "添加来源（枚举 ImFriendAddSourceEnum）", example = "1")
    private Integer addSource;

    @Schema(description = "处理结果（ImFriendRequestHandleResultEnum：0 未处理 / 1 同意 / 2 拒绝）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer handleResult;

    @Schema(description = "处理理由（拒绝时可选填）", example = "不认识")
    private String handleContent;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
