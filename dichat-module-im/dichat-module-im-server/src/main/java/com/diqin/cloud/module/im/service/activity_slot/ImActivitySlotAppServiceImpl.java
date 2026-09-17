package com.diqin.cloud.module.im.service.activity_slot;

import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.activity_slot.vo.AppImActivitySlotRespVO;
import com.diqin.cloud.module.im.dal.dataobject.activity_slot.ImActivitySlotDO;
import com.diqin.cloud.module.im.dal.mysql.activity_slot.ImActivitySlotMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IM 运营活动位 APP 端 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImActivitySlotAppServiceImpl implements ImActivitySlotAppService {

    @Resource
    private ImActivitySlotMapper activitySlotMapper;

    @Override
    public List<AppImActivitySlotRespVO> getActivitySlots(Integer slotPosition) {
        List<ImActivitySlotDO> list = activitySlotMapper.selectActiveList(slotPosition, LocalDateTime.now());
        return BeanUtils.toBean(list, AppImActivitySlotRespVO.class);
    }

}
