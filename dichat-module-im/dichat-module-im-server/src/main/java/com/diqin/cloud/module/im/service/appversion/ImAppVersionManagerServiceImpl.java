package com.diqin.cloud.module.im.service.appversion;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.appversion.ImAppVersionDO;
import com.diqin.cloud.module.im.dal.mysql.appversion.ImAppVersionMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.APP_VERSION_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.APP_VERSION_PLATFORM_VERSION_DUPLICATE;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 应用版本管理 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImAppVersionManagerServiceImpl implements ImAppVersionManagerService {

    @Resource
    private ImAppVersionMapper appVersionMapper;

    @Override
    public PageResult<ImAppVersionManagerRespVO> getAppVersionManagerPage(ImAppVersionManagerPageReqVO pageReqVO) {
        PageResult<ImAppVersionDO> pageResult = appVersionMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, ImAppVersionManagerRespVO.class);
    }

    @Override
    public ImAppVersionManagerRespVO getAppVersion(Long id) {
        ImAppVersionDO entity = appVersionMapper.selectById(id);
        return BeanUtils.toBean(entity, ImAppVersionManagerRespVO.class);
    }

    @Override
    @LogRecord(type = IM_APP_VERSION_TYPE, subType = IM_APP_VERSION_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_APP_VERSION_CREATE_SUCCESS)
    public Long createAppVersion(ImAppVersionManagerSaveReqVO createReqVO) {
        // 校验「平台 + 版本号」唯一（对应表唯一索引 uk_platform_version）
        validatePlatformVersionUnique(null, createReqVO.getPlatform(), createReqVO.getVersionCode());
        ImAppVersionDO entity = BeanUtils.toBean(createReqVO, ImAppVersionDO.class);
        appVersionMapper.insert(entity);
        LogRecordContext.putVariable("appVersion", entity);
        return entity.getId();
    }

    @Override
    @LogRecord(type = IM_APP_VERSION_TYPE, subType = IM_APP_VERSION_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = IM_APP_VERSION_UPDATE_SUCCESS)
    public void updateAppVersion(ImAppVersionManagerUpdateReqVO updateReqVO) {
        // 校验存在
        if (appVersionMapper.selectById(updateReqVO.getId()) == null) {
            throw ServiceExceptionUtil.exception(APP_VERSION_NOT_EXISTS);
        }
        // 校验「平台 + 版本号」唯一（排除自身）
        validatePlatformVersionUnique(updateReqVO.getId(), updateReqVO.getPlatform(), updateReqVO.getVersionCode());
        ImAppVersionDO update = BeanUtils.toBean(updateReqVO, ImAppVersionDO.class);
        LogRecordContext.putVariable("updateReqVO", updateReqVO);
        appVersionMapper.updateById(update);
    }

    @Override
    @LogRecord(type = IM_APP_VERSION_TYPE, subType = IM_APP_VERSION_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = IM_APP_VERSION_DELETE_SUCCESS)
    public void deleteAppVersion(Long id) {
        // 校验存在
        if (appVersionMapper.selectById(id) == null) {
            throw ServiceExceptionUtil.exception(APP_VERSION_NOT_EXISTS);
        }
        appVersionMapper.deleteById(id);
    }

    @Override
    public ImAppVersionDO getLatestByPlatform(Integer platform) {
        return appVersionMapper.selectLatestPublished(platform);
    }

    @Override
    public String getVersionNameByCode(Integer platform, Integer versionCode) {
        ImAppVersionDO entity = appVersionMapper.selectByPlatformAndVersionCode(platform, versionCode);
        return entity == null ? null : entity.getVersionName();
    }

    private void validatePlatformVersionUnique(Long id, Integer platform, Integer versionCode) {
        ImAppVersionDO exist = appVersionMapper.selectByPlatformVersionCode(id, platform, versionCode);
        if (exist != null) {
            throw ServiceExceptionUtil.exception(APP_VERSION_PLATFORM_VERSION_DUPLICATE, platform + " v" + versionCode);
        }
    }

}
