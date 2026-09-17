package com.diqin.cloud.module.im.api.user;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.api.user.dto.ImUserRespDTO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.user.ImUserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * IM 模块对外 RPC Feign 实现
 *
 * <p>替代之前对 system {@code AdminUserApi} 的依赖；调用方（IM server 自身 / 其他模块）
 * 通过 {@code imUserApi.getUser / getUserList / getUserMap / getUserListByKeyword / validateUser / validateUserList}
 * 拿到 im_users 表的用户信息。
 *
 * @author hanson
 */
@RestController
@Validated
public class ImUserApiImpl implements ImUserApi {

    @Resource
    private ImUserService userService;

    @Override
    public CommonResult<ImUserRespDTO> getUser(Long id) {
        ImUserDO user = userService.getUser(id);
        if (user == null) {
            return CommonResult.success(null);
        }
        return CommonResult.success(toDTO(user));
    }

    @Override
    public CommonResult<List<ImUserRespDTO>> getUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return CommonResult.success(Collections.emptyList());
        }
        List<ImUserDO> users = userService.getUserList(ids);
        return CommonResult.success(BeanUtils.toBean(users, ImUserRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> validateUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return CommonResult.success(true);
        }
        userService.validateUserList(ids);
        return CommonResult.success(true);
    }

    /**
     * DO → DTO 转换：isBanned → isBanned（同名直接复制即可）
     */
    private ImUserRespDTO toDTO(ImUserDO user) {
        ImUserRespDTO dto = BeanUtils.toBean(user, ImUserRespDTO.class);
        if (dto != null) {
            dto.setIsBanned(Boolean.TRUE.equals(user.getIsBanned()));
            dto.setBanReason(user.getBanReason());
            dto.setLastLoginTime(user.getLoginDate());
            dto.setCreateTime(user.getCreateTime());
        }
        return dto;
    }
}
