package com.diqin.cloud.module.im.controller.app.appversion;

import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.controller.app.appversion.vo.AppImAppVersionCheckRespVO;
import com.diqin.cloud.module.im.dal.dataobject.appversion.ImAppVersionDO;
import com.diqin.cloud.module.im.service.appversion.ImAppVersionManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 应用版本检测
 * <p>
 * 供 C 端 App 启动 / 设置页「检查更新」调用：传入当前版本号与平台，
 * 后端按 {@code im_app_version} 表中该平台「最新已发布」版本（状态为灰度中 / 全量发布中 version_code 最大者）比对，
 * 返回是否有更新、下载地址、是否强制更新等。版本数据由管理后台维护，不再依赖静态配置。
 * <p>
 * 路由说明：网关将 {@code /app-api/im/**} 转发到本服务（真实部署会重写为 {@code /im/**}），
 * 为兼容两种网关配置，本接口同时注册 {@code /im/app/version/check} 与 {@code /app-api/im/app/version/check} 两个路径。
 *
 * @author dichat
 */
@Tag(name = "用户 APP - 应用版本检测")
@RestController
@Validated
public class AppImAppVersionController {

    @Resource
    private ImAppVersionManagerService appVersionManagerService;

    @PermitAll
    @GetMapping({"/im/app/version/check"})
    @Operation(summary = "检查应用新版本", description = "传入当前版本号与平台，返回是否有新版本、下载地址、是否强制更新等")
    public CommonResult<AppImAppVersionCheckRespVO> checkVersion(
            @RequestParam(value = "current", required = false) String current,
            @RequestParam(value = "platform", required = false, defaultValue = "android") String platform) {
        Integer platformCode = toPlatformCode(platform);
        ImAppVersionDO latest = appVersionManagerService.getLatestByPlatform(platformCode);
        // 该平台暂无已发布版本 -> 无需更新
        if (latest == null) {
            return success(new AppImAppVersionCheckRespVO().setHasUpdate(false));
        }

        boolean hasUpdate;
        if (StrUtil.isNotBlank(current)) {
            // 当前版本低于最新版本 -> 有更新
            hasUpdate = compareVersion(current, latest.getVersionName()) < 0;
        } else {
            // 客户端未上报版本，保守提示有更新，引导其拉取最新版
            hasUpdate = true;
        }

        // 强制更新：该版本本身标记为强制更新，或当前版本低于最低可运行版本
        boolean force = latest.getUpdateType() != null && latest.getUpdateType() == 1;
        if (latest.getMinSupportCode() != null && StrUtil.isNotBlank(current)) {
            String threshold = appVersionManagerService.getVersionNameByCode(platformCode, latest.getMinSupportCode());
            if (threshold != null && compareVersion(current, threshold) < 0) {
                force = true;
            }
        }

        return success(new AppImAppVersionCheckRespVO()
                .setHasUpdate(hasUpdate)
                .setLatestVersion(latest.getVersionName())
                .setDownloadUrl(latest.getDownloadUrl())
                .setForce(force)
                .setDescription(latest.getChangelog())
                .setSize(formatSize(latest.getFileSize())));
    }

    /**
     * 平台字符串 -> 表 platform 整型：android=1 / ios=2 / harmony(os)=3。
     */
    private Integer toPlatformCode(String platform) {
        if ("ios".equalsIgnoreCase(platform)) {
            return 2;
        }
        if ("harmony".equalsIgnoreCase(platform) || "harmonyos".equalsIgnoreCase(platform)) {
            return 3;
        }
        return 1;
    }

    /**
     * 字节数 -> 人类可读大小（如 "28.6 MB"）。
     */
    private String formatSize(Long bytes) {
        if (bytes == null || bytes <= 0) {
            return "";
        }
        double mb = bytes / (1024.0 * 1024.0);
        return String.format(Locale.US, "%.1f MB", mb);
    }

    /**
     * 语义化版本号比较（仅比较数字段，忽略后缀如 -beta）。
     *
     * @return 负整数表示 a < b；0 表示相等；正整数表示 a > b
     */
    private int compareVersion(String a, String b) {
        String[] as = a.split("\\.");
        String[] bs = b.split("\\.");
        int len = Math.max(as.length, bs.length);
        for (int i = 0; i < len; i++) {
            int x = i < as.length ? parseIntSafe(as[i]) : 0;
            int y = i < bs.length ? parseIntSafe(bs[i]) : 0;
            if (x != y) {
                return Integer.compare(x, y);
            }
        }
        return 0;
    }

    private int parseIntSafe(String s) {
        try {
            // 只取前缀数字，忽略 -beta / +build 等非数字后缀
            return Integer.parseInt(s.replaceAll("[^0-9].*$", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
