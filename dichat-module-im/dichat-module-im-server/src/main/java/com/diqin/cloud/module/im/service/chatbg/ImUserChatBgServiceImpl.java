package com.diqin.cloud.module.im.service.chatbg;

import com.diqin.cloud.framework.common.exception.ServiceException;
import com.diqin.cloud.module.im.dal.dataobject.chatbg.ImUserChatBgDO;
import com.diqin.cloud.module.im.dal.mysql.chatbg.ImUserChatBgMapper;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class ImUserChatBgServiceImpl implements ImUserChatBgService {

    /** 全局默认背景的会话key 哨兵值（配合唯一索引 uk_user_conv） */
    private static final String GLOBAL_CONV_KEY = "";

    /** 背景类型：图片(url) */
    private static final int BG_TYPE_IMAGE = 1;
    /** 背景类型：颜色(hex) */
    private static final int BG_TYPE_COLOR = 2;

    @Resource
    private ImUserChatBgMapper chatBgMapper;

    @Override
    public void syncBg(Long userId, String bgValue, Integer bgType, String convKey) {
        if (!StringUtils.hasText(bgValue)) {
            throw new ServiceException(ErrorCodeConstants.CHAT_BG_VALUE_EMPTY);
        }
        String resolvedConvKey = StringUtils.hasText(convKey) ? convKey : GLOBAL_CONV_KEY;
        int resolvedBgType = (bgType != null) ? bgType
                : (bgValue.trim().startsWith("#") ? BG_TYPE_COLOR : BG_TYPE_IMAGE);

        ImUserChatBgDO exist = chatBgMapper.selectByUserIdAndConvKey(userId, resolvedConvKey);
        if (exist == null) {
            ImUserChatBgDO entity = ImUserChatBgDO.builder()
                    .userId(userId)
                    .convKey(resolvedConvKey)
                    .bgType(resolvedBgType)
                    .bgValue(bgValue)
                    .build();
            chatBgMapper.insert(entity);
        } else {
            exist.setBgType(resolvedBgType);
            exist.setBgValue(bgValue);
            chatBgMapper.updateById(exist);
        }
    }

    @Override
    public ImUserChatBgDO getBg(Long userId, String convKey) {
        String resolvedConvKey = StringUtils.hasText(convKey) ? convKey : GLOBAL_CONV_KEY;
        ImUserChatBgDO entity = chatBgMapper.selectByUserIdAndConvKey(userId, resolvedConvKey);
        // 指定会话未单独设置时，回退到全局默认背景
        if (entity == null && StringUtils.hasText(convKey)) {
            entity = chatBgMapper.selectByUserIdAndConvKey(userId, GLOBAL_CONV_KEY);
        }
        return entity;
    }
}
