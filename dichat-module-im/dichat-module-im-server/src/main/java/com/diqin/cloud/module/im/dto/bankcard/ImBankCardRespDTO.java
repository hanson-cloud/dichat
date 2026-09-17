package com.diqin.cloud.module.im.dto.bankcard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * APP - 银行卡 Response VO
 * <p>
 * 卡号在转换层脱敏，仅返回掩码与前 4 位尾号，绝不暴露完整卡号。
 *
 * @author dichat
 */
@Schema(description = "APP - 银行卡 Response VO")
@Data
public class ImBankCardRespDTO {

    @Schema(description = "银行卡编号")
    private Long id;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "卡类型：1-借记卡 2-信用卡")
    private Integer cardType;

    @Schema(description = "持卡人姓名")
    private String holderName;

    @Schema(description = "预留手机号（脱敏）")
    private String phone;

    @Schema(description = "是否默认卡")
    private Boolean isDefault;

    @Schema(description = "状态：0-正常 1-已解绑")
    private Integer status;

    @Schema(description = "卡号掩码，如 **** **** **** 1234")
    private String cardNoMasked;

    @Schema(description = "卡号后 4 位")
    private String cardTail;

    @Schema(description = "绑定时间")
    private LocalDateTime createTime;

}
