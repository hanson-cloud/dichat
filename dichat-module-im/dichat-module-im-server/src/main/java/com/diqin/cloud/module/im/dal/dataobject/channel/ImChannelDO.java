package com.diqin.cloud.module.im.dal.dataobject.channel;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 频道 DO
 * <p>
 * 业务语义：
 * - 频道是运营单向推送的主体；C 端用户不能向频道发消息
 * - {@link #code} 是业务码（API / 字典外露），id 是数字主键给前端会话 targetId 用
 *
 * @author hanson
 */
@TableName("im_channel")
@KeySequence("im_channel_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImChannelDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 频道业务码
     */
    private String code;
    /**
     * 频道名称
     */
    private String name;
    /**
     * 频道头像
     */
    private String avatar;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
