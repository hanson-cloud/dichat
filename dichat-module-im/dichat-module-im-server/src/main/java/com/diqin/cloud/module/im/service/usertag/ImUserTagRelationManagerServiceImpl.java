package com.diqin.cloud.module.im.service.usertag;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerSaveReqVO;
import com.diqin.cloud.module.im.dal.dataobject.usertag.ImUserTagDO;
import com.diqin.cloud.module.im.dal.dataobject.usertag.ImUserTagRelationDO;
import com.diqin.cloud.module.im.dal.mysql.usertag.ImUserTagMapper;
import com.diqin.cloud.module.im.dal.mysql.usertag.ImUserTagRelationMapper;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.USER_TAG_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 用户标签关联关系 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImUserTagRelationManagerServiceImpl implements ImUserTagRelationManagerService {

    @Resource
    private ImUserTagRelationMapper relationMapper;
    @Resource
    private ImUserTagMapper tagMapper;
    @Resource
    private ImUserService userService;

    @Override
    public PageResult<ImUserTagRelationManagerRespVO> getRelationManagerPage(ImUserTagRelationManagerPageReqVO pageReqVO) {
        PageResult<ImUserTagRelationDO> pageResult = relationMapper.selectPage(pageReqVO);
        // 回填用户昵称
        Map<Long, String> userNicknameMap = userService.getUserMap(
                CollectionUtils.convertList(pageResult.getList(), ImUserTagRelationDO::getUserId))
                .entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey, e -> e.getValue().getNickname()));
        // 回填标签名称
        Map<Long, String> tagNameMap = CollectionUtils.convertMap(
                tagMapper.selectByIds(CollectionUtils.convertList(pageResult.getList(), ImUserTagRelationDO::getTagId)),
                ImUserTagDO::getId, ImUserTagDO::getName);
        return BeanUtils.toBean(pageResult, ImUserTagRelationManagerRespVO.class, vo -> {
            vo.setUserNickname(userNicknameMap.get(vo.getUserId()));
            vo.setTagName(tagNameMap.get(vo.getTagId()));
        });
    }

    @Override
    @LogRecord(type = IM_USER_TAG_TYPE, subType = IM_USER_TAG_ASSIGN_SUB_TYPE, bizNo = "{{#createReqVO.tagId}}", success = IM_USER_TAG_ASSIGN_SUCCESS)
    public void createRelation(ImUserTagRelationManagerSaveReqVO createReqVO) {
        // 校验标签存在
        if (tagMapper.selectById(createReqVO.getTagId()) == null) {
            throw ServiceExceptionUtil.exception(USER_TAG_NOT_EXISTS);
        }
        // 去重：仅插入尚未关联的用户
        List<ImUserTagRelationDO> toInsert = createReqVO.getUserIds().stream()
                .filter(userId -> relationMapper.selectByUserAndTag(userId, createReqVO.getTagId()) == null)
                .map(userId -> new ImUserTagRelationDO().setUserId(userId).setTagId(createReqVO.getTagId()))
                .toList();
        if (!toInsert.isEmpty()) {
            relationMapper.insertBatch(toInsert);
        }
        LogRecordContext.putVariable("reqVO", createReqVO);
    }

    @Override
    @LogRecord(type = IM_USER_TAG_TYPE, subType = IM_USER_TAG_UNASSIGN_SUB_TYPE, bizNo = "{{#tagId}}-{{#userId}}", success = IM_USER_TAG_UNASSIGN_SUCCESS)
    public void deleteRelation(Long tagId, Long userId) {
        relationMapper.delete(new LambdaQueryWrapperX<ImUserTagRelationDO>()
                .eq(ImUserTagRelationDO::getTagId, tagId)
                .eq(ImUserTagRelationDO::getUserId, userId));
    }

}
