package com.diqin.cloud.module.im.framework.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * IM 模块全局配置
 * <p>
 * 各子模块用嵌套 inner class 区分（friend / group / face / message / rtc 等），
 * yaml 路径保持 dichat.im.{module}.{key} 与原有部署保持兼容
 *
 * @author hanson
 */
@Component
@ConfigurationProperties(prefix = "dichat.im")
@Validated
@Data
public class ImProperties {

    private Friend friend = new Friend();

    private Group group = new Group();

    private Face face = new Face();

    private Message message = new Message();

    @Valid
    private Rtc rtc = new Rtc();

    @Valid
    private Auth auth = new Auth();

    /**
     * 应用版本检测配置（供 /im/app/version/check 接口比对客户端版本）
     */
    @Valid
    private AppVersion appVersion = new AppVersion();

    @Valid
    private Robot robot = new Robot();

    /**
     * 好友模块配置
     */
    @Data
    public static class Friend {

        /**
         * 是否自动通过所有好友申请（全局开关）
         * <p>
         * 默认 false，普通用户必须走申请-审批流程；开启后所有用户的好友申请会立即同意，主要用于全员开放型 IM 部署。
         * 如需细化到「仅特定用户自动通过」（如机器人 / AI 账号），请在 system 用户表加字段，并在 applyFriend 内按用户级开关短路
         */
        private boolean autoAccept = false;

    }

    /**
     * 群模块配置
     */
    @Data
    public static class Group {

        /**
         * 群最大成员人数
         */
        private int maxMember = 500;

        /**
         * 单群管理员人数上限
         */
        private int adminMaxCount = 3;

        /**
         * 单群置顶消息条数上限
         */
        private int pinMaxCount = 5;

    }

    /**
     * 表情模块配置
     */
    @Data
    public static class Face {

        /**
         * 个人表情数量上限
         */
        private int userItemMaxCount = 200;

    }

    /**
     * 消息模块配置
     */
    @Data
    public static class Message {

        /**
         * 是否启用私聊已读功能
         * <p>
         * 关闭后：private read 接口直接抛业务异常；服务端不再下发私聊 READ / RECEIPT 事件信号。
         * 客户端侧需镜像此开关，隐藏私聊气泡的「已读 / 未读」标签
         */
        private boolean privateReadEnabled = true;

        /**
         * 是否启用群聊已读功能（含群消息回执）
         * <p>
         * 关闭后：group read 接口直接抛业务异常；服务端不再下发群 READ / RECEIPT 事件信号；
         * 群消息回执 receiptStatus 一并停用（即使发送方传 receipt=true 也强制落 NO_RECEIPT，不再算「N 人已读」）。
         * 客户端侧需镜像此开关，隐藏群回执 popover 与「发送回执消息」入口
         */
        private boolean groupReadEnabled = true;

        /**
         * pull 最大拉取数量
         */
        private int maxPullSize = 1000;

        /**
         * 消息撤回时间限制（分钟）
         */
        private int recallTimeoutMinutes = 5;

        /**
         * 私聊离线消息最大拉取天数
         * <p>
         * 客户端通过 pull 接口增量拉取私聊离线消息时，仅返回最近 N 天内产生的消息，
         * 超过该窗口的老消息不再主动推送（可通过历史消息接口按需倒翻）。
         */
        private int privatePullMaxDays = 30;

        /**
         * 群聊离线消息最大拉取天数
         * <p>
         * 客户端通过 pull 接口增量拉取群聊离线消息时，仅返回最近 N 天内产生的消息；
         * 退群前消息的补齐也以该窗口为基准（早于窗口的退群群不再扫描），避免老用户首次
         * 拉取时对历史退群群做大量查询。
         */
        private int groupPullMaxDays = 30;

    }

    /**
     * 实时通话模块配置
     * <p>
     * 媒体走 LiveKit SFU；后端只签 Token + 通过 IM 长连接推送来电 / 接通 / 结束三种信令。
     * 关闭后所有 RTC 接口直接抛 RTC_NOT_ENABLED；前端可据此隐藏通话按钮。
     */
    @Data
    public static class Rtc {

        /**
         * 是否启用实时通话功能
         */
        private boolean enabled = true;

        /**
         * LiveKit Server WebSocket 地址；客户端 connect 时使用，通常 ws://host:7880 或 wss://host
         */
        @NotBlank(message = "LiveKit URL 不能为空")
        private String livekitUrl = "ws://127.0.0.1:7880";

        /**
         * LiveKit API Key
         */
        @NotBlank(message = "LiveKit API Key 不能为空")
        private String apiKey = "dichat-im";

        /**
         * LiveKit API Secret；生产必须改为强随机值
         */
        @NotBlank(message = "LiveKit API Secret 不能为空")
        @Size(min = 32, message = "LiveKit API Secret 长度需 ≥ 32 位")
        private String apiSecret = "CHANGE_ME";

        /**
         * 单次签发的 Token 有效期（小时）
         */
        private int tokenTtlHours = 6;

        /**
         * 群通话最大同时在房成员数；超过 invite 直接拒绝
         */
        private int groupMaxParticipants = 16;

        /**
         * 僵尸通话清理阈值（分钟）；通话创建超过此值仍未结束才纳入扫描，避开「刚发起还在响铃」的合理零人态
         */
        private int cleanupZombieThresholdMinutes = 5;

        /**
         * 振铃超时阈值（分钟）；被叫 INVITING 超过此值未接通 → 标 NO_ANSWER + 推 RTC_CALL(REJECT) 让 banner 收敛
         */
        private int inviteTimeoutMinutes = 1;

    }

    /**
     * IM 用户鉴权配置
     * <p>
     * 用于 {@code /im/auth/login} 的 access/refresh token 过期时间与密码长度约束；
     * token 本体由 dichat Security 体系承载（{@code TokenAuthenticationFilter}）。
     */
    @Data
    public static class Auth {

        /**
         * access token 过期时间（秒）
         */
        private int accessTokenExpireIn = 7200;

        /**
         * refresh token 过期时间（秒）
         */
        private int refreshTokenExpireIn = 30 * 24 * 3600;

        /**
         * 密码最短长度
         */
        private int passwordMinLength = 6;

        /**
         * 密码最长长度
         */
        private int passwordMaxLength = 32;

    }

    /**
     * 应用版本检测配置
     * <p>
     * 供 {@code /im/app/version/check} 比对客户端上报的当前版本号，判断是否有新版本 / 是否强制更新，
     * 并返回对应平台的下载地址。所有字段均可在 Nacos / yaml 的热更新配置中调整，无需改代码。
     */
    @Data
    public static class AppVersion {

        /**
         * 最新发布版本号，如 1.0.0；客户端当前版本低于此值即提示更新
         */
        private String latestVersion = "1.0.0";

        /**
         * 最低可运行版本号（强制更新阈值）；客户端当前版本低于此值返回 force=true
         * <p>
         * 为空表示不启用强制更新
         */
        private String forceBelowVersion = "";

        /**
         * Android 平台新版本下载地址（apk 直链）
         */
        private String androidDownloadUrl = "";

        /**
         * iOS 平台新版本下载地址（App Store / TestFlight）
         */
        private String iosDownloadUrl = "";

        /**
         * 通用下载地址；当对应平台专属地址为空时回退使用
         */
        private String downloadUrl = "";

        /**
         * 更新说明（展示在「发现新版本」弹窗）
         */
        private String description = "";

        /**
         * 安装包大小，如 28.6 MB
         */
        private String size = "";

    }

    /**
     * 机器人 / 转人工配置
     * <p>
     * 供机器人「转人工」动作使用：负载均衡挑选接手客服、以及去重冷却避免重复转接。
     * 所有字段均可在 Nacos / yaml 中调整，无需改代码。
     */
    @Valid
    @Data
    public static class Robot {

        /**
         * 负载均衡「活跃会话」统计窗口（分钟）：
         * 该窗口内与不同对端的私聊计入客服当前负载，用于按最低并发自动分配接手客服。
         * 默认 60 分钟（即近 1 小时）。
         */
        private int loadBalanceWindowMinutes = 60;

        /**
         * 「转人工」去重冷却窗口（分钟）：
         * 该窗口内用户已与某客服有过私聊，则不再重复触发转接开新会话，
         * 避免用户在 bot 会话里反复说话时每次都重开一个客服会话。
         * 默认 30 分钟。
         */
        private int transferCooldownMinutes = 30;

        /**
         * 是否启用「转人工」自动分配（总开关）：
         * <p>
         * 默认 true，命中「转人工」规则时按最低并发自动挑一个在线且已绑定 IM 身份的客服接手。
         * 设为 false 时，「转人工」整体降级为兜底提示（不再挑客服、不开用户↔客服会话），
         * 常用于客服下班 / 系统维护 / 模块灰度期间临时关掉人工转接。
         * 该开关为总闸，优先级高于下方的窗口类配置。
         */
        private boolean enableAutoAssign = true;

    }

}
