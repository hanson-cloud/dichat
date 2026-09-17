package com.diqin.cloud.module.im.controller.app.message;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchRespVO;
import com.diqin.cloud.module.im.dal.mysql.message.ImMessageSearchMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "用户APP - IM 消息搜索")
@RestController
@RequestMapping("/im/message/search")
@Validated
public class AppImMessageSearchController {

    @Resource
    private ImMessageSearchMapper messageSearchMapper;

    @GetMapping
    @Operation(summary = "搜索消息（跨私聊和群聊）")
    public CommonResult<PageResult<ImMessageSearchRespVO>> searchMessages(
            @RequestParam @NotEmpty String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Long senderId,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "1") Integer pageNo) {
        int offset = (pageNo - 1) * pageSize;
        List<ImMessageSearchRespVO> list = messageSearchMapper.searchMessages(
                keyword, type, senderId, null, null, offset, pageSize);
        long total = messageSearchMapper.countMessages(keyword, type, senderId, null, null);
        return success(new PageResult<>(list, total));
    }
}
