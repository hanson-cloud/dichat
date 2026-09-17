package com.diqin.cloud.module.im.dal.dataobject.friend;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 好友关系 DO
 * <p>
 * 业务语义：
 * - 双向关系：A-B 互为好友会存 2 条记录（userId=A, friendUserId=B 和 userId=B, friendUserId=A）
 * - 状态管理：{@link #status} 使用 {@link CommonStatusEnum}，ENABLE=正常，DISABLE=已删除
 * - 免打扰：{@link #silent} 控制是否屏蔽来自该好友的通知
 * - 联系人置顶：{@link #pinned} 单边，影响联系人 / 会话排序
 * - 黑名单：{@link #blocked} 弱关联 friend，单边屏蔽对方消息（必须先是好友）
 *
 * @author hanson
 */
@TableName("im_friend")
@KeySequence("im_friend_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImFriendDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     * <p>
     * 关联 AdminUserDO 的 id 字段
     */
    private Long userId;
    /**
     * 好友用户编号
     * <p>
     * 关联 AdminUserDO 的 id 字段
     */
    private Long friendUserId;
    /**
     * 是否免打扰
     */
    private Boolean silent;
    /**
     * 好友展示备注
     */
    private String displayName;
    /**
     * 备注电话（多号逗号分隔，仅自己可见）
     * <p>
     * 与 {@link #displayName} 同属单边备注类数据；前端设置页可编辑
     */
    private String phone;
    /**
     * 文字备注（仅自己可见）
     */
    private String remark;
    /**
     * 添加来源
     * <p>
     * 枚举 {@link com.diqin.cloud.module.im.enums.friend.ImFriendAddSourceEnum}
     */
    private Integer addSource;
    /**
     * 是否置顶联系人
     */
    private Boolean pinned;
    /**
     * 是否拉黑（弱关联 friend，单边屏蔽对方私聊消息）
     */
    private Boolean blocked;
    /**
     * 好友状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 添加好友时间
     */
    private LocalDateTime addTime;
    /**
     * 删除好友时间
     * <p>
     * 不为 null 时表示已删除
     */
    private LocalDateTime deleteTime;
    /**
     * 对方已读到我发的最大消息编号
     * <p>
     * 用于已读回执：A 给 B 发消息，B 读了之后更新 A 的好友记录上的这个字段，
     * 这样 A 进入会话时能知道 B 已读到哪条消息（显示"已读"双勾）
     */
    private Long lastReadMessageId;

}
