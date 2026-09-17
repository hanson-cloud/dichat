package com.diqin.cloud.module.im.service.message;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchReqVO;
import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchRespVO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.message.ImMessageSearchMapper;
import com.diqin.cloud.module.im.service.group.ImGroupService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class ImMessageSearchServiceImpl implements ImMessageSearchService {

    @Resource
    private ImMessageSearchMapper messageSearchMapper;
    @Resource
    private ImUserService userService;
    @Resource
    private ImGroupService groupService;

    @Override
    public PageResult<ImMessageSearchRespVO> searchMessages(ImMessageSearchReqVO reqVO) {
        long offset = (long) (reqVO.getPageNo() - 1) * reqVO.getPageSize();
        List<ImMessageSearchRespVO> list = messageSearchMapper.searchMessages(
                reqVO.getKeyword(), reqVO.getMsgType(), reqVO.getSenderId(),
                reqVO.getBeginTime(), reqVO.getEndTime(),
                offset, reqVO.getPageSize());
        if (CollUtil.isEmpty(list)) {
            return PageResult.empty();
        }

        // 回填目标方名称
        fillTargetNames(list);

        // 获取总数
        long total = messageSearchMapper.countMessages(
                reqVO.getKeyword(), reqVO.getMsgType(), reqVO.getSenderId(),
                reqVO.getBeginTime(), reqVO.getEndTime());

        return new PageResult<>(list, total);
    }

    private void fillTargetNames(List<ImMessageSearchRespVO> list) {
        // 收集私聊接收人ID
        Set<Long> receiverIds = list.stream()
                .filter(r -> r.getChatType() == 1)
                .map(ImMessageSearchRespVO::getTargetId)
                .collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(receiverIds)) {
            Map<Long, String> nicknames = CollectionUtils.convertMap(
                    userService.getUserList(receiverIds), ImUserDO::getId, ImUserDO::getNickname);
            list.stream().filter(r -> r.getChatType() == 1)
                    .forEach(r -> r.setTargetName(
                            nicknames.getOrDefault(r.getTargetId(), "用户" + r.getTargetId())));
        }

        // 收集群ID
        Set<Long> groupIds = list.stream()
                .filter(r -> r.getChatType() == 2)
                .map(ImMessageSearchRespVO::getTargetId)
                .collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(groupIds)) {
            Map<Long, String> groupNames = groupIds.stream()
                    .map(id -> groupService.getGroup(id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            ImGroupDO::getId, ImGroupDO::getName));
            list.stream().filter(r -> r.getChatType() == 2)
                    .forEach(r -> r.setTargetName(
                            groupNames.getOrDefault(r.getTargetId(), "群" + r.getTargetId())));
        }
    }
}
