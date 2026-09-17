package com.diqin.cloud.module.im.controller.admin.group.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - IM 群成员分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImGroupMemberManagerPageReqVO extends PageParam {

    @Schema(description = "群编号", example = "1024")
    private Long groupId;

    @Schema(description = "用户编号", example = "2048")
    private Long userId;

    @Schema(description = "成员角色（1=群主 2=管理员 3=普通成员）", example = "3")
    private Integer role;

    @Schema(description = "成员状态（0=启用 1=停用）", example = "0")
    private Integer status;

    @Schema(description = "群内显示名，模糊匹配", example = "张三")
    private String displayUserName;

}
