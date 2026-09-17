package com.diqin.cloud.module.im.controller.admin.appversion.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 应用版本分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImAppVersionManagerPageReqVO extends PageParam {

    @Schema(description = "版本名称（模糊匹配）", example = "1.2.3")
    private String versionName;

    @Schema(description = "目标平台: 1=Android 2=iOS 3=鸿蒙(HarmonyOS)", example = "1")
    private Integer platform;

    @Schema(description = "状态: 0=下线 1=灰度中 2=全量发布 3=已归档", example = "2")
    private Integer status;

    @Schema(description = "更新方式: 0=普通更新 1=强制更新 2=静默更新", example = "0")
    private Integer updateType;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
