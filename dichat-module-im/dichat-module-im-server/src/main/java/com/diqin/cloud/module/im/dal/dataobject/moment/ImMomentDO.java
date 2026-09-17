package com.diqin.cloud.module.im.dal.dataobject.moment;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

@TableName("im_moment")
@KeySequence("im_moment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImMomentDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String content;
    private String images;
    private Integer visibility;
    private Integer likeCount;
    private Integer commentCount;
    /** 发布位置（地名 / 地址） */
    private String location;
    /** 提醒谁看的用户编号列表（JSON 数组） */
    private String remindUserIds;
    /** 部分可见白名单用户编号列表（JSON 数组，visibility=2 时生效） */
    private String visibleUserIds;
    /** 不给谁看黑名单用户编号列表（JSON 数组，visibility=3 时生效） */
    private String invisibleUserIds;
}
