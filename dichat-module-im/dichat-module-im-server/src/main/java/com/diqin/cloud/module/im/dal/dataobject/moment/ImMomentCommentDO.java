package com.diqin.cloud.module.im.dal.dataobject.moment;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

@TableName("im_moment_comment")
@KeySequence("im_moment_comment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImMomentCommentDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long momentId;
    private Long userId;
    private Long replyUserId;
    private String content;
}
