package com.diqin.cloud.module.im.controller.app.message;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.message.vo.channel.AppImChannelMessagePullRespVO;
import com.diqin.cloud.module.im.dal.dataobject.channel.ImChannelDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImChannelMessageDO;
import com.diqin.cloud.module.im.enums.message.ImMessageStatusEnum;
import com.diqin.cloud.module.im.service.channel.ImChannelService;
import com.diqin.cloud.module.im.service.message.ImChannelMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;
import static com.diqin.cloud.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - IM 频道消息")
@RestController
@RequestMapping("/im/channel/message")
@Validated
public class AppImChannelMessageController {

    @Resource
    private ImChannelMessageService channelMessageService;

    @Resource
    private ImChannelService channelService;

    @GetMapping("/pull")
    @Operation(summary = "拉取频道消息（离线增量）；按 minId 游标分页")
    public CommonResult<List<AppImChannelMessagePullRespVO>> pull(
            @RequestParam(value = "minId", defaultValue = "0") @PositiveOrZero(message = "minId 不能小于 0") Long minId,
            @RequestParam(value = "size", defaultValue = "100")
            @Min(value = 1, message = "size 必须大于 0")
            @Max(value = 200, message = "size 一次最多 200 条") Integer size) {
        // 1. 拉取消息列表
        Long userId = getLoginUserId();
        List<ImChannelMessageDO> list = channelMessageService.getMessageListForPull(userId, minId, size);
        if (CollUtil.isEmpty(list)) {
            return success(Collections.emptyList());
        }
        // 2. 按 Redis 已读游标补 status；device A 已读后 device B 拉到这条不再算入未读
        Map<Long, Long> readMaxByChannel = channelMessageService.getChannelReadMaxMessageIdMap(
                userId, convertSet(list, ImChannelMessageDO::getChannelId));
        // 3. 冗余频道名称/头像，便于 App 列表直接展示（与管理端同构）
        Map<Long, ImChannelDO> channelMap = channelService.getChannelMap(
                convertSet(list, ImChannelMessageDO::getChannelId));
        return success(BeanUtils.toBean(list, AppImChannelMessagePullRespVO.class, vo -> {
            Long readMax = readMaxByChannel.get(vo.getChannelId());
            vo.setStatus(readMax != null && readMax >= vo.getId()
                    ? ImMessageStatusEnum.READ.getStatus()
                    : ImMessageStatusEnum.UNREAD.getStatus());
            ImChannelDO channel = channelMap.get(vo.getChannelId());
            if (channel != null) {
                vo.setChannelName(channel.getName());
                vo.setChannelAvatar(channel.getAvatar());
            }
        }));
    }

    @PutMapping("/read")
    @Operation(summary = "标记频道消息已读")
    @Parameter(name = "channelId", description = "频道编号", required = true, example = "1")
    @Parameter(name = "messageId", description = "已读到的消息编号", required = true, example = "100")
    public CommonResult<Boolean> readChannelMessages(@RequestParam("channelId") Long channelId,
                                                     @RequestParam("messageId") Long messageId) {
        channelMessageService.readChannelMessages(getLoginUserId(), channelId, messageId);
        return success(true);
    }

}
