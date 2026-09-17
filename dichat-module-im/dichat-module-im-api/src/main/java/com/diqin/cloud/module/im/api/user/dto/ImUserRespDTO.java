package com.diqin.cloud.module.im.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IM 用户信息（对外 RPC DTO）
 *
 * <p>对齐 box-im UserVO 语义，独立维护于 im_users 表；不依赖 system_users。
 * 与 AdminUserRespDTO 字段差异：
 * <ul>
 *   <li>独立 userName 登录账号（admin 用 username 字段）</li>
 *   <li>多 sex / signature / isBanned / reason / lastLoginTime 业务字段</li>
 *   <li>无 deptId / postIds（IM 用户不参与组织架构）</li>
 * </ul>
 *
 * @author hanson
 */
@Schema(description = "RPC 服务 - IM 用户 Response DTO")
@Data
public class ImUserRespDTO {

    @Schema(description = "用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "登录账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    private String username;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String nickname;

    @Schema(description = "头像地址", example = "https://www.diqin.com/1.png")
    private String avatar;

    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer sex;

    @Schema(description = "个性签名", example = "hello world")
    private String signature;

    @Schema(description = "手机号", example = "15601691300")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "账号状态 0:正常 1:停用", example = "0")
    private Integer status;

    @Schema(description = "是否被封禁", example = "false")
    private Boolean isBanned;

    @Schema(description = "封禁原因", example = "违反社区规范")
    private String banReason;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "注册时间")
    private LocalDateTime createTime;

}
