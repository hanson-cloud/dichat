package com.diqin.cloud.module.im.service.album;

import com.diqin.cloud.module.im.dal.dataobject.album.ImUserAlbumDO;

import java.util.List;

public interface ImUserAlbumService {

    /**
     * 上传（新增）一张相册照片
     */
    Long createAlbum(Long userId, String imageUrl, String thumbUrl);

    /**
     * 删除相册照片
     */
    void deleteAlbum(Long userId, Long id);

    /**
     * 获取用户的相册列表（按上传时间倒序）
     */
    List<ImUserAlbumDO> getAlbumList(Long userId);
}
