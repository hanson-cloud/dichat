package com.diqin.cloud.module.im.service.user;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.enums.TerminalEnum;
import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.admin.user.vo.ImUserPageReqVO;
import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserBindReqVO;
import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserPrivacyUpdateReqVO;
import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.user.ImUserMapper;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.enums.ForceOfflineReason;
import com.diqin.cloud.module.im.service.loginlog.ImUserLoginLogManagerService;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import com.diqin.cloud.module.im.framework.ipregion.core.annotation.LoginAreaRecord;
import com.diqin.cloud.module.im.mq.producer.user.ImUserProducer;
import com.diqin.cloud.module.im.service.user.bo.NearbyUserBO;
import com.diqin.cloud.module.im.util.SensitiveWordUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * IM 用户 Service 实现
 *
 * <p>密码：BCrypt 加密（{@link PasswordEncoder#encode} / {@link PasswordEncoder#matches}）
 * <br>登录：复用 dichat 的 {@link SecurityFrameworkUtils#setLoginUser} 写 SecurityContext，
 * 后续 controller 通过 {@code @PreAuthorize} / 业务层 {@code SecurityFrameworkUtils.getLoginUserId()} 即可拿到当前用户。
 *
 * @author hanson
 */
@Slf4j
@Service
public class ImUserServiceImpl implements ImUserService {

    /** 用户名正则：3-30 位字母 / 数字 / 下划线 */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,30}$");

    @Resource
    private ImUserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private ImProperties imProperties;

    @Resource
    private ImUserProducer imUserProducer;

    @Resource
    private ImUserLoginLogManagerService loginLogManagerService;

    @Override
    public void modifyPassword(Long userId, String oldPassword, String newPassword) {
        validatePassword(newPassword);
        ImUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_NOT_EXISTS);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_OLD_PASSWORD_ERROR);
        }
        ImUserDO update = new ImUserDO();
        update.setId(userId);
        update.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImUserDO createUserIfAbsent(String mobile,String nickname, String password, String registerIp) {
        // 用户已经存在
        ImUserDO user = userMapper.selectByUsername(mobile);
        if (user != null) {
            return user;
        }
        // 用户不存在，则进行创建
        return createUser(mobile, null, password, nickname, null, CommonStatusEnum.ENABLE.getStatus(),registerIp);
    }

    private ImUserDO createUser(String mobile, String email, String password, String nickname, String avtar, Integer status,
                                    String registerIp) {
        // 生成密码
        if (password == null) {
            password = IdUtil.fastSimpleUUID();
        }
        // 插入用户
        ImUserDO user = new ImUserDO();
        user.setUsername("dc"+RandomUtil.randomStringLower(10));
        user.setMobile(mobile);
        user.setEmail(email);
        user.setStatus(status);
        user.setPassword(encodePassword(password));
        user.setLoginIp(registerIp);
        user.setNickname(nickname).setAvatar(avtar);
        if (CharSequenceUtil.isEmpty(nickname)) {
            // 昵称为空时，随机一个名字，避免一些依赖 nickname 的逻辑报错，或者有点丑。例如说，短信发送有昵称时~
            user.setNickname("用户" + RandomUtil.randomNumbers(6));
        }
        userMapper.insert(user);

        // 发送 MQ 消息：用户创建
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                imUserProducer.sendUserCreateMessage(user.getId());
            }

        });
        return user;
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    // ===================== 查询 =====================

    @Override
    public ImUserDO getUser(Long id) {
        if (id == null) {
            return null;
        }
        return userMapper.selectById(id);
    }

    @Override
    public List<ImUserDO> getUserList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userMapper.selectListByIds(ids);
    }

    @Override
    public List<ImUserDO> searchUsers(String keyword, Long excludeUserId) {
        if (StrUtil.isBlank(keyword)) {
            return List.of();
        }
        // 手机号格式判断：11 位数字视为可能手机号搜索
        boolean isMobileSearch = keyword.trim().matches("^\\d{11}$");
        // 调用优先级搜索，最多 20 条；手机号搜索需对方开启隐私开关
        return userMapper.selectListByKeywordWithPriority(
                keyword.trim(), excludeUserId, isMobileSearch, 20);
    }

    @Override
    public void validateUser(Long id) {
        ImUserDO user = getUser(id);
        validateUserInternal(user, id);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<ImUserDO> users = userMapper.selectListByIds(ids);
        // 索引 by id 便于 O(1) 查
        java.util.Map<Long, ImUserDO> userMap = new java.util.HashMap<>(users.size());
        for (ImUserDO u : users) {
            userMap.put(u.getId(), u);
        }
        for (Long id : ids) {
            ImUserDO user = userMap.get(id);
            validateUserInternal(user, id);
        }
    }

    private void validateUserInternal(ImUserDO user, Long id) {
        if (id == null || user == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_NOT_EXISTS);
        }
        if (CommonStatusEnum.DISABLE.getStatus().equals(user.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_DISABLED);
        }
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_BANNED,
                    user.getBanReason() == null ? "" : user.getBanReason());
        }
    }

    @Override
    public Map<Long, ImUserDO> getUserMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ImUserDO> users = getUserList(ids);
        return users.stream().collect(java.util.stream.Collectors.toMap(ImUserDO::getId, u -> u));
    }

    // ===================== 用户端 =====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, AppImUserUpdateReqVO reqVO) {
        ImUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_NOT_EXISTS);
        }
        // 敏感词
        if (StrUtil.isNotBlank(reqVO.getNickname()) && SensitiveWordUtils.contains(reqVO.getNickname())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_NICKNAME_CONTAINS_SENSITIVE);
        }
        ImUserDO update = BeanUtils.toBean(reqVO, ImUserDO.class);
        update.setId(userId);
        // 保留敏感字段不变
        update.setPassword(null);
        update.setUsername(null);
        userMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrivacy(Long userId, AppImUserPrivacyUpdateReqVO reqVO) {
        ImUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_NOT_EXISTS);
        }
        ImUserDO update = BeanUtils.toBean(reqVO, ImUserDO.class);
        update.setId(userId);
        // 保留敏感/非隐私字段不变
        update.setPassword(null);
        update.setUsername(null);
        update.setMobile(null);
        update.setEmail(null);
        update.setStatus(null);
        update.setNickname(null);
        update.setAvatar(null);
        update.setSex(null);
        update.setSignature(null);
        userMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAccount(Long userId) {
        ImUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_NOT_EXISTS);
        }
        // 已注销（匿名化账号）防御重复注销
        if (Boolean.TRUE.equals(user.getIsBanned())
                && user.getUsername() != null && user.getUsername().startsWith("deleted_")) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.ACCOUNT_ALREADY_CANCELLED);
        }
        // 软注销：匿名化资料 + 封禁 + 强制下线所有终端（不物理删除，避免跨模块级联）
        ImUserDO update = new ImUserDO();
        update.setId(userId);
        update.setStatus(CommonStatusEnum.DISABLE.getStatus()); // 0=禁用
        update.setIsBanned(true);
        update.setBanReason("用户主动注销");
        update.setUsername("deleted_" + userId);
        update.setNickname("已注销用户");
        update.setMobile("");
        update.setEmail("");
        update.setAvatar("");
        update.setSignature("");
        // 注销同时解除好友关系可见性：清空个性签名，friends 表保留但对方侧展示「已注销用户」
        userMapper.updateById(update);
        // 强制下线所有终端（reason=DEREGISTER，客户端展示「账号已注销」）
        loginLogManagerService.forceOfflineByUserId(userId, ForceOfflineReason.DEREGISTER, null);
        // 备注：好友关系 / 会话数据本期保留但不可见（对方资料页展示「已注销用户」）
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindMobileOrEmail(Long userId, AppImUserBindReqVO reqVO) {
        // 安全待评估：本期不做验证码 / 密码二次校验，直接更新 im_users.mobile / email
        String type = reqVO.getType();
        String value = reqVO.getValue();
        ImUserDO update = new ImUserDO();
        update.setId(userId);
        if ("mobile".equals(type)) {
            if (!cn.hutool.core.lang.Validator.isMobile(value)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_BIND_VALUE_INVALID, "手机号需为 11 位有效号码");
            }
            update.setMobile(value);
        } else if ("email".equals(type)) {
            if (!cn.hutool.core.lang.Validator.isEmail(value)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_BIND_VALUE_INVALID, "邮箱格式不合法");
            }
            update.setEmail(value);
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_BIND_TYPE_INVALID);
        }
        userMapper.updateById(update);
    }

    // ===================== 管理后台 =====================

    @Override
    public PageResult<ImUserDO> getUserPage(ImUserPageReqVO reqVO) {
        return userMapper.selectPage(reqVO);
    }

    @Override
    public List<ImUserDO> getSimpleUserList() {
        return userMapper.selectSimpleUserList();
    }

    @Override
    public void updateBanned(Long id, Boolean banned, String reason) {
        ImUserDO update = new ImUserDO();
        update.setId(id);
        update.setIsBanned(banned);
        update.setBanReason(Boolean.TRUE.equals(banned) ? reason : null);
        userMapper.updateById(update);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ImUserDO update = new ImUserDO();
        update.setId(id);
        update.setStatus(status);
        userMapper.updateById(update);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        validatePassword(newPassword);
        ImUserDO update = new ImUserDO();
        update.setId(id);
        update.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(update);
    }

    @Override
    public ImUserDO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    @LoginAreaRecord
    public void updateUserLogin(Long id, String loginIp, Integer terminal, String userAgent) {
        ImUserDO update = new ImUserDO().setId(id)
                .setLoginIp(loginIp).setLoginDate(LocalDateTime.now());
        // 仅当客户端上报了有效（非未知）终端时才回写设备类型，避免用「未知」覆盖既有真实设备
        if (terminal != null && !TerminalEnum.UNKNOWN.getTerminal().equals(terminal)) {
            update.setDeviceType(terminal);
        }
        userMapper.updateById(update);
    }

    // ===================== 附近的人 =====================

    @Override
    public void reportLocation(Long userId, Double lat, Double lng) {
        ImUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_NOT_EXISTS);
        }
        ImUserDO update = new ImUserDO().setId(userId)
                .setLat(lat).setLng(lng).setLocationUpdateTime(LocalDateTime.now());
        userMapper.updateById(update);
    }

    @Override
    public List<NearbyUserBO> getNearbyUsers(Double lat, Double lng, Long excludeUserId,
                                            Integer radiusMeters, Integer limit) {
        // 1. Mapper 边界框粗筛（走索引），拿到半径框内候选
        List<ImUserDO> candidates = userMapper.selectNearbyCandidates(lat, lng, excludeUserId, radiusMeters);
        if (candidates.isEmpty()) {
            return List.of();
        }
        // 2. Java 侧 Haversine 精算距离 → 过滤半径内 → 按距离升序 → 截断
        int topN = limit != null && limit > 0 ? limit : 50;
        return candidates.stream()
                .filter(u -> u.getLat() != null && u.getLng() != null)
                .map(u -> {
                    NearbyUserBO bo = new NearbyUserBO();
                    bo.setUser(u);
                    bo.setDistance(haversineMeters(lat, lng, u.getLat(), u.getLng()));
                    return bo;
                })
                .filter(bo -> bo.getDistance() <= radiusMeters)
                .sorted(Comparator.comparingDouble(NearbyUserBO::getDistance))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Haversine 公式：计算两点间球面距离（米）
     */
    private static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000; // 地球平均半径（米）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }

    // ===================== 内部工具 =====================
    private void validatePassword(String password) {
        int min = imProperties.getAuth().getPasswordMinLength();
        int max = imProperties.getAuth().getPasswordMaxLength();
        if (StrUtil.isBlank(password) || password.length() < min || password.length() > max) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IM_USER_INVALID_PASSWORD, min, max);
        }
    }

}
