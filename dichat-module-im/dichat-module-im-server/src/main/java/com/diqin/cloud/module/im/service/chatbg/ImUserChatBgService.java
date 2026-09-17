package com.diqin.cloud.module.im.service.chatbg;

import com.diqin.cloud.module.im.dal.dataobject.chatbg.ImUserChatBgDO;

public interface ImUserChatBgService {

    /**
     * 同步聊天背景（图片URL 或 颜色值）
     *
     * @param userId  用户编号
     * @param bgValue 背景值：图片URL 或 颜色十六进制（如 #409EFF）
     * @param bgType  背景类型：1=图片(url) 2=颜色(hex)；为 null 时按值自动推断
     * @param convKey 会话key；为空表示全局默认背景
     */
    void syncBg(Long userId, String bgValue, Integer bgType, String convKey);

    /**
     * 获取聊天背景。会话未单独设置时回退到全局默认背景。
     *
     * @param userId  用户编号
     * @param convKey 会话key；为空表示查询全局默认背景
     * @return 背景 DO；未设置时返回 null
     */
    ImUserChatBgDO getBg(Long userId, String convKey);
}
