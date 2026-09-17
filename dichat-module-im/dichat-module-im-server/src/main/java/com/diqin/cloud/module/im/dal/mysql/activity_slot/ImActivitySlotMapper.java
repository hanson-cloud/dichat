package com.diqin.cloud.module.im.dal.mysql.activity_slot;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.activity_slot.ImActivitySlotDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IM 运营活动位 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImActivitySlotMapper extends BaseMapperX<ImActivitySlotDO> {

    default PageResult<ImActivitySlotDO> selectPage(ImActivitySlotManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImActivitySlotDO>()
                .likeIfPresent(ImActivitySlotDO::getName, reqVO.getName())
                .eqIfPresent(ImActivitySlotDO::getSlotPosition, reqVO.getSlotPosition())
                .eqIfPresent(ImActivitySlotDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImActivitySlotDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImActivitySlotDO::getSort));
    }

    /**
     * 查询指定展示位当前生效的活动位：status=1 且 (startTime 为空或 <= now) 且 (endTime 为空或 >= now)，按 sort 升序。
     * 用于客户端展示，仅读取启用且在生效时间窗口内的活动。
     */
    default List<ImActivitySlotDO> selectActiveList(Integer slotPosition, LocalDateTime now) {
        return selectList(new LambdaQueryWrapperX<ImActivitySlotDO>()
                .eq(ImActivitySlotDO::getSlotPosition, slotPosition)
                .eq(ImActivitySlotDO::getStatus, 1)
                .and(w -> w.isNull(ImActivitySlotDO::getStartTime).or().le(ImActivitySlotDO::getStartTime, now))
                .and(w -> w.isNull(ImActivitySlotDO::getEndTime).or().ge(ImActivitySlotDO::getEndTime, now))
                .orderByAsc(ImActivitySlotDO::getSort));
    }

}
