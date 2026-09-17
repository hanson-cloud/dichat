package com.diqin.cloud.module.im.dal.dataobject.favorite;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

@TableName("im_user_favorite")
@KeySequence("im_user_favorite_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImUserFavoriteDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String convKey;
    private Long messageId;
    private Integer messageType;
    private String messageContent;
    private String senderName;
}
