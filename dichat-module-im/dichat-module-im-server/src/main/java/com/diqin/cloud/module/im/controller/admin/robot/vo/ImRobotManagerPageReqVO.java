package com.diqin.cloud.module.im.controller.admin.robot.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 机器人分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImRobotManagerPageReqVO extends PageParam {

    @Schema(description = "机器人登录账号（模糊匹配）", example = "robot_001")
    private String username;

    @Schema(description = "昵称（模糊匹配）", example = "小助手")
    private String nickname;

    @Schema(description = "状态：0-停用 1-启用", example = "1")
    private Integer status;

    @Schema(description = "是否开启自动回复：0-关闭 1-开启", example = "1")
    private Boolean autoReplyEnabled;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
