package com.diqin.cloud.module.product.api.comment;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.product.api.comment.dto.ProductCommentCreateReqDTO;
import com.diqin.cloud.module.product.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 产品评论")
public interface ProductCommentApi {

    String PREFIX = ApiConstants.PREFIX + "/comment";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建评论")
    CommonResult<Long> createComment(@RequestBody @Valid ProductCommentCreateReqDTO createReqDTO);

}
