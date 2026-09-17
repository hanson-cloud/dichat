package com.diqin.cloud.module.im.service.user_ban;

import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.user_ban.vo.*;
import com.diqin.cloud.module.im.dal.dataobject.user_ban.ImUserBanDO;
import com.diqin.cloud.module.im.dal.mysql.user_ban.ImUserBanMapper;
import com.diqin.cloud.module.im.enums.ForceOfflineReason;
import com.diqin.cloud.module.im.service.loginlog.ImUserLoginLogManagerService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.BAN_RECORD_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

@Service
@Validated
public class ImUserBanManagerServiceImpl implements ImUserBanManagerService {

    /** 处罚类型：1-全局禁言 2-封号 */
    private static final Integer BAN_TYPE_MUTE = 1;
    private static final Integer BAN_TYPE_ACCOUNT = 2;

    @Resource
    private ImUserBanMapper userBanMapper;

    @Resource
    private ImUserService userService;

    @Resource
    private ImUserLoginLogManagerService loginLogManagerService;

    @Override
    public PageResult<ImUserBanManagerRespVO> getUserBanPage(ImUserBanManagerPageReqVO pageReqVO) {
        return BeanUtils.toBean(userBanMapper.selectPage(pageReqVO), ImUserBanManagerRespVO.class);
    }

    @Override
    public ImUserBanManagerRespVO getUserBan(Long id) {
        return BeanUtils.toBean(userBanMapper.selectById(id), ImUserBanManagerRespVO.class);
    }

    @Override
    @LogRecord(type = IM_USER_BAN_TYPE, subType = IM_USER_BAN_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_USER_BAN_CREATE_SUCCESS)
    public Long createUserBan(ImUserBanManagerSaveReqVO createReqVO, Long bannedBy) {
        ImUserBanDO ban = BeanUtils.toBean(createReqVO, ImUserBanDO.class);
        ban.setBannedBy(bannedBy);
        ban.setBanStartTime(LocalDateTime.now());
        ban.setStatus(0); // 生效中
        userBanMapper.insert(ban);
        LogRecordContext.putVariable("ban", ban);
        // 封号（banType=2）：账号状态置为禁用 + 标记封禁 + 全端强踢。
        // 禁用账号后，登录网关（ImAuthServiceImpl.login0 校验 status==DISABLE）会拦截该用户，
        // 阻断封号期间的二次登录。
        if (BAN_TYPE_ACCOUNT.equals(ban.getBanType())) {
            userService.updateStatus(ban.getUserId(), CommonStatusEnum.DISABLE.getStatus());
            userService.updateBanned(ban.getUserId(), true, ban.getReason());
            loginLogManagerService.forceOfflineByUserId(ban.getUserId(), ForceOfflineReason.BANNED, ban.getReason());
        }
        return ban.getId();
    }

    @Override
    @LogRecord(type = IM_USER_BAN_TYPE, subType = IM_USER_BAN_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = IM_USER_BAN_UPDATE_SUCCESS)
    public void updateUserBan(ImUserBanManagerUpdateReqVO updateReqVO) {
        if (userBanMapper.selectById(updateReqVO.getId()) == null) {
            throw ServiceExceptionUtil.exception(BAN_RECORD_NOT_EXISTS);
        }
        userBanMapper.updateById(BeanUtils.toBean(updateReqVO, ImUserBanDO.class));
    }

    @Override
    @LogRecord(type = IM_USER_BAN_TYPE, subType = IM_USER_BAN_UNBAN_SUB_TYPE, bizNo = "{{#unbanReqVO.id}}", success = IM_USER_BAN_UNBAN_SUCCESS)
    public void unbanUserBan(ImUserBanManagerUnbanReqVO unbanReqVO, Long unbannedBy) {
        ImUserBanDO existing = userBanMapper.selectById(unbanReqVO.getId());
        if (existing == null) {
            throw ServiceExceptionUtil.exception(BAN_RECORD_NOT_EXISTS);
        }
        userBanMapper.updateById(new ImUserBanDO()
                .setId(unbanReqVO.getId()).setStatus(1)
                .setUnbannedBy(unbannedBy).setUnbanReason(unbanReqVO.getUnbanReason())
                .setUnbanTime(LocalDateTime.now()));
        LogRecordContext.putVariable("unbanReqVO", unbanReqVO);
        // 若解封的是「封号」记录，且用户无其它生效中的封号记录，则恢复账号可用。
        if (BAN_TYPE_ACCOUNT.equals(existing.getBanType())) {
            restoreAccountIfNoActiveBan(existing.getUserId(), existing.getId());
        }
    }

    @Override
    @LogRecord(type = IM_USER_BAN_TYPE, subType = IM_USER_BAN_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = IM_USER_BAN_DELETE_SUCCESS)
    public void deleteUserBan(Long id) {
        ImUserBanDO existing = userBanMapper.selectById(id);
        if (existing == null) {
            throw ServiceExceptionUtil.exception(BAN_RECORD_NOT_EXISTS);
        }
        userBanMapper.deleteById(id);
        // 若删除的是「封号」记录，且用户无其它生效中的封号记录，则恢复账号可用。
        if (BAN_TYPE_ACCOUNT.equals(existing.getBanType())) {
            restoreAccountIfNoActiveBan(existing.getUserId(), existing.getId());
        }
    }

    /**
     * 检查该用户是否还存在其它「生效中(status=0)」的封号记录（排除当前记录）。
     * 若没有，则恢复账号状态为启用、清除封禁标记（允许重新登录、收发消息）。
     */
    private void restoreAccountIfNoActiveBan(Long userId, Long excludeBanId) {
        if (userId == null) {
            return;
        }
        boolean stillBanned = userBanMapper.selectList(new LambdaQueryWrapperX<ImUserBanDO>()
                        .eq(ImUserBanDO::getUserId, userId)
                        .eq(ImUserBanDO::getBanType, BAN_TYPE_ACCOUNT)
                        .eq(ImUserBanDO::getStatus, 0) // 0=生效中
                        .ne(ImUserBanDO::getId, excludeBanId))
                .stream().findAny().isPresent();
        if (!stillBanned) {
            userService.updateStatus(userId, CommonStatusEnum.ENABLE.getStatus());
            userService.updateBanned(userId, false, null);
        }
    }
}
