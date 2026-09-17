package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 客服工作台会话消息列表 Request VO
 *
 * @author 速构构
 */
@Schema(description = "管理后台 - 客服工作台会话消息列表 Request VO")
@Data
public class ImCsMessageListReqVO {

    @Schema(description = "对端用户编号（im_users.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "225")
    @NotNull(message = "对端用户编号不能为空")
    private Long peerId;

    @Schema(description = "游标：起始消息 id（不含），为空则从最新开始", example = "100")
    private Long maxId;

    @Schema(description = "拉取数量（建议 20~50）", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "拉取数量不能为空")
    private Integer limit;

}
