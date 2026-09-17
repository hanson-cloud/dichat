package com.diqin.cloud.module.im.dal.mysql.moment;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImMomentMapper extends BaseMapperX<ImMomentDO> {

    default List<ImMomentDO> selectListByUserId(Long userId, Integer pageNo, Integer pageSize) {
        int offset = (pageNo - 1) * pageSize;
        // 按发布时间倒序（createTime 相同则按 id 兜底），与 /feed 的倒序语义保持一致
        return selectList(new LambdaQueryWrapperX<ImMomentDO>()
                .eq(ImMomentDO::getUserId, userId)
                .orderByDesc(ImMomentDO::getCreateTime)
                .orderByDesc(ImMomentDO::getId)
                .last("LIMIT " + pageSize + " OFFSET " + offset));
    }

    default List<ImMomentDO> selectFriendMoments(List<Long> friendUserIds, Long loginUserId, Integer pageNo, Integer pageSize) {
        int offset = (pageNo - 1) * pageSize;
        // 放开：①公开(0) ②自己发的全部（不限可见性） ③好友发的部分可见(2)/不给谁看(3) 交由 Java 层按白/黑名单精确过滤
        // 注意：好友发的"仅自己可见(1)"不放出，Java 层也会丢弃
        return selectList(new LambdaQueryWrapperX<ImMomentDO>()
                .in(ImMomentDO::getUserId, friendUserIds)
                .and(w -> w.eq(ImMomentDO::getVisibility, 0)
                        .or().eq(ImMomentDO::getUserId, loginUserId)
                        .or().eq(ImMomentDO::getVisibility, 2)
                        .or().eq(ImMomentDO::getVisibility, 3))
                .orderByDesc(ImMomentDO::getId)
                .last("LIMIT " + pageSize + " OFFSET " + offset));
    }

    default void delete(Long momentId, Long userId){
        this.delete( Wrappers.<ImMomentDO>lambdaQuery().eq(ImMomentDO::getId, momentId).eq(ImMomentDO::getUserId, userId));
    }
}
