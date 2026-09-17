package com.diqin.cloud.module.im.controller.admin.activity_slot.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 运营活动位分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImActivitySlotManagerPageReqVO extends PageParam {

    @Schema(description = "活动名称（模糊匹配）", example = "夏日大促")
    private String name;

    @Schema(description = "展示位：1-发现页Banner 2-聊天列表 3-朋友圈", example = "1")
    private Integer slotPosition;

    @Schema(description = "状态：0-停用 1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
