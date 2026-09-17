package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 客服新增 Request VO")
@Data
public class ImCustomerServiceManagerSaveReqVO {

    @Schema(description = "客服工号/账号（全局唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "cs_001")
    @NotBlank(message = "客服工号不能为空")
    private String username;

    @Schema(description = "客服昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小张")
    @NotBlank(message = "客服昵称不能为空")
    private String nickname;

    @Schema(description = "头像地址", example = "https://xxx.png")
    private String avatar;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "cs@example.com")
    private String email;

    @Schema(description = "关联管理后台账号编号（system_users.id，用于客服工作台识别坐席身份，可空）", example = "1")
    private Long adminUserId;

    @Schema(description = "状态：0-离线 1-在线 2-忙碌", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "最大并发会话数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "最大并发会话数不能为空")
    private Integer maxConcurrent;

    @Schema(description = "欢迎语", example = "您好，请问有什么可以帮您？")
    private String welcomeMsg;

    @Schema(description = "是否开启自动回复：0-关闭 1-开启", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "自动回复开关不能为空")
    private Boolean autoReplyEnabled;

    @Schema(description = "是否自动分配会话：0-关闭 1-开启", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "自动分配开关不能为空")
    private Boolean autoAssign;

    @Schema(description = "排序", example = "0")
    private Integer sort;

}
