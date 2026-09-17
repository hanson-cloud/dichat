package com.diqin.cloud.module.im.dal.dataobject.face;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 表情包项 DO（系统表情包内的单张表情图）
 *
 * @author hanson
 */
@TableName("im_face_pack_item")
@KeySequence("im_face_pack_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImFacePackItemDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 所属表情包编号
     */
    private Long packId;
    /**
     * 表情图 URL
     */
    private String url;
    /**
     * 表情名（可选；如「狗头」「捂脸」）
     */
    private String name;
    /**
     * 渲染宽度（像素）
     */
    private Integer width;
    /**
     * 渲染高度（像素）
     */
    private Integer height;
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
