package com.diqin.cloud.module.im.service.album;

import com.diqin.cloud.module.im.dal.dataobject.album.ImUserAlbumDO;
import com.diqin.cloud.module.im.dal.mysql.album.ImUserAlbumMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
public class ImUserAlbumServiceImpl implements ImUserAlbumService {

    @Resource
    private ImUserAlbumMapper albumMapper;

    @Override
    public Long createAlbum(Long userId, String imageUrl, String thumbUrl) {
        ImUserAlbumDO entity = ImUserAlbumDO.builder()
                .userId(userId)
                .imageUrl(imageUrl)
                .thumbUrl(thumbUrl)
                .build();
        albumMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void deleteAlbum(Long userId, Long id) {
        albumMapper.delete(id, userId);
    }

    @Override
    public List<ImUserAlbumDO> getAlbumList(Long userId) {
        return albumMapper.selectListByUserId(userId);
    }
}
