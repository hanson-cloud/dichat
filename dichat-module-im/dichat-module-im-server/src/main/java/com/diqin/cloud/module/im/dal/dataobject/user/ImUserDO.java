package com.diqin.cloud.module.im.dal.dataobject.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 用户 DO
 *
 * <p>独立维护 im_users 表；不依赖 system_users（解耦 system 管理员体系）。
 * 对应表结构参考 box-im `im_user` + 业务扩充。
 *
 * @author hanson
 */
@TableName(value = "im_users", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImUserDO extends TenantBaseDO {

    /**
     * 用户编号
     */
    @TableId
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码（BCrypt 加密后密文）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String avatar;

//    /**
//     * 头像缩略图
//     */
//    private String avatarThumb;

    /**
     * 性别 0:男 1:女
     */
    private Integer sex;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账号状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 账号是否被封禁
     */
    private Boolean isBanned;

    /**
     * 封禁原因
     */
    private String banReason;

    /**
     * 最后登录 IP
     */
    private String loginIp;

    /**
     * 登录 IP 解析出的区域编号（area_id）
     * <p>
     * 由登录事务提交后异步触发 {@code LoginAreaWritebackListener} 经 system 远程
     * {@code AreaApi} 解析并回写；引用 dichat 静态区域树（area.csv），稳定且可聚合。
     * 看板的「全球 / 区域分布」直接按本列 GROUP BY，不再依赖 im_ip_region 文本表。
     * </p>
     */
    private Long areaId;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;

    /**
     * 设备类型（用户最近一次登录/活跃所使用的终端）
     * <p>
     * 枚举 {@link com.diqin.cloud.framework.common.enums.TerminalEnum}
     */
    private Integer deviceType;

    /**
     * 注册时间（冗余 BaseDO.createTime，便于业务侧按注册时间筛选）
     */
    private LocalDateTime registerTime;

    /**
     * 是否允许通过手机号找到我（隐私设置）
     * <p>
     * 对应微信「添加我的方式 - 手机号」
     * true: 允许（默认）；false: 不允许
     */
    private Boolean allowFindByMobile;

    /**
     * 是否允许通过账号（username / DiChatID）找到我（隐私设置）
     * <p>
     * 对应微信「添加我的方式 - 微信号」
     * true: 允许（默认）；false: 不允许
     */
    private Boolean allowFindByUsername;

    /**
     * 加我为好友时是否需要验证（隐私设置）
     * <p>
     * true: 需要验证（默认）；false: 允许直接添加
     */
    private Boolean friendVerify;

    /**
     * 向好友公开我的动态（朋友圈 / 时刻）（隐私设置）
     * <p>
     * true: 公开（默认）；false: 不公开
     */
    private Boolean publicMoments;

    /**
     * 纬度（-90~90）；NULL 表示从未上报位置
     */
    private Double lat;

    /**
     * 经度（-180~180）；NULL 表示从未上报位置
     */
    private Double lng;

    /**
     * 是否允许被「附近的人」看到（位置隐私开关）
     * <p>
     * 对应微信「附近的人」可见性；true: 允许（默认）；false: 不允许
     */
    private Boolean locationVisible;

    /**
     * 位置最后上报时间（用于按新鲜度排序 / 过期清理）
     */
    private LocalDateTime locationUpdateTime;

}
