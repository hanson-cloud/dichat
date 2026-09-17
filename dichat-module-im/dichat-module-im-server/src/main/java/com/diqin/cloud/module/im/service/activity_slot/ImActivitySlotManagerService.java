package com.diqin.cloud.module.im.service.activity_slot;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerUpdateReqVO;

/**
 * IM 运营活动位管理 Service 接口
 *
 * @author 速构构
 */
public interface ImActivitySlotManagerService {

    PageResult<ImActivitySlotManagerRespVO> getActivitySlotPage(ImActivitySlotManagerPageReqVO pageReqVO);

    ImActivitySlotManagerRespVO getActivitySlot(Long id);

    Long createActivitySlot(ImActivitySlotManagerSaveReqVO createReqVO);

    void updateActivitySlot(ImActivitySlotManagerUpdateReqVO updateReqVO);

    void deleteActivitySlot(Long id);

}
