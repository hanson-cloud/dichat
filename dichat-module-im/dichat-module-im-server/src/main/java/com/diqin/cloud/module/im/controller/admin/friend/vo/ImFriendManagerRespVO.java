package com.diqin.cloud.module.im.controller.admin.friend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 好友关系 Response VO")
@Data
public class ImFriendManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String userNickname;

    @Schema(description = "好友用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long friendUserId;

    @Schema(description = "好友昵称", example = "李四")
    private String friendNickname;

    @Schema(description = "好友展示备注", example = "老同学")
    private String displayName;

    @Schema(description = "添加来源（枚举 ImFriendAddSourceEnum）", example = "1")
    private Integer addSource;

    @Schema(description = "是否免打扰", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean silent;

    @Schema(description = "是否置顶联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean pinned;

    @Schema(description = "是否拉黑", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean blocked;

    @Schema(description = "好友状态（CommonStatusEnum：0 正常 / 1 已删除）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "添加好友时间")
    private LocalDateTime addTime;

    @Schema(description = "删除好友时间")
    private LocalDateTime deleteTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
