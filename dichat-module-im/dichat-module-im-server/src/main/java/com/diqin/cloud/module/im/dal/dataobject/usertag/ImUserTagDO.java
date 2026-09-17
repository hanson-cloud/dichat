package com.diqin.cloud.module.im.dal.dataobject.usertag;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 用户标签 DO
 *
 * @author 速构构
 */
@TableName(value = "im_user_tag", autoResultMap = true)
@KeySequence("im_user_tag_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImUserTagDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 标签名称（全局唯一）
     */
    private String name;
    /**
     * 标签颜色（十六进制，如 #409EFF），用于前端标识
     */
    private String color;
    /**
     * 标签描述
     */
    private String description;
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
