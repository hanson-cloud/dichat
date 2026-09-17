package com.diqin.cloud.module.im.controller.admin.user;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.user.vo.ImUserPageReqVO;
import com.diqin.cloud.module.im.controller.admin.user.vo.ImUserRespVO;
import com.diqin.cloud.module.im.controller.admin.user.vo.ImUserSimpleRespVO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.user.ImUserConvert;
import com.diqin.cloud.module.im.service.user.ImUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * IM 用户相关接口
 *
 * <p>用户信息源切换说明：数据源已从 {@code system_users} 切换到 IM 独立维护的 {@code im_users} 表
 * （{@code com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO}）。所有查询 / 更新走
 * 本地 {@link ImUserService}，不再依赖 {@code com.diqin.cloud.module.system.api.user.AdminUserApi}。
 *
 * <p>对外暴露：
 * <ul>
 *   <li>{@code GET /im/user/get?userId=} 透传</li>
 *   <li>{@code GET /im/user/list?userIds=} 透传</li>
 *   <li>{@code GET /im/user/search?name=} 关键词搜索（仅昵称 / 用户名，最多 20 条）</li>
 *   <li>{@code GET /im/user/online-terminals?userIds=} 在线终端查询</li>
 *   <li>管理后台：分页 / 创建 / 更新 / 删除 / 启停 / 封禁 / 重置密码</li>
 * </ul>
 */
@Tag(name = "管理后台 - IM 用户")
@RestController
@RequestMapping("/im/user")
@Validated
public class ImUserController {

    @Resource
    private ImUserService userService;

    @GetMapping("/page")
    @Operation(summary = "【管理后台】分页查询 IM 用户")
    @PreAuthorize("@ss.hasPermission('im:manager:user:query')")
    public CommonResult<PageResult<ImUserRespVO>> getUserPage(@Valid ImUserPageReqVO reqVO) {
        PageResult<ImUserDO> page = userService.getUserPage(reqVO);
        return success(new PageResult<>(ImUserConvert.toRespVOList(page.getList()), page.getTotal()));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "【管理后台】获得 IM 用户精简列表（id / 账号 / 昵称 / 头像）；表单选择关联用户时使用")
    public CommonResult<List<ImUserSimpleRespVO>> getSimpleUserList() {
        List<ImUserDO> list = userService.getSimpleUserList();
        return success(BeanUtils.toBean(list, ImUserSimpleRespVO.class));
    }

    @PutMapping("/ban")
    @Operation(summary = "【管理后台】封禁 / 解封 IM 用户")
    @PreAuthorize("@ss.hasPermission('im:manager:user:ban')")
    public CommonResult<Boolean> banUser(@RequestParam("id") Long id,
                                         @RequestParam("banned") Boolean banned,
                                         @RequestParam(value = "reason", required = false) String reason) {
        userService.updateBanned(id, banned, reason);
        return success(true);
    }

    @PutMapping("/status")
    @Operation(summary = "【管理后台】启停 IM 用户")
    @PreAuthorize("@ss.hasPermission('im:manager:user:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id,
                                              @RequestParam("status") Integer status) {
        userService.updateStatus(id, status);
        return success(true);
    }

    @PutMapping("/reset-password")
    @Operation(summary = "【管理后台】重置 IM 用户密码")
    @PreAuthorize("@ss.hasPermission('im:manager:user:reset-password')")
    public CommonResult<Boolean> resetPassword(@RequestParam("id") Long id,
                                               @RequestParam("newPassword") String newPassword) {
        userService.resetPassword(id, newPassword);
        return success(true);
    }
}
