package com.diqin.cloud.module.im.service.activity_slot;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.activity_slot.ImActivitySlotDO;
import com.diqin.cloud.module.im.dal.mysql.activity_slot.ImActivitySlotMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.ACTIVITY_SLOT_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 运营活动位管理 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImActivitySlotManagerServiceImpl implements ImActivitySlotManagerService {

    @Resource
    private ImActivitySlotMapper activitySlotMapper;

    @Override
    public PageResult<ImActivitySlotManagerRespVO> getActivitySlotPage(ImActivitySlotManagerPageReqVO pageReqVO) {
        PageResult<ImActivitySlotDO> pageResult = activitySlotMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, ImActivitySlotManagerRespVO.class);
    }

    @Override
    public ImActivitySlotManagerRespVO getActivitySlot(Long id) {
        ImActivitySlotDO slot = activitySlotMapper.selectById(id);
        return BeanUtils.toBean(slot, ImActivitySlotManagerRespVO.class);
    }

    @Override
    @LogRecord(type = IM_ACTIVITY_SLOT_TYPE, subType = IM_ACTIVITY_SLOT_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_ACTIVITY_SLOT_CREATE_SUCCESS)
    public Long createActivitySlot(ImActivitySlotManagerSaveReqVO createReqVO) {
        ImActivitySlotDO slot = BeanUtils.toBean(createReqVO, ImActivitySlotDO.class);
        activitySlotMapper.insert(slot);
        LogRecordContext.putVariable("slot", slot);
        return slot.getId();
    }

    @Override
    @LogRecord(type = IM_ACTIVITY_SLOT_TYPE, subType = IM_ACTIVITY_SLOT_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = IM_ACTIVITY_SLOT_UPDATE_SUCCESS)
    public void updateActivitySlot(ImActivitySlotManagerUpdateReqVO updateReqVO) {
        ImActivitySlotDO old = activitySlotMapper.selectById(updateReqVO.getId());
        if (old == null) {
            throw ServiceExceptionUtil.exception(ACTIVITY_SLOT_NOT_EXISTS);
        }
        ImActivitySlotDO update = BeanUtils.toBean(updateReqVO, ImActivitySlotDO.class);
        LogRecordContext.putVariable("updateReqVO", updateReqVO);
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, old);
        activitySlotMapper.updateById(update);
    }

    @Override
    public void deleteActivitySlot(Long id) {
        if (activitySlotMapper.selectById(id) == null) {
            throw ServiceExceptionUtil.exception(ACTIVITY_SLOT_NOT_EXISTS);
        }
        activitySlotMapper.deleteById(id);
    }

}
