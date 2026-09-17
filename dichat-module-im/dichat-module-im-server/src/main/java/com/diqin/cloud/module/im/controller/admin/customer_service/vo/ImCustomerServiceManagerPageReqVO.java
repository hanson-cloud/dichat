package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 客服分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImCustomerServiceManagerPageReqVO extends PageParam {

    @Schema(description = "客服工号/账号（模糊匹配）", example = "cs_001")
    private String username;

    @Schema(description = "客服昵称（模糊匹配）", example = "小张")
    private String nickname;

    @Schema(description = "状态：0-离线 1-在线 2-忙碌", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
