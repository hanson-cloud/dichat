package com.diqin.cloud.module.im.enums;

import com.diqin.cloud.framework.common.exception.ErrorCode;

/**
 * IM 错误码枚举类
 * <p>
 * im 系统，使用 1-040-000-000 段。
 * 分层约定：1-040-100-xxx（钱包/支付/转账/红包/银行卡 App 端）与
 * 1-040-710/720/730/740-xxx（管理后台端）为同一业务的两套错误码，
 * 分别由 App 接口与 admin 管理接口引用，二者命名空间隔离、请勿混用同名。
 */
public interface ErrorCodeConstants {

    // ========== 消息 (1-040-300-000) ==========
    ErrorCode MESSAGE_NOT_EXISTS = new ErrorCode(1_040_300_000, "消息不存在");
    ErrorCode MESSAGE_RECALL_DENIED = new ErrorCode(1_040_300_002, "只能撤回自己发送的消息");
    ErrorCode MESSAGE_ALREADY_RECALLED = new ErrorCode(1_040_300_003, "消息已撤回");
    ErrorCode MESSAGE_SENSITIVE_WORD_BLOCKED = new ErrorCode(1_040_300_004, "消息包含敏感词，无法发送");
    ErrorCode MESSAGE_PULL_SIZE_EXCEEDED = new ErrorCode(1_040_300_005, "单次拉取消息数量不能超过 {} 条");
    ErrorCode MESSAGE_RECALL_TIMEOUT = new ErrorCode(1_040_300_007, "超过 {} 分钟的消息无法撤回");
    ErrorCode MESSAGE_QUOTE_INVALID = new ErrorCode(1_040_300_008, "引用的消息不可用");
    ErrorCode MESSAGE_NOT_IN_GROUP = new ErrorCode(1_040_300_009, "消息不属于该群");
    ErrorCode MESSAGE_PRIVATE_READ_DISABLED = new ErrorCode(1_040_300_010, "私聊已读功能已关闭");
    ErrorCode MESSAGE_GROUP_READ_DISABLED = new ErrorCode(1_040_300_011, "群聊已读功能已关闭");
    ErrorCode MESSAGE_CONTENT_INVALID = new ErrorCode(1_040_300_013, "消息内容格式不正确");
    ErrorCode MESSAGE_DELETE_DENIED = new ErrorCode(1_040_300_014, "仅能删除自己发送的消息");

    // ========== 群 (1-040-400-000) ==========
    ErrorCode GROUP_NOT_EXISTS = new ErrorCode(1_040_400_000, "群不存在");
    ErrorCode GROUP_BANNED = new ErrorCode(1_040_400_001, "群已被封禁");
    ErrorCode GROUP_DISSOLVED = new ErrorCode(1_040_400_002, "群已解散");
    ErrorCode GROUP_NOT_OWNER = new ErrorCode(1_040_400_003, "仅群主可执行该操作");
    ErrorCode GROUP_NOT_OWNER_OR_ADMIN = new ErrorCode(1_040_400_004, "仅群主或管理员可执行该操作");
    ErrorCode GROUP_TRANSFER_OWNER_TO_SELF = new ErrorCode(1_040_400_005, "不能将群主转让给自己");
    ErrorCode GROUP_MESSAGE_PIN_MAX_LIMIT = new ErrorCode(1_040_400_006, "群置顶消息数量不能超过 {} 条");
    ErrorCode GROUP_MESSAGE_ALREADY_PINNED = new ErrorCode(1_040_400_007, "该消息已置顶");
    ErrorCode GROUP_MESSAGE_NOT_PINNED = new ErrorCode(1_040_400_008, "该消息未置顶");
    ErrorCode GROUP_MESSAGE_PIN_DIRECTED_DENIED = new ErrorCode(1_040_400_009, "定向消息不支持置顶");

    // ========== 群成员 (1-040-500-000) ==========
    ErrorCode GROUP_MEMBER_NOT_IN_GROUP = new ErrorCode(1_040_500_001, "您已不在该群中");
    ErrorCode GROUP_OWNER_CANNOT_QUIT = new ErrorCode(1_040_500_003, "群主不能退出群聊，请先转让群主或解散群聊");
    ErrorCode GROUP_CANNOT_REMOVE_SELF = new ErrorCode(1_040_500_004, "不能将自己移出群聊");
    ErrorCode GROUP_MEMBER_EXCEED = new ErrorCode(1_040_500_005, "群聊人数不能超过 {} 人");
    ErrorCode GROUP_INVITE_NOT_FRIEND = new ErrorCode(1_040_500_006, "'{}' 不是您的好友，邀请失败");
    ErrorCode GROUP_ADMIN_TARGET_NOT_IN_GROUP = new ErrorCode(1_040_500_007, "目标用户已不在该群中");
    ErrorCode GROUP_ADMIN_TARGET_IS_OWNER = new ErrorCode(1_040_500_008, "群主无法被设为或撤销管理员");
    ErrorCode GROUP_ADMIN_MAX_LIMIT = new ErrorCode(1_040_500_009, "群管理员数量不能超过 {} 人");
    ErrorCode GROUP_REMOVE_OWNER_DENIED = new ErrorCode(1_040_500_010, "群主无法被移出群聊");
    ErrorCode GROUP_REMOVE_ADMIN_DENIED = new ErrorCode(1_040_500_011, "管理员无法移出其他管理员，请先由群主撤销其管理员身份");
    ErrorCode GROUP_MUTED_CANNOT_SEND = new ErrorCode(1_040_500_012, "群已全局禁言，仅群主和管理员可发送消息");
    ErrorCode GROUP_MEMBER_MUTED_CANNOT_SEND = new ErrorCode(1_040_500_013, "您已被禁言，解除时间：{}");
    ErrorCode GROUP_MUTE_MEMBER_SELF = new ErrorCode(1_040_500_014, "不能禁言自己");
    ErrorCode GROUP_MUTE_OWNER_DENIED = new ErrorCode(1_040_500_015, "群主无法被禁言");
    ErrorCode GROUP_MUTE_ADMIN_DENIED = new ErrorCode(1_040_500_016, "管理员无法禁言其他管理员");

    // ========== 好友 (1-040-600-000) ==========
    ErrorCode FRIEND_NOT_FRIEND = new ErrorCode(1_040_600_001, "对方不是您的好友");
    ErrorCode FRIEND_ADD_SELF = new ErrorCode(1_040_600_002, "不允许添加自己为好友");
    ErrorCode FRIEND_NOT_BLOCKED = new ErrorCode(1_040_600_003, "对方未在黑名单中");
    ErrorCode FRIEND_BLOCKED_BY_PEER = new ErrorCode(1_040_600_004, "您已被对方拉入黑名单，无法发送消息");

    // ========== 加群申请 (1-040-510-000) ==========
    ErrorCode GROUP_REQUEST_NOT_EXISTS = new ErrorCode(1_040_510_001, "加群申请不存在");
    ErrorCode GROUP_REQUEST_HANDLED = new ErrorCode(1_040_510_002, "加群申请已处理");
    ErrorCode GROUP_REQUEST_NOT_TO_ME = new ErrorCode(1_040_510_003, "仅群主或管理员可处理加群申请");
    ErrorCode GROUP_REQUEST_ALREADY_MEMBER = new ErrorCode(1_040_510_004, "您已在该群中，无需重复申请");

    // ========== 好友申请 (1-040-610-000) ==========
    ErrorCode FRIEND_REQUEST_NOT_EXISTS = new ErrorCode(1_040_610_001, "好友申请不存在");
    ErrorCode FRIEND_REQUEST_HANDLED = new ErrorCode(1_040_610_002, "好友申请已处理");
    ErrorCode FRIEND_REQUEST_NOT_TO_ME = new ErrorCode(1_040_610_003, "不能处理别人的好友申请");
    ErrorCode FRIEND_REQUEST_ALREADY_FRIEND = new ErrorCode(1_040_610_005, "您已是 TA 的好友，无需重复添加");
    ErrorCode FRIEND_REQUEST_BLOCKED_BY_PEER = new ErrorCode(1_040_610_006, "您已被对方拉入黑名单，无法添加为好友");

    // ========== 敏感词 (1-040-700-000) ==========
    ErrorCode SENSITIVE_WORD_NOT_EXISTS = new ErrorCode(1_040_700_000, "敏感词不存在");
    ErrorCode SENSITIVE_WORD_DUPLICATED = new ErrorCode(1_040_700_001, "敏感词 '{}' 已存在");

    // ========== 表情包 (1-040-800-000) ==========
    ErrorCode FACE_PACK_NOT_EXISTS = new ErrorCode(1_040_800_000, "表情包不存在");
    ErrorCode FACE_PACK_HAS_ITEMS = new ErrorCode(1_040_800_001, "表情包下还有表情，无法删除");
    ErrorCode FACE_PACK_ITEM_NOT_EXISTS = new ErrorCode(1_040_800_002, "表情不存在");
    ErrorCode FACE_USER_ITEM_NOT_EXISTS = new ErrorCode(1_040_800_010, "个人表情不存在");
    ErrorCode FACE_USER_ITEM_NOT_OWN = new ErrorCode(1_040_800_011, "不能操作他人的表情");
    ErrorCode FACE_USER_ITEM_DUPLICATED = new ErrorCode(1_040_800_013, "该表情已添加到个人表情");
    ErrorCode FACE_USER_ITEM_MAX_LIMIT = new ErrorCode(1_040_800_014, "个人表情数量不能超过 {} 个");

    // ========== 频道 (1-040-810-000) ==========
    ErrorCode IM_CHANNEL_NOT_EXISTS = new ErrorCode(1_040_810_000, "频道不存在");
    ErrorCode IM_CHANNEL_CODE_DUPLICATED = new ErrorCode(1_040_810_001, "频道编码 '{}' 已存在");
    ErrorCode IM_CHANNEL_HAS_MATERIAL = new ErrorCode(1_040_810_002, "频道下还有素材，无法删除");
    ErrorCode IM_CHANNEL_MATERIAL_NOT_EXISTS = new ErrorCode(1_040_810_010, "素材不存在");
    ErrorCode IM_CHANNEL_MATERIAL_USED = new ErrorCode(1_040_810_011, "素材已被推送过，无法删除");
    ErrorCode IM_CHANNEL_MESSAGE_NOT_EXISTS = new ErrorCode(1_040_810_020, "频道消息不存在");

    // ========== 实时通话 (1-040-900-000) ==========
    ErrorCode RTC_NOT_ENABLED = new ErrorCode(1_040_900_000, "通话功能未开启");
    ErrorCode RTC_SESSION_NOT_EXISTS = new ErrorCode(1_040_900_001, "通话已结束");
    ErrorCode RTC_PEER_BUSY = new ErrorCode(1_040_900_002, "对方正在通话中");
    ErrorCode RTC_SELF_BUSY = new ErrorCode(1_040_900_003, "您正在通话中");
    ErrorCode RTC_NOT_PARTICIPANT = new ErrorCode(1_040_900_004, "您不在该通话中");
    ErrorCode RTC_INVITE_SELF = new ErrorCode(1_040_900_005, "不能呼叫自己");
    ErrorCode RTC_PRIVATE_INVITEE_REQUIRED = new ErrorCode(1_040_900_006, "私聊通话必须指定对方");
    ErrorCode RTC_GROUP_REQUIRED = new ErrorCode(1_040_900_007, "群聊通话必须指定群编号");
    ErrorCode RTC_INVITE_BUSY = new ErrorCode(1_040_900_008, "通话发起繁忙，请稍后再试");
    ErrorCode RTC_GROUP_CALL_ACTIVE = new ErrorCode(1_040_900_009, "该群已有进行中通话，请通过胶囊条加入");
    ErrorCode RTC_GROUP_INVITEE_OVER_LIMIT = new ErrorCode(1_040_900_010, "群通话邀请人数超过最大值");
    ErrorCode RTC_GROUP_INVITEE_REQUIRED = new ErrorCode(1_040_900_011, "群通话必须选择被邀请人");

    // ========== IM 用户 (1-040-200-000) ==========
    ErrorCode IM_USER_NOT_EXISTS = new ErrorCode(1_040_200_000, "用户不存在");
    ErrorCode IM_USER_DISABLED = new ErrorCode(1_040_200_001, "账号已停用，请联系管理员");
    ErrorCode IM_USER_BANNED = new ErrorCode(1_040_200_002, "账号已被封禁：{}");
    ErrorCode IM_USER_PASSWORD_ERROR = new ErrorCode(1_040_200_003, "密码错误");
    ErrorCode IM_USER_USERNAME_DUPLICATED = new ErrorCode(1_040_200_004, "用户名 '{}' 已被注册");
    ErrorCode IM_USER_OLD_PASSWORD_ERROR = new ErrorCode(1_040_200_005, "原密码错误");
    ErrorCode IM_USER_INVALID_USERNAME = new ErrorCode(1_040_200_006, "用户名格式不合法（3-30 位字母 / 数字 / 下划线）");
    ErrorCode IM_USER_INVALID_PASSWORD = new ErrorCode(1_040_200_007, "密码长度必须在 {} - {} 之间");
    ErrorCode IM_USER_INVALID_NICKNAME = new ErrorCode(1_040_200_008, "昵称包含敏感词");
    ErrorCode IM_USER_NICKNAME_CONTAINS_SENSITIVE = new ErrorCode(1_040_200_009, "昵称包含敏感词");
    ErrorCode IM_USER_USERNAME_CONTAINS_SENSITIVE = new ErrorCode(1_040_200_010, "用户名包含敏感词");
    ErrorCode IM_USER_REFRESH_TOKEN_INVALID = new ErrorCode(1_040_200_011, "refreshToken 已失效或格式错误");

    // ===================== 账号注销 / 绑定（APP 用户自助） =====================
    ErrorCode ACCOUNT_ALREADY_CANCELLED = new ErrorCode(1_040_200_100, "账号已注销，无法重复注销");
    ErrorCode USER_BIND_TYPE_INVALID = new ErrorCode(1_040_200_101, "绑定类型不合法（仅支持 mobile / email）");
    ErrorCode USER_BIND_VALUE_INVALID = new ErrorCode(1_040_200_102, "绑定值格式不正确：{}");
    ErrorCode USER_BIND_MOBILE_DUPLICATED = new ErrorCode(1_040_200_103, "该手机号已被其他账号绑定");
    ErrorCode USER_BIND_EMAIL_DUPLICATED = new ErrorCode(1_040_200_104, "该邮箱已被其他账号绑定");
    ErrorCode FEEDBACK_CATEGORY_INVALID = new ErrorCode(1_040_200_105, "反馈类别不合法（1-5 为举报，6 为意见反馈）");

    // ========== 钱包 / 支付 - App 端 (1-040-100-000) ==========
    ErrorCode WALLET_NOT_EXIST = new ErrorCode(1_040_100_000, "钱包不存在");
    ErrorCode WALLET_BALANCE_NOT_ENOUGH = new ErrorCode(1_040_100_001, "余额不足");
    ErrorCode WALLET_BUSY = new ErrorCode(1_040_100_002, "钱包操作繁忙，请稍后再试");
    ErrorCode WALLET_STATUS_FREEZE = new ErrorCode(1_040_100_003, "钱包已被冻结");
    ErrorCode PAY_PASSWORD_NOT_SET = new ErrorCode(1_040_100_010, "请先设置支付密码");
    ErrorCode PAY_PASSWORD_ERROR = new ErrorCode(1_040_100_011, "支付密码错误");
    ErrorCode PAY_PASSWORD_WEAK = new ErrorCode(1_040_100_012, "支付密码至少 6 位");
    ErrorCode PAY_PASSWORD_RESET_PHONE_MISMATCH = new ErrorCode(1_040_100_013, "手机号与账户绑定手机号不一致");
    ErrorCode GESTURE_PATTERN_NOT_SET = new ErrorCode(1_040_100_016, "尚未设置手势密码");
    ErrorCode GESTURE_PATTERN_ERROR = new ErrorCode(1_040_100_017, "手势密码错误");
    ErrorCode TRANSFER_SELF = new ErrorCode(1_040_100_020, "不能给自己转账");
    ErrorCode TRANSFER_TARGET_NOT_EXIST = new ErrorCode(1_040_100_021, "转账目标用户不存在");
    ErrorCode TRANSFER_AMOUNT_INVALID = new ErrorCode(1_040_100_022, "转账金额必须大于 0");
    ErrorCode TRANSFER_SINGLE_LIMIT = new ErrorCode(1_040_100_023, "超出单笔转账限额");
    ErrorCode TRANSFER_DAILY_LIMIT = new ErrorCode(1_040_100_024, "超出每日转账限额");
    ErrorCode TRANSFER_NOT_EXIST = new ErrorCode(1_040_100_025, "转账订单不存在或无权限查看");
    ErrorCode TRANSFER_STATUS_INVALID = new ErrorCode(1_040_100_026, "转账订单状态异常，无法操作");

    // ========== 红包（App 端 / 1-040-100-100） ==========
    ErrorCode RED_PACKET_NOT_EXIST = new ErrorCode(1_040_100_100, "红包不存在");
    ErrorCode RED_PACKET_STATUS_INVALID = new ErrorCode(1_040_100_101, "红包状态异常，无法操作");
    ErrorCode RED_PACKET_AMOUNT_INVALID = new ErrorCode(1_040_100_102, "红包金额或个数不合法");
    ErrorCode RED_PACKET_OWN_FORBIDDEN = new ErrorCode(1_040_100_103, "不能抢自己发的红包");
    ErrorCode RED_PACKET_ALREADY_GRABBED = new ErrorCode(1_040_100_104, "您已抢过该红包");
    ErrorCode RED_PACKET_GRABBED_OUT = new ErrorCode(1_040_100_105, "手气已用完，红包抢光了");
    ErrorCode RED_PACKET_PASSWORD_ERROR = new ErrorCode(1_040_100_106, "红包口令错误");
    ErrorCode RED_PACKET_SINGLE_LIMIT = new ErrorCode(1_040_100_107, "超出单笔红包限额");
    ErrorCode RED_PACKET_DAILY_LIMIT = new ErrorCode(1_040_100_108, "超出每日发红包限额");

    // ========== 银行卡（App 端 / 1-040-100-200） ==========
    ErrorCode BANK_CARD_NOT_EXIST = new ErrorCode(1_040_100_200, "银行卡不存在");
    ErrorCode BANK_CARD_NOT_YOURS = new ErrorCode(1_040_100_201, "该银行卡不属于您");
    ErrorCode BANK_CARD_NO_INVALID = new ErrorCode(1_040_100_202, "银行卡号格式不正确（16-19 位数字）");
    ErrorCode BANK_CARD_ALREADY_BOUND = new ErrorCode(1_040_100_203, "该银行卡已绑定");
    ErrorCode BANK_CARD_BIND_FAILED = new ErrorCode(1_040_100_204, "银行卡绑定失败");

    // ========== 文件 (1-040-110-000) ==========
    ErrorCode FILE_NOT_EMPTY = new ErrorCode(1_040_110_000, "上传文件不能为空");
    ErrorCode FILE_SIZE_EXCEEDED = new ErrorCode(1_040_110_001, "上传文件大小不能超过 10MB");
    ErrorCode FILE_NOT_IMAGE = new ErrorCode(1_040_110_002, "仅支持上传图片文件");
    ErrorCode FILE_NOT_VIDEO = new ErrorCode(1_040_110_003, "仅支持上传视频文件");

    // ========== 红包 - 管理端 (1-040-710-000) ==========
    ErrorCode RED_PACKET_NOT_EXISTS = new ErrorCode(1_040_710_000, "红包不存在");
    ErrorCode RED_PACKET_FINISHED = new ErrorCode(1_040_710_001, "红包已无法领取");
    ErrorCode RED_PACKET_ALREADY_RECEIVED = new ErrorCode(1_040_710_002, "您已领取过该红包");
    ErrorCode RED_PACKET_NOT_RECEIVER = new ErrorCode(1_040_710_003, "您不是该私聊红包的领取人");
    ErrorCode RED_PACKET_GRAB_AMOUNT_INVALID = new ErrorCode(1_040_710_004, "红包金额必须大于零");
    ErrorCode RED_PACKET_GRAB_FAILED = new ErrorCode(1_040_710_005, "红包领取失败，请重试");
    ErrorCode RED_PACKET_REFUND_DENIED = new ErrorCode(1_040_710_006, "仅已过期且未领完的红包可退款");

    // ========== 银行卡 - 管理端 (1-040-720-000) ==========
    ErrorCode BANK_CARD_NOT_EXISTS = new ErrorCode(1_040_720_000, "银行卡不存在");
    ErrorCode BANK_CARD_NOT_OWN = new ErrorCode(1_040_720_001, "不能操作他人的银行卡");
    ErrorCode BANK_CARD_DUPLICATED = new ErrorCode(1_040_720_002, "该银行卡已绑定");
    ErrorCode BANK_CARD_AUDIT_DENIED = new ErrorCode(1_040_720_003, "仅待审核的银行卡可审核");
    ErrorCode BANK_CARD_UNBIND_DENIED = new ErrorCode(1_040_720_004, "仅正常状态的银行卡可解绑");

    ErrorCode BANK_NOT_EXISTS = new ErrorCode(1_040_720_005, "支持的银行不存在");

    ErrorCode BANK_CARD_LUHN_INVALID = new ErrorCode(1_040_720_006, "银行卡号校验未通过（卡号不合法）");
    ErrorCode BANK_CARD_BIN_NOT_SUPPORTED = new ErrorCode(1_040_720_007, "该银行卡号不在支持的银行范围内");
    ErrorCode BANK_CARD_BIN_MISMATCH = new ErrorCode(1_040_720_008, "银行卡号与所选银行不匹配");
    ErrorCode BANK_CARD_IDCARD_INVALID = new ErrorCode(1_040_720_009, "身份证号校验未通过");

    // ========== 转账 - 管理端 (1-040-730-000) ==========
    ErrorCode TRANSFER_NOT_EXISTS = new ErrorCode(1_040_730_000, "转账记录不存在");
    ErrorCode TRANSFER_TO_SELF = new ErrorCode(1_040_730_001, "不能给自己转账");
    ErrorCode TRANSFER_RECORD_AMOUNT_INVALID = new ErrorCode(1_040_730_002, "转账金额必须大于零");
    ErrorCode TRANSFER_FAILED = new ErrorCode(1_040_730_003, "转账失败，请稍后重试");

    // ========== 提现 - 管理端 (1-040-740-000) ==========
    ErrorCode WITHDRAW_NOT_EXISTS = new ErrorCode(1_040_740_000, "提现申请不存在");
    ErrorCode WITHDRAW_AMOUNT_INVALID = new ErrorCode(1_040_740_001, "提现金额必须大于零");
    ErrorCode WITHDRAW_BALANCE_NOT_ENOUGH = new ErrorCode(1_040_740_002, "钱包余额不足");
    ErrorCode WITHDRAW_NO_BANK_CARD = new ErrorCode(1_040_740_003, "请先绑定提现银行卡");
    ErrorCode WITHDRAW_FAILED = new ErrorCode(1_040_740_004, "提现失败，请稍后重试");
    ErrorCode WITHDRAW_AUDIT_DENIED = new ErrorCode(1_040_740_005, "仅待审核的提现申请可审核");
    ErrorCode WITHDRAW_DUPLICATE_PENDING = new ErrorCode(1_040_740_006, "存在进行中的提现申请，请等待审核完成后再发起");
    ErrorCode WITHDRAW_CANCEL_DENIED = new ErrorCode(1_040_740_007, "仅待审核的提现申请可撤销");
    ErrorCode WITHDRAW_DISABLED = new ErrorCode(1_040_740_008, "提现功能已关闭，暂不可提现");
    ErrorCode WITHDRAW_CONFIG_MESSAGE_REQUIRED = new ErrorCode(1_040_740_009, "关闭提现功能时必须填写通知用户的关闭消息");
    ErrorCode WITHDRAW_CONFIG_ENABLED_NULL = new ErrorCode(1_040_740_010, "提现开关状态不能为空");

    // ========== 用户标签 - 管理端 (1-040-750-000) ==========
    ErrorCode USER_TAG_NOT_EXISTS = new ErrorCode(1_040_750_000, "用户标签不存在");
    ErrorCode USER_TAG_NAME_DUPLICATE = new ErrorCode(1_040_750_001, "标签名称 '{}' 已存在");

    // ========== 机器人 - 管理端 (1-040-750-002) ==========
    ErrorCode ROBOT_NOT_EXISTS = new ErrorCode(1_040_750_002, "机器人不存在");
    ErrorCode ROBOT_USERNAME_DUPLICATE = new ErrorCode(1_040_750_003, "机器人账号 '{}' 已存在");
    ErrorCode ROBOT_REPLY_RULE_NOT_EXISTS = new ErrorCode(1_040_750_004, "机器人自动回复规则不存在");
    ErrorCode ROBOT_REPLY_RULE_KEYWORD_DUPLICATE = new ErrorCode(1_040_750_005, "该机器人的关键词 '{}' 已存在");
    ErrorCode ROBOT_USER_ID_DUPLICATE = new ErrorCode(1_040_750_013, "该 IM 用户 '{}' 已绑定其他机器人");
    ErrorCode ROBOT_USER_NOT_EXISTS = new ErrorCode(1_040_750_014, "关联的 IM 用户 '{}' 不存在");

    // ========== 客服 - 管理端 (1-040-750-006) ==========
    ErrorCode CUSTOMER_SERVICE_NOT_EXISTS = new ErrorCode(1_040_750_006, "客服不存在");
    ErrorCode CUSTOMER_SERVICE_USERNAME_DUPLICATE = new ErrorCode(1_040_750_007, "客服工号 '{}' 已存在");
    ErrorCode CUSTOMER_SERVICE_ADMIN_NOT_BOUND = new ErrorCode(1_040_750_015, "当前管理员未绑定客服身份，无法使用客服工作台");
    ErrorCode CUSTOMER_SERVICE_STATUS_INVALID = new ErrorCode(1_040_750_016, "客服在线状态不合法（仅允许 0-离线 1-在线 2-忙碌）");

    // ========== 消息审核 - 管理端 (1-040-750-008) ==========
    ErrorCode MESSAGE_REVIEW_NOT_EXISTS = new ErrorCode(1_040_750_008, "消息审核记录不存在");

    // ========== 运营活动位 - 管理端 (1-040-750-009) ==========
    ErrorCode ACTIVITY_SLOT_NOT_EXISTS = new ErrorCode(1_040_750_009, "运营活动位不存在");

    // ========== 封禁台账 - 管理端 (1-040-750-010) ==========
    ErrorCode BAN_RECORD_NOT_EXISTS = new ErrorCode(1_040_750_010, "封禁记录不存在");

    // ========== 应用版本 - 管理端 (1-040-750-011) ==========
    ErrorCode APP_VERSION_NOT_EXISTS = new ErrorCode(1_040_750_011, "应用版本不存在");
    ErrorCode APP_VERSION_PLATFORM_VERSION_DUPLICATE = new ErrorCode(1_040_750_012, "该平台版本 '{}' 已存在");

    // ========== 收藏 (1-040-310-000) ==========
    ErrorCode FAVORITE_NOT_EXISTS = new ErrorCode(1_040_310_000, "收藏记录不存在");
    ErrorCode FAVORITE_ALREADY_EXISTS = new ErrorCode(1_040_310_001, "该消息已收藏");
    ErrorCode FAVORITE_DELETE_DENIED = new ErrorCode(1_040_310_002, "无权删除该收藏");

    // ========== 朋友圈 (1-040-320-000) ==========
    ErrorCode MOMENT_NOT_EXISTS = new ErrorCode(1_040_320_000, "动态不存在");
    ErrorCode MOMENT_DELETE_DENIED = new ErrorCode(1_040_320_001, "无权删除该动态");
    ErrorCode MOMENT_LIKE_DUPLICATE = new ErrorCode(1_040_320_002, "已点赞过该动态");
    ErrorCode MOMENT_COMMENT_NOT_EXISTS = new ErrorCode(1_040_320_003, "评论不存在");
    ErrorCode MOMENT_COMMENT_DELETE_DENIED = new ErrorCode(1_040_320_004, "无权删除该评论");
    ErrorCode MOMENT_INVISIBLE_REMIND_CONFLICT = new ErrorCode(1_040_320_005, "不给谁看与提醒谁看不能是同一人");

    // ========== 聊天背景 (1-040-330-000) ==========
    ErrorCode CHAT_BG_VALUE_EMPTY = new ErrorCode(1_040_330_000, "聊天背景内容不能为空");

    // ========== 附近的人 (1-040-340-000) ==========
    ErrorCode NEARBY_LOCATION_INVALID = new ErrorCode(1_040_340_000, "经纬度不合法");

}
