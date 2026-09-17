package com.diqin.cloud.module.im.service.appversion;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.appversion.ImAppVersionDO;

/**
 * IM 应用版本管理 Service 接口
 *
 * @author 速构构
 */
public interface ImAppVersionManagerService {

    /**
     * 获得应用版本分页
     */
    PageResult<ImAppVersionManagerRespVO> getAppVersionManagerPage(ImAppVersionManagerPageReqVO pageReqVO);

    /**
     * 获得应用版本详情
     */
    ImAppVersionManagerRespVO getAppVersion(Long id);

    /**
     * 创建应用版本
     */
    Long createAppVersion(ImAppVersionManagerSaveReqVO createReqVO);

    /**
     * 更新应用版本
     */
    void updateAppVersion(ImAppVersionManagerUpdateReqVO updateReqVO);

    /**
     * 删除应用版本
     */
    void deleteAppVersion(Long id);

    /**
     * 取指定平台「最新已发布」版本（供 C 端检查更新使用）。
     *
     * @param platform 目标平台: 1=Android 2=iOS 3=鸿蒙
     * @return 最新版本 DO；无已发布版本时返回 null
     */
    ImAppVersionDO getLatestByPlatform(Integer platform);

    /**
     * 取指定平台某版本号对应的版本名称（用于强制更新阈值换算）。
     */
    String getVersionNameByCode(Integer platform, Integer versionCode);

}
