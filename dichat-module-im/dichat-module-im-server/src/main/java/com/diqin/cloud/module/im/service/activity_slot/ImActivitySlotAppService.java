package com.diqin.cloud.module.im.service.activity_slot;

import com.diqin.cloud.module.im.controller.app.activity_slot.vo.AppImActivitySlotRespVO;

import java.util.List;

/**
 * IM 运营活动位 APP 端 Service 接口
 *
 * <p>用于客户端按展示位拉取当前生效的运营活动，仅读取、不暴露管理端写能力与状态字段。
 *
 * @author 速构构
 */
public interface ImActivitySlotAppService {

    /**
     * 获得指定展示位当前生效的运营活动位列表（status=1 且生效时间内），按 sort 升序
     *
     * @param slotPosition 展示位：1-发现页Banner 2-聊天列表 3-朋友圈
     */
    List<AppImActivitySlotRespVO> getActivitySlots(Integer slotPosition);

}
