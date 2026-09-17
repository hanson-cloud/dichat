package com.diqin.cloud.module.im.dal.dataobject.appversion;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM App 应用版本 DO
 * <p>
 * 对应表 {@code im_app_version}，记录各平台（Android / iOS / 鸿蒙）的版本发布与强制更新策略。
 * 管理后台维护，C 端「检查更新」接口 {@code AppImAppVersionController#checkVersion} 按平台读取最新已发布版本。
 *
 * @author 速构构
 */
@TableName(value = "im_app_version", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImAppVersionDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 目标平台: 1=Android 2=iOS 3=鸿蒙(HarmonyOS)
     */
    private Integer platform;

    /**
     * 版本号（整数，用于版本比较）
     */
    private Integer versionCode;

    /**
     * 版本名称（如 "1.2.3", "2.0.0-beta1"）
     */
    private String versionName;

    /**
     * 安装包下载地址（APK/IPA/APP）
     */
    private String downloadUrl;

    /**
     * 安装包大小（字节）
     */
    private Long fileSize;

    /**
     * 更新方式: 0=普通更新 1=强制更新 2=静默更新（仅限小补丁）
     */
    private Integer updateType;

    /**
     * 版本更新日志
     */
    private String changelog;

    /**
     * 最低兼容版本code（低于此版本无法使用）
     */
    private Integer minSupportCode;

    /**
     * 安装包 MD5 校验值
     */
    private String md5;

    /**
     * 状态: 0=下线 1=灰度中 2=全量发布 3=已归档
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 下线时间
     */
    private LocalDateTime offlineTime;

}
