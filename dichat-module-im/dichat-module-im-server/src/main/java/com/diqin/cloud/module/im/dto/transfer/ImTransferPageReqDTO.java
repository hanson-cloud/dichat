package com.diqin.cloud.module.im.dto.transfer;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * APP - 转账记录分页 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 转账记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ImTransferPageReqDTO extends PageParam {

    @Schema(description = "方向：1-我转出的 2-我收到的；不传查全部", example = "1")
    private Integer direction;

    @Schema(description = "状态：0-待领取 1-已到账 2-已退款 3-已过期")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间范围", example = "[\"2024-01-01 00:00:00\", \"2024-12-31 23:59:59\"]")
    private LocalDateTime[] createTime;

}
