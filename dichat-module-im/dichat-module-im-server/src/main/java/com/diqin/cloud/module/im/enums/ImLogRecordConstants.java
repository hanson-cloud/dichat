package com.diqin.cloud.module.im.enums;

/**
 * IM 操作日志记录常量
 *
 * <p>对应框架 @LogRecord 注解的 type/subType/success 参数。
 * SpEL 变量由 ServiceImpl 中 LogRecordContext.putVariable() 注入。
 *
 * @author 速构构
 */
public interface ImLogRecordConstants {

    // ======================= IM 举报 =======================
    String IM_COMPLAINT_TYPE = "IM 举报";
    String IM_COMPLAINT_HANDLE_SUB_TYPE = "审核处置举报";
    String IM_COMPLAINT_HANDLE_SUCCESS = "处置了举报 #{{#reqVO.id}}（{{#reqVO.handleResult == null ? '' : reqVO.handleResult}}）";

    // ======================= IM 公告 =======================

    // ======================= IM 群成员 =======================
    String IM_GROUP_MEMBER_TYPE = "IM 群成员";
    String IM_GROUP_MEMBER_CREATE_SUB_TYPE = "添加群成员";
    String IM_GROUP_MEMBER_CREATE_SUCCESS = "向群 #{{#reqVO.groupId}} 添加了成员 #{{#reqVO.userId}}";
    String IM_GROUP_MEMBER_UPDATE_ROLE_SUB_TYPE = "修改群成员角色";
    String IM_GROUP_MEMBER_UPDATE_ROLE_SUCCESS = "修改了群 #{{#reqVO.groupId}} 成员 #{{#reqVO.userId}} 的角色为 {{#reqVO.role}}";
    String IM_GROUP_MEMBER_DELETE_SUB_TYPE = "移除群成员";
    String IM_GROUP_MEMBER_DELETE_SUCCESS = "从群 #{{#groupId}} 移除了成员 #{{#userId}}";

    // ======================= IM 黑名单 =======================
    String IM_BLACKLIST_TYPE = "IM 黑名单";
    String IM_BLACKLIST_CREATE_SUB_TYPE = "添加黑名单";
    String IM_BLACKLIST_CREATE_SUCCESS = "将用户 #{{#reqVO.blockId}} 加入黑名单";
    String IM_BLACKLIST_DELETE_SUB_TYPE = "移除黑名单";
    String IM_BLACKLIST_DELETE_SUCCESS = "将用户 #{{#blockId}} 移出黑名单";

    // ======================= IM 登录管理 =======================
    String IM_LOGIN_LOG_TYPE = "IM 登录管理";
    String IM_LOGIN_LOG_FORCE_OFFLINE_SUB_TYPE = "强制下线";
    String IM_LOGIN_LOG_FORCE_OFFLINE_SUCCESS = "强制下线了登录记录 #{{#id}}";

    // ======================= IM 用户标签 =======================
    String IM_USER_TAG_TYPE = "IM 用户标签";
    String IM_USER_TAG_CREATE_SUB_TYPE = "创建用户标签";
    String IM_USER_TAG_CREATE_SUCCESS = "创建了标签【{{#tag.name}}】";
    String IM_USER_TAG_UPDATE_SUB_TYPE = "更新用户标签";
    String IM_USER_TAG_UPDATE_SUCCESS = "更新了标签 #{{#updateReqVO.id}}: {_DIFF{#updateReqVO}}";
    String IM_USER_TAG_DELETE_SUB_TYPE = "删除用户标签";
    String IM_USER_TAG_DELETE_SUCCESS = "删除了标签【{{#tag.name}}】";
    String IM_USER_TAG_ASSIGN_SUB_TYPE = "打标用户";
    String IM_USER_TAG_ASSIGN_SUCCESS = "为标签 #{{#reqVO.tagId}} 打标了 {{#reqVO.userIds?.size()}} 个用户";
    String IM_USER_TAG_UNASSIGN_SUB_TYPE = "取消用户标签";
    String IM_USER_TAG_UNASSIGN_SUCCESS = "取消了标签 #{{#tagId}} 与用户 #{{#userId}} 的关联";

    // ======================= IM 机器人 =======================
    String IM_ROBOT_TYPE = "IM 机器人";
    String IM_ROBOT_CREATE_SUB_TYPE = "创建机器人";
    String IM_ROBOT_CREATE_SUCCESS = "创建了机器人【{{#robot.nickname}}】({{#robot.username}})";
    String IM_ROBOT_UPDATE_SUB_TYPE = "更新机器人";
    String IM_ROBOT_UPDATE_SUCCESS = "更新了机器人 #{{#updateReqVO.id}}";
    String IM_ROBOT_DELETE_SUB_TYPE = "删除机器人";
    String IM_ROBOT_DELETE_SUCCESS = "删除了机器人 #{{#id}}";
    String IM_ROBOT_RULE_CREATE_SUB_TYPE = "创建回复规则";
    String IM_ROBOT_RULE_CREATE_SUCCESS = "为机器人 #{{#reqVO.robotId}} 创建了回复规则【{{#reqVO.keyword}}】";
    String IM_ROBOT_RULE_UPDATE_SUB_TYPE = "更新回复规则";
    String IM_ROBOT_RULE_UPDATE_SUCCESS = "更新了回复规则 #{{#updateReqVO.id}}";
    String IM_ROBOT_RULE_DELETE_SUB_TYPE = "删除回复规则";
    String IM_ROBOT_RULE_DELETE_SUCCESS = "删除了回复规则 #{{#id}}";

    // ======================= IM 客服 =======================
    String IM_CUSTOMER_SERVICE_TYPE = "IM 客服";
    String IM_CUSTOMER_SERVICE_CREATE_SUB_TYPE = "创建客服";
    String IM_CUSTOMER_SERVICE_CREATE_SUCCESS = "创建了客服【{{#cs.nickname}}】({{#cs.username}})";
    String IM_CUSTOMER_SERVICE_UPDATE_SUB_TYPE = "更新客服";
    String IM_CUSTOMER_SERVICE_UPDATE_SUCCESS = "更新了客服 #{{#updateReqVO.id}}";
    String IM_CUSTOMER_SERVICE_DELETE_SUB_TYPE = "删除客服";
    String IM_CUSTOMER_SERVICE_DELETE_SUCCESS = "删除了客服 #{{#id}}";

    // ======================= IM 消息审核 =======================
    String IM_MESSAGE_REVIEW_TYPE = "IM 消息审核";
    String IM_MESSAGE_REVIEW_REVIEW_SUB_TYPE = "审核消息";
    String IM_MESSAGE_REVIEW_REVIEW_SUCCESS = "{{#reqVO.reviewStatus == 1 ? '通过' : '驳回'}}了消息审核 #{{#reqVO.id}}";

    // ======================= IM 运营活动位 =======================
    String IM_ACTIVITY_SLOT_TYPE = "IM 运营活动位";
    String IM_ACTIVITY_SLOT_CREATE_SUB_TYPE = "创建活动位";
    String IM_ACTIVITY_SLOT_CREATE_SUCCESS = "创建了活动位【{{#slot.name}}】";
    String IM_ACTIVITY_SLOT_UPDATE_SUB_TYPE = "更新活动位";
    String IM_ACTIVITY_SLOT_UPDATE_SUCCESS = "更新了活动位 #{{#updateReqVO.id}}: {_DIFF{#updateReqVO}}";
    String IM_ACTIVITY_SLOT_DELETE_SUB_TYPE = "删除活动位";
    String IM_ACTIVITY_SLOT_DELETE_SUCCESS = "删除了活动位 #{{#id}}";

    // ======================= IM 封禁台账 =======================
    String IM_USER_BAN_TYPE = "IM 封禁台账";
    String IM_USER_BAN_CREATE_SUB_TYPE = "封禁用户";
    String IM_USER_BAN_CREATE_SUCCESS = "{{#ban.banType == 1 ? '禁言' : '封号'}}了用户 #{{#ban.userId}}";
    String IM_USER_BAN_UPDATE_SUB_TYPE = "更新封禁记录";
    String IM_USER_BAN_UPDATE_SUCCESS = "更新了封禁记录 #{{#updateReqVO.id}}";
    String IM_USER_BAN_UNBAN_SUB_TYPE = "解封用户";
    String IM_USER_BAN_UNBAN_SUCCESS = "解封了用户（封禁记录 #{{#unbanReqVO.id}}）";
    String IM_USER_BAN_DELETE_SUB_TYPE = "删除封禁记录";
    String IM_USER_BAN_DELETE_SUCCESS = "删除了封禁记录 #{{#id}}";

    // ======================= IM 应用版本 =======================
    String IM_APP_VERSION_TYPE = "IM 应用版本";
    String IM_APP_VERSION_CREATE_SUB_TYPE = "创建应用版本";
    String IM_APP_VERSION_CREATE_SUCCESS = "创建了应用版本【{{#appVersion.versionName}}】";
    String IM_APP_VERSION_UPDATE_SUB_TYPE = "更新应用版本";
    String IM_APP_VERSION_UPDATE_SUCCESS = "更新了应用版本 #{{#updateReqVO.id}}";
    String IM_APP_VERSION_DELETE_SUB_TYPE = "删除应用版本";
    String IM_APP_VERSION_DELETE_SUCCESS = "删除了应用版本 #{{#id}}";
}
