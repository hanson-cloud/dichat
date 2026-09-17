package com.diqin.cloud.module.im.controller.app.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImChangePasswordReqVO;
import com.diqin.cloud.module.im.controller.app.online.vo.AppImOnlineTerminalRespVO;
import com.diqin.cloud.module.im.controller.app.user.vo.*;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.enums.ForceOfflineReason;
import com.diqin.cloud.module.im.service.loginlog.ImUserLoginLogManagerService;
import com.diqin.cloud.module.im.service.online.ImOnlineService;
import com.diqin.cloud.module.im.service.user.ImUserConvert;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.diqin.cloud.module.system.api.logger.LoginLogApi;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogPageReqDTO;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogRespDTO;
import com.diqin.cloud.module.system.enums.logger.LoginLogStatusEnum;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * App - IM 用户
 *
 * @author hanson
 */
@Tag(name = "用户 APP - IM 用户")
@RestController
@RequestMapping("/im/user")
@Validated
public class AppImUserController {

    @Resource
    private ImUserService userService;

    @Resource
    private ImOnlineService onlineService;

    @Resource
    private ImUserLoginLogManagerService loginLogManagerService;

    @Resource
    private LoginLogApi loginLogApi;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public CommonResult<AppImUserRespVO> me() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        ImUserDO user = userService.getUser(userId);
        return success(BeanUtils.toBean(user, AppImUserRespVO.class));
    }

    @PutMapping("/me")
    @Operation(summary = "修改当前用户信息", description = "修改当前登录用户的资料（昵称 / 头像 / 签名 / 性别）")
    public CommonResult<Boolean> updateMe(@Valid @RequestBody AppImUserUpdateReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userService.updateProfile(userId, reqVO);
        return success(true);
    }

    @GetMapping("/me/privacy")
    @Operation(summary = "获取当前用户隐私设置")
    public CommonResult<AppImUserRespVO> getMyPrivacy() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        ImUserDO user = userService.getUser(userId);
        return success(BeanUtils.toBean(user, AppImUserRespVO.class));
    }

    @PutMapping("/me/privacy")
    @Operation(summary = "修改当前用户隐私设置", description = "修改当前登录用户的隐私开关：好友验证 / 手机号 / DiChatID / 动态 / 附近的人")
    public CommonResult<Boolean> updateMyPrivacy(@Valid @RequestBody AppImUserPrivacyUpdateReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userService.updatePrivacy(userId, reqVO);
        return success(true);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户",
            description = "按微信标准搜索：优先级为 账号精确 > 昵称精确 > 昵称前缀 > 昵称模糊。"
                    + "手机号搜索受对方隐私设置控制。最多返回 20 条。自动排除当前登录用户自己。")
    @Parameter(name = "keyword", description = "搜索关键词（账号 / 昵称 / 手机号）", required = true, example = "张三")
    public CommonResult<List<AppImUserCardRespVO>> searchUsers(@RequestParam("keyword") String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return success(Collections.emptyList());
        }
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        // Service 层已处理：优先级排序 + 隐私过滤 + 排除自己
        List<ImUserDO> users = userService.searchUsers(keyword.trim(), userId);
        return success(ImUserConvert.toCardVOList(users));
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "需要登录；校验原密码后写入新密码")
    public CommonResult<Boolean> changePassword(@Valid @RequestBody AppImChangePasswordReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userService.modifyPassword(userId, reqVO.getOldPassword(), reqVO.getNewPassword());
        return success(true);
    }

    @GetMapping("/online-terminals")
    @Operation(summary = "查询用户在线终端", description = "传入逗号分隔的用户编号，返回每个在线用户的终端列表")
    @Parameter(name = "userIds", description = "用户编号列表，逗号分隔", required = true, example = "1,2,3")
    public CommonResult<List<AppImOnlineTerminalRespVO>> getOnlineTerminals(@RequestParam("userIds") String userIds) {
        List<Long> userIdList = parseUserIds(userIds);
        if (CollUtil.isEmpty(userIdList)) {
            return success(Collections.emptyList());
        }
        Map<Long, List<Integer>> terminalMap = onlineService.getOnlineTerminalMap(userIdList);
        return success(terminalMap.entrySet().stream()
                .map(e -> new AppImOnlineTerminalRespVO(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
    }


    @PostMapping("/kick-terminal")
    @Operation(summary = "强制下线指定终端", description = "根据登录日志编号强制用户下线，关闭其 WebSocket 会话")
    @Parameter(name = "terminalUserId", description = "登录日志编号（非用户编号）", required = true, example = "1024")
    public CommonResult<Boolean> kickTerminal(@RequestParam("terminalUserId") Long terminalUserId) {
        // 用户在「设备管理」页踢掉其他终端 / 新设备登录互踢：reason=SELF
        loginLogManagerService.forceOffline(terminalUserId, ForceOfflineReason.SELF);
        return success(true);
    }

    @GetMapping("/login-logs")
    @Operation(summary = "查询我的登录设备列表", description = "返回当前登录用户的所有在线设备（按登录时间倒序），用于设备管理页。" +
            "登录日志已统一收敛到 system 的 system_login_log，此处仅做网关可达的查询转发（IM 不持有数据）。")
    public CommonResult<List<AppImUserLoginLogRespVO>> getMyLoginLogs() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        LoginLogPageReqDTO rpcReq = new LoginLogPageReqDTO();
        rpcReq.setPageNo(1);
        rpcReq.setPageSize(100);
        rpcReq.setUserType(UserTypeEnum.MEMBER.getValue());
        rpcReq.setUserId(userId);
        rpcReq.setOnlineStatus(LoginLogStatusEnum.ONLINE.getStatus());
        PageResult<LoginLogRespDTO> pageResult = loginLogApi.getLoginLogPage(rpcReq).getCheckedData();
        if (pageResult == null || CollUtil.isEmpty(pageResult.getList())) {
            return success(Collections.emptyList());
        }
        return success(pageResult.getList().stream()
                .sorted(Comparator.comparing(LoginLogRespDTO::getCreateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toAppVO)
                .toList());
    }

    /** system_login_log 记录 → App 登录设备 VO（userIp→loginIp、createTime→loginTime） */
    private AppImUserLoginLogRespVO toAppVO(LoginLogRespDTO src) {
        AppImUserLoginLogRespVO vo = BeanUtils.toBean(src, AppImUserLoginLogRespVO.class);
        vo.setLoginIp(src.getUserIp());
        vo.setLoginTime(src.getCreateTime());
        return vo;
    }

    @PostMapping("/location")
    @Operation(summary = "上报当前用户位置", description = "更新 lat/lng/location_update_time，供「附近的人」使用")
    @Parameters({
            @Parameter(name = "lat", description = "纬度（-90~90）", required = true, example = "31.2304"),
            @Parameter(name = "lng", description = "经度（-180~180）", required = true, example = "121.4737")
    })
    public CommonResult<Boolean> reportLocation(
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userService.reportLocation(userId, lat, lng);
        return success(true);
    }

    @DeleteMapping("/account")
    @Operation(summary = "注销账号", description = "当前用户主动注销：匿名化资料、封禁、强制下线所有终端（软注销，不物理删除）")
    public CommonResult<Boolean> cancelAccount() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userService.cancelAccount(userId);
        return success(true);
    }

    @PostMapping("/bind")
    @Operation(summary = "绑定/修改手机号或邮箱", description = "直接更新 im_users.mobile/email（本期不做验证码校验，后续接 SMS/邮件）")
    public CommonResult<Boolean> bind(@Valid @RequestBody AppImUserBindReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userService.bindMobileOrEmail(userId, reqVO);
        return success(true);
    }

    private static List<Long> parseUserIds(String userIds) {
        if (StrUtil.isBlank(userIds)) {
            return Collections.emptyList();
        }
        return Arrays.stream(userIds.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
