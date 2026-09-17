package com.diqin.cloud.module.im.dal.mysql.appversion;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.appversion.ImAppVersionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM App 应用版本 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImAppVersionMapper extends BaseMapperX<ImAppVersionDO> {

    default PageResult<ImAppVersionDO> selectPage(ImAppVersionManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImAppVersionDO>()
                .likeIfPresent(ImAppVersionDO::getVersionName, reqVO.getVersionName())
                .eqIfPresent(ImAppVersionDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(ImAppVersionDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ImAppVersionDO::getUpdateType, reqVO.getUpdateType())
                .betweenIfPresent(ImAppVersionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImAppVersionDO::getId));
    }

    /**
     * 取指定平台「最新已发布」版本：状态为灰度中(1) / 全量发布(2) 中 version_code 最大者。
     *
     * @param platform 目标平台: 1=Android 2=iOS 3=鸿蒙
     * @return 最新版本 DO；该平台无已发布版本时返回 null
     */
    default ImAppVersionDO selectLatestPublished(Integer platform) {
        List<ImAppVersionDO> list = selectList(new LambdaQueryWrapperX<ImAppVersionDO>()
                .eq(ImAppVersionDO::getPlatform, platform)
                .in(ImAppVersionDO::getStatus, 1, 2)
                .orderByDesc(ImAppVersionDO::getVersionCode)
                .last("LIMIT 1"));
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 按平台 + 版本号精确查询（用于强制更新阈值换算）。
     */
    default ImAppVersionDO selectByPlatformAndVersionCode(Integer platform, Integer versionCode) {
        return selectOne(new LambdaQueryWrapperX<ImAppVersionDO>()
                .eq(ImAppVersionDO::getPlatform, platform)
                .eq(ImAppVersionDO::getVersionCode, versionCode));
    }

    /**
     * 校验「平台 + 版本号」唯一（排除自身）。对应表唯一索引 uk_platform_version。
     */
    default ImAppVersionDO selectByPlatformVersionCode(Long id, Integer platform, Integer versionCode) {
        return selectOne(new LambdaQueryWrapperX<ImAppVersionDO>()
                .eq(ImAppVersionDO::getPlatform, platform)
                .eq(ImAppVersionDO::getVersionCode, versionCode)
                .ne(id != null, ImAppVersionDO::getId, id));
    }

}
