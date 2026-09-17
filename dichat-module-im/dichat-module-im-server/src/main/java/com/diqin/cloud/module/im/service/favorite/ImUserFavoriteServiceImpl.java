package com.diqin.cloud.module.im.service.favorite;

import com.diqin.cloud.module.im.dal.dataobject.favorite.ImUserFavoriteDO;
import com.diqin.cloud.module.im.dal.mysql.favorite.ImUserFavoriteMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
public class ImUserFavoriteServiceImpl implements ImUserFavoriteService {

    @Resource
    private ImUserFavoriteMapper favoriteMapper;

    @Override
    public Long createFavorite(Long userId, Long messageId, Integer messageType, String messageContent, String senderName, String convKey) {
        // 检查是否已收藏
        ImUserFavoriteDO exist = favoriteMapper.selectByUserIdAndMessageId(userId, messageId);
        if (exist != null) {
            return exist.getId();
        }
        ImUserFavoriteDO entity = ImUserFavoriteDO.builder()
                .userId(userId)
                .convKey(convKey)
                .messageId(messageId)
                .messageType(messageType)
                .messageContent(messageContent)
                .senderName(senderName)
                .build();
        favoriteMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void deleteFavorite(Long userId, Long id) {
        favoriteMapper.delete(id, userId);
    }

    @Override
    public List<ImUserFavoriteDO> getFavoriteList(Long userId) {
        return favoriteMapper.selectListByUserId(userId);
    }

    @Override
    public boolean isFavorited(Long userId, Long messageId) {
        return favoriteMapper.selectByUserIdAndMessageId(userId, messageId) != null;
    }
}
