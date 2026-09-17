package com.diqin.cloud.module.im.service.user;

import com.diqin.cloud.module.im.controller.admin.user.vo.ImUserRespVO;
import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserCardRespVO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IM 用户对象转换工具类
 * <p>
 * 集中处理 {@link ImUserDO} → VO 的转换，
 * 消除各 Controller 中重复的字段映射代码（isBanned → banned、日期格式化等）。
 */
public class ImUserConvert {

    /**
     * ImUserDO → ImUserRespVO
     */
    public static ImUserRespVO toRespVO(ImUserDO user) {
        if (user == null) {
            return null;
        }
        ImUserRespVO vo = new ImUserRespVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setSex(user.getSex());
        vo.setSignature(user.getSignature());
        vo.setMobile(user.getMobile());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setBanned(user.getIsBanned());
        vo.setBanReason(user.getBanReason());
        vo.setLoginIp(user.getLoginIp());
        if (user.getLoginDate() != null) {
            vo.setLoginDate(user.getLoginDate().toString());
        }
        if (user.getRegisterTime() != null) {
            vo.setCreateTime(user.getRegisterTime().toString());
        }
        return vo;
    }

    /**
     * 批量 ImUserDO → ImUserRespVO
     */
    public static List<ImUserRespVO> toRespVOList(List<ImUserDO> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(ImUserConvert::toRespVO).collect(Collectors.toList());
    }

    /**
     * ImUserDO Map → ImUserRespVO Map
     * <p>供 Controller 批量填充昵称/头像时使用。
     */
    public static Map<Long, ImUserRespVO> toRespVOMap(Map<Long, ImUserDO> userMap) {
        if (userMap == null || userMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toRespVO(e.getValue())));
    }

    // ==================== App AppImUserCardRespVO（搜索结果） ====================

    /**
     * ImUserDO → AppImUserCardRespVO（卡片/搜索结果，不含敏感字段）
     *
     * @param user 用户 DO
     * @return 卡片 VO（null 安全）
     */
    public static AppImUserCardRespVO toCardVO(ImUserDO user) {
        if (user == null) {
            return null;
        }
        AppImUserCardRespVO vo = new AppImUserCardRespVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setSex(user.getSex());
        vo.setSignature(user.getSignature());
        return vo;
    }

    /**
     * 批量 ImUserDO → AppImUserCardRespVO
     */
    public static List<AppImUserCardRespVO> toCardVOList(List<ImUserDO> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(ImUserConvert::toCardVO).collect(Collectors.toList());
    }

    // ==================== 工具方法 ====================

    /**
     * 从 ImUserDO Map 中按 userId 提取昵称（null 安全）
     */
    public static String getNickname(Map<Long, ImUserDO> userMap, Long userId) {
        if (userId == null || userMap == null) {
            return null;
        }
        ImUserDO user = userMap.get(userId);
        return user != null ? user.getNickname() : null;
    }

    /**
     * 从 ImUserDO Map 中按 userId 提取头像（null 安全）
     */
    public static String getAvatar(Map<Long, ImUserDO> userMap, Long userId) {
        if (userId == null || userMap == null) {
            return null;
        }
        ImUserDO user = userMap.get(userId);
        return user != null ? user.getAvatar() : null;
    }

}
