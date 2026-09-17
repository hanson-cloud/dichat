package com.diqin.cloud.module.im.dal.dataobject.album;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

@TableName("im_user_album")
@KeySequence("im_user_album_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImUserAlbumDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    /** 原图 URL */
    private String imageUrl;
    /** 缩略图 URL */
    private String thumbUrl;
}
