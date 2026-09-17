package com.diqin.cloud.module.im.controller.admin.redpacket;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketGrabManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketRefundStatusRespVO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketDO;
import com.diqin.cloud.module.im.service.redpacket.ImRedPacketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 红包")
@RestController
@RequestMapping("/im/manager/red-packet")
@Validated
public class ImRedPacketManagerController {

    @Resource
    private ImRedPacketService redPacketService;

    @GetMapping("/page")
    @Operation(summary = "获得红包分页")
    @PreAuthorize("@ss.hasPermission('im:manager:red-packet:query')")
    public CommonResult<PageResult<ImRedPacketManagerRespVO>> getRedPacketPage(ImRedPacketManagerPageReqVO reqVO) {
        PageResult<ImRedPacketDO> page = redPacketService.getRedPacketPage(reqVO);
        return success(new PageResult<>(BeanUtils.toBean(page.getList(), ImRedPacketManagerRespVO.class), page.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获得红包详情（含领取明细）")
    @PreAuthorize("@ss.hasPermission('im:manager:red-packet:query')")
    public CommonResult<ImRedPacketManagerRespVO> getRedPacket(@RequestParam("id") Long id) {
        ImRedPacketDO redPacket = redPacketService.getRedPacket(id);
        ImRedPacketManagerRespVO resp = BeanUtils.toBean(redPacket, ImRedPacketManagerRespVO.class);
        resp.setGrabs(BeanUtils.toBean(redPacketService.getGrabList(id), ImRedPacketGrabManagerRespVO.class));
        return success(resp);
    }

    @PostMapping("/refund")
    @Operation(summary = "退款（仅已过期且未领完的红包）")
    @PreAuthorize("@ss.hasPermission('im:manager:red-packet:refund')")
    public CommonResult<Boolean> refundRedPacket(@RequestParam("id") Long id) {
        redPacketService.refundRedPacket(id);
        return success(true);
    }

    @GetMapping("/refund-status")
    @Operation(summary = "获得红包退款状态")
    @PreAuthorize("@ss.hasPermission('im:manager:red-packet:query')")
    public CommonResult<ImRedPacketRefundStatusRespVO> getRedPacketRefundStatus(@RequestParam("id") Long id) {
        return success(redPacketService.getRedPacketRefundStatus(id));
    }

}
