package com.diqin.cloud.module.im.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 群成员 Response VO")
@Data
public class ImGroupMemberManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "群编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long groupId;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String userNickname;

    @Schema(description = "群内显示名", example = "张三")
    private String displayUserName;

    @Schema(description = "群备注", example = "备注")
    private String groupRemark;

    @Schema(description = "是否免打扰", example = "false")
    private Boolean silent;

    @Schema(description = "成员状态（0=启用 1=停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "成员角色（1=群主 2=管理员 3=普通成员）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer role;

    @Schema(description = "加入来源", example = "2")
    private Integer addSource;

    @Schema(description = "入群时间")
    private LocalDateTime joinTime;

    @Schema(description = "退群时间")
    private LocalDateTime quitTime;

    @Schema(description = "禁言到期时间")
    private LocalDateTime muteEndTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
