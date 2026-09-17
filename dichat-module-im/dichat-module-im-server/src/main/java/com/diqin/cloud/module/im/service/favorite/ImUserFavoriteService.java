package com.diqin.cloud.module.im.service.favorite;

import com.diqin.cloud.module.im.dal.dataobject.favorite.ImUserFavoriteDO;

import java.util.List;

public interface ImUserFavoriteService {

    /**
     * 收藏消息
     */
    Long createFavorite(Long userId, Long messageId, Integer messageType, String messageContent, String senderName, String convKey);

    /**
     * 取消收藏
     */
    void deleteFavorite(Long userId, Long id);

    /**
     * 获取用户的收藏列表
     */
    List<ImUserFavoriteDO> getFavoriteList(Long userId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long messageId);
}
