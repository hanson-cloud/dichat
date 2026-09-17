package com.diqin.cloud.module.im.dal.mysql.group;

import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 群 Mapper
 *
 * @author hanson
 */
@Mapper
public interface ImGroupMapper extends BaseMapperX<ImGroupDO> {

    default ImGroupDO selectByIdForUpdate(Long id) {
        return selectOne(new LambdaQueryWrapperX<ImGroupDO>()
                .eq(ImGroupDO::getId, id)
                .last("FOR UPDATE"));
    }

    default PageResult<ImGroupDO> selectPage(ImGroupManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImGroupDO>()
                .likeIfPresent(ImGroupDO::getName, reqVO.getName())
                .eqIfPresent(ImGroupDO::getOwnerUserId, reqVO.getOwnerUserId())
                .eqIfPresent(ImGroupDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ImGroupDO::getBanned, reqVO.getBanned())
                .betweenIfPresent(ImGroupDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImGroupDO::getId));
    }

    /**
     * 群聊广场：检索公开群（is_public=1 且未封禁、未解散）
     *
     * @param keyword   关键字（可选；匹配群名 / 话题，OR 关系）
     * @param category  分类（可选；精确匹配）
     * @param pageParam 分页参数
     * @return 分页结果
     */
    default PageResult<ImGroupDO> selectPublicGroups(String keyword, String category, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<ImGroupDO>()
                .eq(ImGroupDO::getIsPublic, true)
                .eq(ImGroupDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .eq(ImGroupDO::getBanned, false)
                .eqIfPresent(ImGroupDO::getCategory, category)
                // 关键字：name OR topic 模糊匹配；空值不拼条件
                .and(StrUtil.isNotBlank(keyword), w -> w
                        .like(ImGroupDO::getName, keyword.trim())
                        .or()
                        .like(ImGroupDO::getTopic, keyword.trim()))
                // 排序：成员数倒序优先（热门），其次 id 倒序（新发布优先）
                .orderByDesc(ImGroupDO::getMemberCount)
                .orderByDesc(ImGroupDO::getId));
    }

}
