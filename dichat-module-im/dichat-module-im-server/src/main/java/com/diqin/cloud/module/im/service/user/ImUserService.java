package com.diqin.cloud.module.im.service.user;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.validation.Mobile;
import com.diqin.cloud.module.im.controller.admin.user.vo.ImUserPageReqVO;
import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserPrivacyUpdateReqVO;
import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.user.bo.NearbyUserBO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IM 用户 Service 接口
 *
 * <p>对应 im_users 表，对外提供：
 * <ul>
 *   <li>注册 / 登录 / 改密 — 走 im 模块独立账号体系</li>
 *   <li>getUser / getUserList / getUserMap / getUserListByKeyword — 供其他模块 RPC 替代 AdminUserApi</li>
 *   <li>用户资料更新 / 头像 / 昵称</li>
 * </ul>
 *
 * @author hanson
 */
public interface ImUserService {

    /**
     * 修改密码（需登录；校验原密码）
     */
    void modifyPassword(Long userId, String oldPassword, String newPassword);

    /**
     * 基于手机号创建用户。
     * 如果用户已经存在，则直接进行返回
     *
     * @param mobile     手机号
     * @param registerIp 注册 IP
     * @return 用户对象
     */
    ImUserDO createUserIfAbsent(@Mobile String mobile,String nickname, String password, String registerIp);

    // ===================== 查询（供其他业务模块调用） =====================

    /**
     * 通过 ID 查询用户
     */
    ImUserDO getUser(Long id);

    /**
     * 通过 ID 集合查询用户
     */
    List<ImUserDO> getUserList(Collection<Long> ids);

    /**
     * 按微信标准搜索用户（带优先级排序 + 隐私过滤 + 排除自己）
     *
     * <p>搜索优先级：
     * <ol>
     *   <li>精确匹配 username（需对方开启 allowFindByUsername）</li>
     *   <li>精确匹配 nickname</li>
     *   <li>nickname 前缀匹配（以关键词开头）</li>
     *   <li>nickname 模糊匹配（包含关键词）</li>
     *   <li>手机号精确匹配（需对方开启 allowFindByMobile，且调用方允许手机号搜索）</li>
     * </ol>
     *
     * @param keyword       搜索关键词
     * @param excludeUserId 排除的用户 ID（通常是当前登录用户，传 null 表示不排除）
     * @return 按优先级排序、去重后的用户列表，最多 20 条
     */
    List<ImUserDO> searchUsers(String keyword, Long excludeUserId);

    /**
     * 校验用户是否有效（存在 + 未停用 + 未封禁）；无效抛业务异常
     */
    void validateUser(Long id);

    /**
     * 批量校验用户是否有效
     */
    void validateUserList(Collection<Long> ids);

    /**
     * 批量查询用户，返回 Map（id → ImUserDO）
     * <p>供 Manager Controller 批量填充用户昵称、头像使用。
     */
    Map<Long, ImUserDO> getUserMap(Collection<Long> ids);

    // ===================== 用户端（修改自己资料） =====================

    /**
     * 更新自己资料（昵称 / 头像 / 性别 / 签名）
     * <p>
     * 修改 username / mobile / email / status 等敏感字段。
     * <p>
     */
    void updateProfile(Long userId, AppImUserUpdateReqVO reqVO);

    /**
     * 注销账号（软注销：匿名化资料 + 封禁 + 强制下线所有终端）
     * <p>不物理删除，避免跨模块级联；好友关系 / 会话数据保留但不可见。
     *
     * @param userId 用户编号
     */
    void cancelAccount(Long userId);

    /**
     * 绑定 / 修改手机号或邮箱（直接更新 im_users.mobile / email）
     * <p>本期不做验证码校验、不做唯一性强校验，后续接 SMS / 邮件与安全评估。
     *
     * @param userId 用户编号
     * @param reqVO  绑定请求（type=mobile/email, value）
     */
    void bindMobileOrEmail(Long userId, com.diqin.cloud.module.im.controller.app.user.vo.AppImUserBindReqVO reqVO);

    /**
     * 更新当前用户隐私设置
     *
     * @param userId 用户编号
     * @param reqVO  隐私设置
     */
    void updatePrivacy(Long userId, AppImUserPrivacyUpdateReqVO reqVO);

    // ===================== 管理后台 =====================

    /**
     * 管理后台 - 分页查询
     */
    PageResult<ImUserDO> getUserPage(ImUserPageReqVO reqVO);

    /**
     * 管理后台 - 获得 IM 用户精简列表（id / 账号 / 昵称 / 头像）
     * <p>供前端表单下拉选择使用（如机器人关联用户、客服绑定 IM 身份）。
     * 仅返回账号正常且未封禁的用户。
     */
    List<ImUserDO> getSimpleUserList();


    /**
     * 管理后台 - 封禁 / 解封
     */
    void updateBanned(Long id, Boolean banned, String reason);

    /**
     * 管理后台 - 启停账号
     */
    void updateStatus(Long id, Integer status);

    /**
     * 管理后台 - 重置密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 通过账号查询用户
     *
     * @param username 手机
     * @return 用户对象
     */
    ImUserDO getUserByUsername(String username);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword     未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 更新用户的最后登陆信息，并落库一条登录日志（审计台账）
     *
     * @param id        用户编号
     * @param loginIp   登录 IP
     * @param terminal  终端类型（TerminalEnum）
     * @param userAgent 浏览器 / 设备 UA
     */
    void updateUserLogin(Long id, String loginIp, Integer terminal, String userAgent);

    // ===================== 附近的人 =====================

    /**
     * 上报当前用户位置（供「附近的人」使用）
     * <p>更新 lat / lng / location_update_time；位置可见性由用户资料里的 {@code locationVisible} 独立控制（默认开启）。
     *
     * @param userId 用户编号
     * @param lat    纬度（-90~90）
     * @param lng    经度（-180~180）
     */
    void reportLocation(Long userId, Double lat, Double lng);

    /**
     * 查询附近的人（按距离由近及远排序）
     * <p>仅返回开启位置可见、坐标有效、且在半径内的用户，排除自己。
     *
     * @param lat            中心点纬度
     * @param lng            中心点经度
     * @param excludeUserId 排除的用户 ID（当前登录用户）
     * @param radiusMeters  半径（米）
     * @param limit          最多返回条数
     * @return 附带精确距离（米）的附近用户 BO 列表
     */
    List<NearbyUserBO> getNearbyUsers(Double lat, Double lng, Long excludeUserId,
                                     Integer radiusMeters, Integer limit);
}
