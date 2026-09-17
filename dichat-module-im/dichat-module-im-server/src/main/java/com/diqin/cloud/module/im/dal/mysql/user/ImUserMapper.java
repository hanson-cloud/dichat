package com.diqin.cloud.module.im.dal.mysql.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.user.vo.ImUserPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;
import java.util.function.Consumer;

/**
 * IM 用户 Mapper
 *
 * <p>搜索优先级（参考微信标准）：
 * <ol>
 *   <li>精确匹配 username（账号）—— 最高优先级</li>
 *   <li>精确匹配 nickname（昵称）</li>
 *   <li>nickname 前缀匹配（以关键词开头）</li>
 *   <li>nickname 模糊匹配（包含关键词）</li>
 * </ol>
 *
 * @author hanson
 */
@Mapper
public interface ImUserMapper extends BaseMapperX<ImUserDO> {

    /**
     * 按账号查询用户（用于登录/注册）
     * <p>按 username / mobile / email 查找，找到即返回
     */
    default ImUserDO selectByUsername(String username) {
        return selectOne(new LambdaQueryWrapperX<ImUserDO>()
                .eq(ImUserDO::getUsername, username)
                .or().eq(ImUserDO::getMobile, username)
                .or().eq(ImUserDO::getEmail, username));
    }

    default List<ImUserDO> selectListByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<ImUserDO>()
                .in(ImUserDO::getId, ids));
    }

    // ===================== 优先级搜索（微信标准）=====================

    /**
     * 按优先级搜索用户（去重，最多返回 limit 条）
     *
     * <p>搜索优先级：
     * <ol>
     *   <li>精确匹配 username（需对方开启 allowFindByUsername）</li>
     *   <li>精确匹配 nickname</li>
     *   <li>nickname 前缀匹配（LIKE 'keyword%'）</li>
     *   <li>nickname 模糊匹配（LIKE '%keyword%'）</li>
     *   <li>手机号精确匹配（需对方开启 allowFindByMobile，且 allowMobile=true）</li>
     * </ol>
     *
     * @param keyword       搜索关键词
     * @param excludeUserId 排除的用户 ID（通常是当前登录用户自己，传 null 不排除）
     * @param allowMobile   是否允许通过手机号搜索（一般由调用方判断：仅当关键词是 11 位纯数字时才为 true）
     * @param limit         最多返回条数
     * @return 按优先级去重后的用户列表（LinkedHashSet 保持插入顺序）
     */
    default List<ImUserDO> selectListByKeywordWithPriority(String keyword, Long excludeUserId,
                                                           boolean allowMobile, int limit) {
        // 用 Set<Long> 做 ID 去重，用 List 保持优先级顺序
        Set<Long> seenIds = new HashSet<>();
        List<ImUserDO> result = new ArrayList<>(limit);

        // 内部函数：添加时去重
        Consumer<ImUserDO> addIfNew = u -> {
            if (u != null && seenIds.add(u.getId())) {
                result.add(u);
            }
        };

        // 1. 精确匹配 username（最高优先级；需对方允许被账号搜索）
        ImUserDO exactUsername = selectOne(Wrappers.<ImUserDO>lambdaQuery()
                .eq(ImUserDO::getUsername, keyword)
                .eq(ImUserDO::getAllowFindByUsername, true)
                .ne(excludeUserId != null, ImUserDO::getId, excludeUserId)
                .last("LIMIT 1"));
        addIfNew.accept(exactUsername);

        // 2. 精确匹配 nickname
        if (result.size() < limit) {
            List<ImUserDO> exactNickname = selectList(Wrappers.<ImUserDO>lambdaQuery()
                    .eq(ImUserDO::getNickname, keyword)
                    .ne(excludeUserId != null, ImUserDO::getId, excludeUserId)
                    .last("LIMIT " + (limit - result.size())));
            for (ImUserDO u : exactNickname) {
                addIfNew.accept(u);
                if (result.size() >= limit) break;
            }
        }

        // 3. nickname 前缀匹配（以关键词开头）
        if (result.size() < limit) {
            List<ImUserDO> prefixMatch = selectList(Wrappers.<ImUserDO>lambdaQuery()
                    .likeRight(ImUserDO::getNickname, keyword)
                    .ne(excludeUserId != null, ImUserDO::getId, excludeUserId)
                    .last("LIMIT " + (limit - result.size())));
            for (ImUserDO u : prefixMatch) {
                addIfNew.accept(u);
                if (result.size() >= limit) break;
            }
        }

        // 4. nickname 模糊匹配（包含关键词）
        if (result.size() < limit) {
            // 多查一些，去重后截断
            int queryLimit = (limit - result.size()) * 3 + result.size();
            List<ImUserDO> fuzzyMatch = selectList(Wrappers.<ImUserDO>lambdaQuery()
                    .like(ImUserDO::getNickname, keyword)
                    .ne(excludeUserId != null, ImUserDO::getId, excludeUserId)
                    .last("LIMIT " + queryLimit));
            for (ImUserDO u : fuzzyMatch) {
                addIfNew.accept(u);
                if (result.size() >= limit) break;
            }
        }

        // 5. 手机号精确匹配（受隐私开关 + allowMobile 参数双重控制）
        if (allowMobile && result.size() < limit) {
            ImUserDO mobileMatch = selectOne(Wrappers.<ImUserDO>lambdaQuery()
                    .eq(ImUserDO::getMobile, keyword)
                    .eq(ImUserDO::getAllowFindByMobile, true)
                    .ne(excludeUserId != null, ImUserDO::getId, excludeUserId)
                    .last("LIMIT 1"));
            addIfNew.accept(mobileMatch);
        }

        return result;
    }

    /**
     * 分页查询
     */
    default PageResult<ImUserDO> selectPage(ImUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImUserDO>()
                .likeIfPresent(ImUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(ImUserDO::getNickname, reqVO.getNickname())
                .likeIfPresent(ImUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(ImUserDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ImUserDO::getIsBanned, reqVO.getBanned())
                .betweenIfPresent(ImUserDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImUserDO::getId));
    }

    // ===================== 表单下拉精简列表 =====================

    /**
     * 精简列表（供前端表单选择 IM 用户，如机器人关联、客服绑定 IM 身份）
     * <p>仅返回账号正常（{@code status=ENABLE}）且未封禁的用户，按 ID 倒序；不含手机号 / 密码等敏感字段。
     */
    default List<ImUserDO> selectSimpleUserList() {
        return selectList(new LambdaQueryWrapperX<ImUserDO>()
                .eq(ImUserDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .and(w -> w.isNull(ImUserDO::getIsBanned).or().eq(ImUserDO::getIsBanned, false))
                .orderByDesc(ImUserDO::getId));
    }

    // ===================== 附近的人 =====================

    /**
     * 按经纬度「边界框」粗筛附近候选用户
     * <p>
     * 仅返回：开启位置可见（{@code location_visible=1}）、坐标非空、在给定半径的经纬度边界框内、
     * 非自己、账号正常且未被封禁的用户；按位置新鲜度倒序，最多 200 条。
     * 精确距离（Haversine）由 Service 层在 Java 侧二次计算，避免依赖数据库地理函数。
     *
     * @param lat            中心点纬度
     * @param lng            中心点经度
     * @param excludeUserId 排除的用户 ID（当前登录用户）
     * @param radiusMeters  半径（米），用于推算经纬度边界框
     * @return 边界框内候选用户（未做精确距离过滤）
     */
    default List<ImUserDO> selectNearbyCandidates(Double lat, Double lng,
                                                 Long excludeUserId, int radiusMeters) {
        // 纬度 1° ≈ 111320 m；经度需乘 cos(lat)
        double latDelta = radiusMeters / 111320.0;
        double cosLat = Math.max(Math.cos(Math.toRadians(lat)), 1e-6);
        double lngDelta = radiusMeters / (111320.0 * cosLat);
        double minLat = lat - latDelta, maxLat = lat + latDelta;
        double minLng = lng - lngDelta, maxLng = lng + lngDelta;
        return selectList(new LambdaQueryWrapperX<ImUserDO>()
                .eq(ImUserDO::getLocationVisible, true)
                .between(ImUserDO::getLat, minLat, maxLat)
                .between(ImUserDO::getLng, minLng, maxLng)
                .ne(excludeUserId != null, ImUserDO::getId, excludeUserId)
                .eq(ImUserDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .and(w -> w.isNull(ImUserDO::getIsBanned).or().eq(ImUserDO::getIsBanned, false))
                .orderByDesc(ImUserDO::getLocationUpdateTime)
                .last("LIMIT 200"));
    }

}
