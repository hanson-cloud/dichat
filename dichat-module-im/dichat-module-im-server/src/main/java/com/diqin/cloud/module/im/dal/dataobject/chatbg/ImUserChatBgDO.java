package com.diqin.cloud.module.im.dal.dataobject.chatbg;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

@TableName("im_user_chat_bg")
@KeySequence("im_user_chat_bg_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImUserChatBgDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String convKey;
    private Integer bgType;
    private String bgValue;
}
