package com.diqin.cloud.module.im.service.blacklist;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerSaveReqVO;
import com.diqin.cloud.module.im.dal.dataobject.blacklist.ImBlacklistDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.blacklist.ImBlacklistMapper;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * 管理后台 - IM 全局黑名单 Service 实现类
 *
 * @author dichat
 */
@Service
public class ImBlacklistManagerServiceImpl implements ImBlacklistManagerService {

    @Resource
    private ImBlacklistMapper blacklistMapper;
    @Resource
    private ImUserService userService;

    @Override
    public PageResult<ImBlacklistManagerRespVO> getBlacklistManagerPage(ImBlacklistManagerPageReqVO pageReqVO) {
        PageResult<ImBlacklistDO> pageResult = blacklistMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 批量查询用户，回填操作人 / 被拉黑人昵称
        Set<Long> userIds = convertSet(pageResult.getList(), ImBlacklistDO::getUserId);
        userIds.addAll(convertSet(pageResult.getList(), ImBlacklistDO::getBlockId, Objects::nonNull));
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userService.getUserList(userIds), ImUserDO::getId);
        return BeanUtils.toBean(pageResult, ImBlacklistManagerRespVO.class, vo -> {
            MapUtils.findAndThen(userMap, vo.getUserId(), user -> vo.setUserNickname(user.getNickname()));
            MapUtils.findAndThen(userMap, vo.getBlockId(), user -> vo.setBlockNickname(user.getNickname()));
        });
    }

    @Override
    @LogRecord(type = IM_BLACKLIST_TYPE, subType = IM_BLACKLIST_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_BLACKLIST_CREATE_SUCCESS)
    public Long createBlacklist(ImBlacklistManagerSaveReqVO saveReqVO) {
        // 去重：同一 (操作人, 被拉黑人) 已存在活跃记录则拒绝
        Long count = blacklistMapper.selectCount(new LambdaQueryWrapperX<ImBlacklistDO>()
                .eq(ImBlacklistDO::getUserId, saveReqVO.getUserId())
                .eq(ImBlacklistDO::getBlockId, saveReqVO.getBlockId()));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("该黑名单记录已存在");
        }
        ImBlacklistDO blacklist = BeanUtils.toBean(saveReqVO, ImBlacklistDO.class);
        blacklistMapper.insert(blacklist);
        LogRecordContext.putVariable("reqVO", saveReqVO);
        return blacklist.getId();
    }

    @Override
    @LogRecord(type = IM_BLACKLIST_TYPE, subType = IM_BLACKLIST_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = IM_BLACKLIST_DELETE_SUCCESS)
    public void deleteBlacklist(Long id) {
        ImBlacklistDO blacklist = blacklistMapper.selectById(id);
        if (blacklist == null) {
            throw new IllegalArgumentException("黑名单记录不存在");
        }
        LogRecordContext.putVariable("blockId", blacklist.getBlockId());
        blacklistMapper.deleteById(id);
    }

}
