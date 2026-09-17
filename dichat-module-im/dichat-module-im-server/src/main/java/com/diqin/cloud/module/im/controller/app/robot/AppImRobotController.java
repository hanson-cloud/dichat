package com.diqin.cloud.module.im.controller.app.robot;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerRespVO;
import com.diqin.cloud.module.im.controller.app.robot.vo.AppImCommonQuestionRespVO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotReplyRuleDO;
import com.diqin.cloud.module.im.service.robot.ImRobotManagerService;
import com.diqin.cloud.module.im.service.robot.ImRobotReplyRuleManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 机器人
 * <p>
 * 供移动端「智能助手」入口拉取启用中的机器人（含关联 userId），
 * App 据此打开与该 userId 的 IM 私聊会话，由后端自动回复引擎应答。
 * <p>
 * 路由说明：网关将 {@code /app-api/im/**} 转发到本服务（真实部署会重写为 {@code /im/**}），
 * 为兼容两种网关配置，本接口同时注册 {@code /im/app/robot/list}
 * 与 {@code /app-api/im/app/robot/list} 两个路径。
 *
 * @author 速构构
 */
@Tag(name = "用户 APP - 机器人")
@RestController
@Validated
public class AppImRobotController {

    @Resource
    private ImRobotManagerService robotManagerService;

    @Resource
    private ImRobotReplyRuleManagerService replyRuleManagerService;

    @GetMapping({"/im/app/robot/list"})
    @Operation(summary = "获取启用中的机器人列表（用于移动端智能助手入口打开私聊）")
    public CommonResult<List<ImRobotManagerRespVO>> getRobotList() {
        return success(robotManagerService.getAppRobotList());
    }

    @GetMapping({"/im/app/robot/common-questions"})
    @Operation(summary = "获取常见问题列表（客服页快捷入口）", description = "从启用中的机器人规则里随机拉取 reply_type=3 的常见问题")
    @Parameter(name = "limit", description = "返回条数，默认 5", example = "5")
    public CommonResult<List<AppImCommonQuestionRespVO>> getCommonQuestions(
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {
        List<ImRobotReplyRuleDO> rules = replyRuleManagerService.getCommonQuestions(limit);
        if (rules == null || rules.isEmpty()) {
            return success(Collections.emptyList());
        }
        return success(BeanUtils.toBean(rules, AppImCommonQuestionRespVO.class));
    }

}
