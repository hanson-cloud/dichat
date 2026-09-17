package com.diqin.cloud.module.im.service.rtc;

import com.diqin.cloud.module.im.dal.dataobject.rtc.ImRtcCallDO;
import com.diqin.cloud.module.im.dal.mysql.rtc.ImRtcCallMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
public class ImRtcCallHistoryServiceImpl implements ImRtcCallHistoryService {

    @Resource
    private ImRtcCallMapper rtcCallMapper;

    @Override
    public List<ImRtcCallDO> getCallHistory(Long userId) {
        return rtcCallMapper.selectList(ImRtcCallDO::getInviterUserId, userId);
    }
}
