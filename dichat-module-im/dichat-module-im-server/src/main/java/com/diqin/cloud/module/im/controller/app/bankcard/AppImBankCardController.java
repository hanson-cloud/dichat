package com.diqin.cloud.module.im.controller.app.bankcard;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.dto.bankcard.ImBankCardBindReqDTO;
import com.diqin.cloud.module.im.dto.bankcard.ImBankCardRespDTO;
import com.diqin.cloud.module.im.dto.bankcard.ImBankCardSetDefaultReqDTO;
import com.diqin.cloud.module.im.service.bankcard.ImBankCardService;
import com.diqin.cloud.module.system.api.sms.SmsCodeApi;
import com.diqin.cloud.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.diqin.cloud.module.system.enums.sms.SmsSceneEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.servlet.ServletUtils.getClientIP;

/**
 * 用户 APP - IM 银行卡
 * <p>
 * 卡号全程加密（{@code EncryptTypeHandler} / AES），对外仅返回脱敏掩码。
 *
 * @author dichat
 */
@Tag(name = "用户 APP - IM 银行卡")
@RestController
@RequestMapping("/im/bank-card")
@Validated
public class AppImBankCardController {

    @Resource
    private ImBankCardService bankCardService;

    @Resource
    private SmsCodeApi smsCodeApi;

    @PostMapping("/bind")
    @Operation(summary = "绑定银行卡（卡号自动 AES 加密落库）")
    public CommonResult<ImBankCardRespDTO> bind(@Valid @RequestBody ImBankCardBindReqDTO reqDTO) {
        return success(BeanUtils.toBean(bankCardService.bind(SecurityFrameworkUtils.getLoginUserId(), reqDTO),
                ImBankCardRespDTO.class));
    }

    @PostMapping("/send-sms")
    @Operation(summary = "发送绑卡短信验证码")
    public CommonResult<Boolean> sendSms(@RequestParam("phone") @NotEmpty(message = "手机号不能为空")
                                         @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        smsCodeApi.sendSmsCode(new SmsCodeSendReqDTO()
                .setMobile(phone)
                .setScene(SmsSceneEnum.MEMBER_BIND_BANK_CARD.getScene())
                .setCreateIp(getClientIP())).checkError();
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "我的银行卡列表（卡号脱敏）")
    public CommonResult<List<ImBankCardRespDTO>> list() {
        return success(BeanUtils.toBean(bankCardService.getMyBankCards(SecurityFrameworkUtils.getLoginUserId()),
                ImBankCardRespDTO.class));
    }

    @PostMapping("/set-default")
    @Operation(summary = "设为默认卡（保证仅一张默认）")
    public CommonResult<Boolean> setDefault(@Valid @RequestBody ImBankCardSetDefaultReqDTO reqDTO) {
        bankCardService.setDefault(SecurityFrameworkUtils.getLoginUserId(), reqDTO.getId());
        return success(true);
    }

    @PostMapping("/unbind")
    @Operation(summary = "解绑银行卡（软删除，保留对账）")
    public CommonResult<Boolean> unbind(@RequestParam("id") Long id) {
        bankCardService.deleteBankCard(SecurityFrameworkUtils.getLoginUserId(), id);
        return success(true);
    }

}
