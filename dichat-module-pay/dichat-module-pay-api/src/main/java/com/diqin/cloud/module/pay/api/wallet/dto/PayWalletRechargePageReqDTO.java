package com.diqin.cloud.module.pay.api.wallet.dto;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.util.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 钱包充值记录分页 Request DTO（RPC）
 *
 * @author dichat
 */
@Schema(description = "钱包充值记录分页 Request DTO")
@Data
public class PayWalletRechargePageReqDTO extends PageParam {

    @Schema(description = "是否已支付", example = "true")
    private Boolean payStatus;

    @Schema(description = "创建时间范围", example = "[\"2024-01-01 00:00:00\", \"2024-12-31 23:59:59\"]")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
