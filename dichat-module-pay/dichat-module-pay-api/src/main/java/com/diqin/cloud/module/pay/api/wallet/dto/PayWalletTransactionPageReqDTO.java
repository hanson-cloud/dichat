package com.diqin.cloud.module.pay.api.wallet.dto;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.util.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 钱包余额流水分页 Request DTO（RPC）
 *
 * @author dichat
 */
@Schema(description = "钱包余额流水分页 Request DTO")
@Data
public class PayWalletTransactionPageReqDTO extends PageParam {

    @Schema(description = "类型：1-收入 2-支出", example = "1")
    private Integer type;

    @Schema(description = "创建时间范围", example = "[\"2024-01-01 00:00:00\", \"2024-12-31 23:59:59\"]")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
