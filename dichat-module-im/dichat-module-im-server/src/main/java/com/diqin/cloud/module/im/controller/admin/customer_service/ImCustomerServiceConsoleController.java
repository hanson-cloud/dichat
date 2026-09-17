package com.diqin.cloud.module.im.controller.admin.customer_service;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsConversationPageReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsConversationRespVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsMessageListReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsMessageSendReqVO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceConsoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - IM 客服工作台
 * <p>以当前登录管理员绑定的客服身份，提供会话列表、消息查询、代发消息与实时消息流（SSE）。
 * 所有接口均要求管理员已通过 {@code ImCustomerServiceManagerService.getCustomerServiceByAdminUserId} 绑定客服身份。</p>
 *
 * @author 速构构
 */
@Tag(name = "管理后台 - IM 客服工作台")
@RestController
@RequestMapping("/im/manager/customer-service/console")
@Validated
public class ImCustomerServiceConsoleController {

    @Resource
    private ImCustomerServiceConsoleService consoleService;

    @GetMapping("/conversation/page")
    @Operation(summary = "客服工作台 - 会话列表")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:query')")
    public CommonResult<PageResult<ImCsConversationRespVO>> getConversationPage(@Valid ImCsConversationPageReqVO reqVO) {
        return success(consoleService.getConversationPage(reqVO, getLoginUserId()));
    }

    @GetMapping("/message/list")
    @Operation(summary = "客服工作台 - 会话消息列表（游标翻页）")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:query')")
    public CommonResult<List<ImPrivateMessageDO>> getMessageList(@Valid ImCsMessageListReqVO reqVO) {
        return success(consoleService.getMessageList(reqVO, getLoginUserId()));
    }

    @PostMapping("/conversation/read")
    @Operation(summary = "客服工作台 - 标记会话已读（清零客服侧未读）")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:update')")
    public CommonResult<Boolean> markRead(@RequestParam("peerId") Long peerId) {
        consoleService.markRead(peerId, getLoginUserId());
        return success(true);
    }

    @PostMapping("/message/send")
    @Operation(summary = "客服工作台 - 以客服身份代发消息")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:update')")
    public CommonResult<ImPrivateMessageDO> sendMessage(@Valid @RequestBody ImCsMessageSendReqVO reqVO) {
        return success(consoleService.sendMessage(reqVO, getLoginUserId()));
    }

    @GetMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "客服工作台 - 实时消息流（SSE）")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:query')")
    public SseEmitter stream() {
        return consoleService.openStream(getLoginUserId());
    }

    @GetMapping("/status")
    @Operation(summary = "客服工作台 - 获取我的在线状态（0-离线 1-在线 2-忙碌）")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:query')")
    public CommonResult<Integer> getStatus() {
        return success(consoleService.getMyStatus(getLoginUserId()));
    }

    @PostMapping("/status")
    @Operation(summary = "客服工作台 - 手动切换在线状态（1-在线 2-忙碌 0-离线）")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:update')")
    public CommonResult<Boolean> setStatus(@RequestParam("status") Integer status) {
        consoleService.setStatus(getLoginUserId(), status);
        return success(true);
    }

    private Long getLoginUserId() {
        return SecurityFrameworkUtils.getLoginUserId();
    }

}
