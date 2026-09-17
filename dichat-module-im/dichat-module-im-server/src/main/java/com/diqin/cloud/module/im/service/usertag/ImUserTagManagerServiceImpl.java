package com.diqin.cloud.module.im.service.usertag;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.usertag.ImUserTagDO;
import com.diqin.cloud.module.im.dal.mysql.usertag.ImUserTagMapper;
import com.diqin.cloud.module.im.dal.mysql.usertag.ImUserTagRelationMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.USER_TAG_NAME_DUPLICATE;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.USER_TAG_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 用户标签管理 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImUserTagManagerServiceImpl implements ImUserTagManagerService {

    @Resource
    private ImUserTagMapper tagMapper;
    @Resource
    private ImUserTagRelationMapper relationMapper;

    @Override
    public PageResult<ImUserTagManagerRespVO> getTagManagerPage(ImUserTagManagerPageReqVO pageReqVO) {
        PageResult<ImUserTagDO> pageResult = tagMapper.selectPage(pageReqVO);
        // 批量统计每个标签的打标用户数
        Map<Long, Long> countMap = relationMapper.countGroupByTagId(
                CollectionUtils.convertList(pageResult.getList(), ImUserTagDO::getId));
        return BeanUtils.toBean(pageResult, ImUserTagManagerRespVO.class, vo ->
                vo.setTaggedUserCount(countMap.getOrDefault(vo.getId(), 0L)));
    }

    @Override
    public ImUserTagManagerRespVO getTag(Long id) {
        ImUserTagDO tag = tagMapper.selectById(id);
        if (tag == null) {
            return null;
        }
        Long count = relationMapper.countGroupByTagId(List.of(id)).getOrDefault(id, 0L);
        ImUserTagManagerRespVO respVO = BeanUtils.toBean(tag, ImUserTagManagerRespVO.class);
        respVO.setTaggedUserCount(count);
        return respVO;
    }

    @Override
    @LogRecord(type = IM_USER_TAG_TYPE, subType = IM_USER_TAG_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_USER_TAG_CREATE_SUCCESS)
    public Long createTag(ImUserTagManagerSaveReqVO createReqVO) {
        // 校验标签名称唯一
        validateTagNameUnique(null, createReqVO.getName());
        ImUserTagDO tag = BeanUtils.toBean(createReqVO, ImUserTagDO.class);
        tagMapper.insert(tag);
        LogRecordContext.putVariable("tag", tag);
        return tag.getId();
    }

    @Override
    @LogRecord(type = IM_USER_TAG_TYPE, subType = IM_USER_TAG_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = IM_USER_TAG_UPDATE_SUCCESS)
    public void updateTag(ImUserTagManagerUpdateReqVO updateReqVO) {
        // 校验存在
        ImUserTagDO exists = tagMapper.selectById(updateReqVO.getId());
        if (exists == null) {
            throw ServiceExceptionUtil.exception(USER_TAG_NOT_EXISTS);
        }
        // 校验标签名称唯一（排除自身）
        validateTagNameUnique(updateReqVO.getId(), updateReqVO.getName());
        ImUserTagDO update = BeanUtils.toBean(updateReqVO, ImUserTagDO.class);
        LogRecordContext.putVariable("updateReqVO", updateReqVO);
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, exists);
        tagMapper.updateById(update);
    }

    @Override
    @LogRecord(type = IM_USER_TAG_TYPE, subType = IM_USER_TAG_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = IM_USER_TAG_DELETE_SUCCESS)
    public void deleteTag(Long id) {
        // 校验存在
        ImUserTagDO tag = tagMapper.selectById(id);
        if (tag == null) {
            throw ServiceExceptionUtil.exception(USER_TAG_NOT_EXISTS);
        }
        LogRecordContext.putVariable("tag", tag);
        // 级联删除关联关系
        relationMapper.deleteByTagId(id);
        tagMapper.deleteById(id);
    }

    private void validateTagNameUnique(Long id, String name) {
        ImUserTagDO exist = tagMapper.selectByName(name);
        if (exist != null && (!exist.getId().equals(id))) {
            throw ServiceExceptionUtil.exception(USER_TAG_NAME_DUPLICATE, name);
        }
    }

}
