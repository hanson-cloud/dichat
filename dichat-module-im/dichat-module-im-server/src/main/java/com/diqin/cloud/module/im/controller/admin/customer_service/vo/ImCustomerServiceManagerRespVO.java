package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 客服 Response VO")
@Data
public class ImCustomerServiceManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "客服工号/账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "cs_001")
    private String username;

    @Schema(description = "客服昵称", example = "小张")
    private String nickname;

    @Schema(description = "头像地址", example = "https://xxx.png")
    private String avatar;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "cs@example.com")
    private String email;

    @Schema(description = "关联管理后台账号编号（system_users.id，可空）", example = "1")
    private Long adminUserId;

    @Schema(description = "状态：0-离线 1-在线 2-忙碌", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "最大并发会话数", example = "5")
    private Integer maxConcurrent;

    @Schema(description = "当前活跃会话数（近窗口内与不同对端私聊数，用于负载展示）", example = "3")
    private Integer activeConversations;

    @Schema(description = "欢迎语", example = "您好，请问有什么可以帮您？")
    private String welcomeMsg;

    @Schema(description = "是否开启自动回复：0-关闭 1-开启", example = "0")
    private Boolean autoReplyEnabled;

    @Schema(description = "是否自动分配会话：0-关闭 1-开启", example = "1")
    private Boolean autoAssign;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
