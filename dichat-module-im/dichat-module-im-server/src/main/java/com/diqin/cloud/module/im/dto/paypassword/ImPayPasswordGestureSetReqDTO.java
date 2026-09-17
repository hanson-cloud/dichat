package com.diqin.cloud.module.im.dto.paypassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * APP - 设置手势密码 Request VO
 * <p>
 * patternHash = SHA-256(点序，如 "0,1,2,4,5,8")，服务端仅存哈希，永不存明文轨迹。
 *
 * @author dichat
 */
@Schema(description = "APP - 设置手势密码 Request VO")
@Data
public class ImPayPasswordGestureSetReqDTO {

    @Schema(description = "手势图案 SHA-256 哈希", example = "a1b2c3...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手势图案哈希不能为空")
    @Size(max = 128, message = "手势图案哈希长度异常")
    private String patternHash;

}
