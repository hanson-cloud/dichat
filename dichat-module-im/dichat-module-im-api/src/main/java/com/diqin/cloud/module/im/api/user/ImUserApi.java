package com.diqin.cloud.module.im.api.user;

import cn.hutool.core.convert.Convert;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.module.im.api.enums.ApiConstants;
import com.diqin.cloud.module.im.api.user.dto.ImUserRespDTO;
import feign.FeignIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * RPC 服务 - IM 用户
 *
 * <p>对外暴露给其它模块（如 dichat-module-im-server 自身 / dichat-server 启动器），
 * 取代之前对 system {@code AdminUserApi} 的依赖。
 *
 * @author hanson
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - IM 用户")
public interface ImUserApi {

    String PREFIX = ApiConstants.PREFIX + "/user";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过用户 ID 查询 IM 用户")
    @Parameter(name = "id", description = "用户编号", example = "1", required = true)
    CommonResult<ImUserRespDTO> getUser(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "通过用户 ID 集合查询用户们")
    @Parameter(name = "ids", description = "用户编号集合", example = "1,2", required = true)
    CommonResult<List<ImUserRespDTO>> getUserList(@RequestParam("ids") Collection<Long> ids);

    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验用户们是否有效")
    @Parameter(name = "ids", description = "用户编号集合", example = "3,5", required = true)
    CommonResult<Boolean> validateUserList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 获得用户 Map
     */
    default Map<Long, ImUserRespDTO> getUserMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ImUserRespDTO> users = getUserList(ids).getCheckedData();
        return CollectionUtils.convertMap(users, ImUserRespDTO::getId);
    }

    /**
     * 校验用户是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用 / 封禁
     */
    default void validateUser(Long id) {
        validateUserList(Collections.singleton(id)).checkError();
    }

    /**
     * FeignIgnore: 提供给 easy-trans 的 selectById 调用入口（与 AdminUserApi 对齐）
     */
    @FeignIgnore
    default ImUserRespDTO selectById(Object id) {
        return getUser(Convert.toLong(id)).getCheckedData();
    }

    @FeignIgnore
    default List<ImUserRespDTO> selectByIds(List<?> ids) {
        return getUserList(Convert.toList(Long.class, ids)).getCheckedData();
    }

}
