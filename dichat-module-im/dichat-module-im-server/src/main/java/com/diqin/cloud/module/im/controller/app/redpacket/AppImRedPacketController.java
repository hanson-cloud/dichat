package com.diqin.cloud.module.im.controller.app.redpacket;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.dto.redpacket.ImRedPacketGrabReqDTO;
import com.diqin.cloud.module.im.dto.redpacket.ImRedPacketGrabRespDTO;
import com.diqin.cloud.module.im.dto.redpacket.ImRedPacketRespDTO;
import com.diqin.cloud.module.im.dto.redpacket.ImRedPacketSendReqDTO;
import com.diqin.cloud.module.im.service.redpacket.ImRedPacketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - IM 红包
 * <p>
 * 余额变动委托 {@link com.diqin.cloud.module.pay.api.wallet.PayWalletApi}。
 *
 * @author dichat
 */
@Tag(name = "用户 APP - IM 红包")
@RestController
@RequestMapping("/im/red-packet")
@Validated
public class AppImRedPacketController {

    @Resource
    private ImRedPacketService redPacketService;

    @PostMapping("/send")
    @Operation(summary = "发红包（冻结发送方钱包余额）")
    public CommonResult<ImRedPacketRespDTO> send(@Valid @RequestBody ImRedPacketSendReqDTO reqDTO) {
        return success(BeanUtils.toBean(redPacketService.send(SecurityFrameworkUtils.getLoginUserId(), reqDTO),
                ImRedPacketRespDTO.class));
    }

    @PostMapping("/grab")
    @Operation(summary = "抢红包（红包级锁串行 + 乐观行锁 claim 一份明细并入账）")
    public CommonResult<ImRedPacketGrabRespDTO> grab(@Valid @RequestBody ImRedPacketGrabReqDTO reqDTO) {
        return success(BeanUtils.toBean(redPacketService.grabRedPacket(SecurityFrameworkUtils.getLoginUserId(),
                reqDTO.getRedPacketNo()), ImRedPacketGrabRespDTO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "我发出的红包分页")
    public CommonResult<PageResult<ImRedPacketRespDTO>> page(PageParam pageReqVO) {
        return success(BeanUtils.toBean(redPacketService.getRedPacketPageMySent(SecurityFrameworkUtils.getLoginUserId(),
                pageReqVO), ImRedPacketRespDTO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "红包详情（按流水号查询）")
    public CommonResult<ImRedPacketRespDTO> get(@RequestParam("no") String no) {
        return success(BeanUtils.toBean(redPacketService.getRedPacketByNo(no), ImRedPacketRespDTO.class));
    }

    @GetMapping("/my-grab")
    @Operation(summary = "查询当前用户对某红包的领取记录")
    public CommonResult<ImRedPacketGrabRespDTO> myGrab(@RequestParam("no") String no) {
        return success(BeanUtils.toBean(redPacketService.getMyGrab(no, SecurityFrameworkUtils.getLoginUserId()),
                ImRedPacketGrabRespDTO.class));
    }

    @GetMapping("/page-grabbed")
    @Operation(summary = "我抢到的红包分页")
    public CommonResult<PageResult<ImRedPacketRespDTO>> pageGrabbed(PageParam pageReqVO) {
        return success(BeanUtils.toBean(redPacketService.getRedPacketPageMyGrabbed(
                SecurityFrameworkUtils.getLoginUserId(), pageReqVO), ImRedPacketRespDTO.class));
    }

}
