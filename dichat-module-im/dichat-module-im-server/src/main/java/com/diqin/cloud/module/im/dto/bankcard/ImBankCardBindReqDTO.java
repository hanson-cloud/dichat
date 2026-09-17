package com.diqin.cloud.module.im.dto.bankcard;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * APP - 绑定银行卡 Request VO
 * <p>
 * 卡号由 MyBatis 的 {@code EncryptTypeHandler} 自动 AES 加密落库，业务层不接触明文。
 *
 * @author dichat
 */
@Schema(description = "APP - 绑定银行卡 Request VO")
@Data
public class ImBankCardBindReqDTO {

    @Schema(description = "银行卡号（16-19 位数字）", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^\\d{16,19}$", message = "银行卡号格式不正确（16-19 位数字）")
    private String cardNo;

    @Schema(description = "银行名称")
    @Size(max = 64, message = "银行名称不能超过 64 字")
    private String bankName;

    @Schema(description = "银行编码")
    @Size(max = 32, message = "银行编码不能超过 32 字")
    private String bankCode;

    @Schema(description = "卡类型：1-借记卡 2-信用卡", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "卡类型不能为空")
    private Integer cardType;

    @Schema(description = "持卡人姓名")
    @Size(max = 64, message = "持卡人姓名不能超过 64 字")
    private String holderName;

    @Schema(description = "证件号（身份证），用于绑卡实名校验；落库前脱敏为后四位")
    @NotBlank(message = "证件号不能为空")
    private String idCard;

    @Schema(description = "预留手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "短信验证码（绑卡时校验）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "短信验证码不能为空")
    private String code;

    @Schema(description = "是否设为默认卡")
    private Boolean isDefault;

}
