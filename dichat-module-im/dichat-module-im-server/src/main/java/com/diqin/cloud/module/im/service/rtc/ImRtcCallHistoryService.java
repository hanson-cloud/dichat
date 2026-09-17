package com.diqin.cloud.module.im.service.rtc;

import com.diqin.cloud.module.im.dal.dataobject.rtc.ImRtcCallDO;

import java.util.List;

public interface ImRtcCallHistoryService {

    /**
     * 获取用户的通话记录列表
     *
     * @param userId 用户编号
     * @return 通话记录列表
     */
    List<ImRtcCallDO> getCallHistory(Long userId);
}
