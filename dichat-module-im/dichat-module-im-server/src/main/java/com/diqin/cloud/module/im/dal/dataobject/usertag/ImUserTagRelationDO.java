package com.diqin.cloud.module.im.dal.dataobject.usertag;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 用户标签关联关系 DO
 *
 * @author 速构构
 */
@TableName(value = "im_user_tag_relation", autoResultMap = true)
@KeySequence("im_user_tag_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImUserTagRelationDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 标签编号
     */
    private Long tagId;

}
