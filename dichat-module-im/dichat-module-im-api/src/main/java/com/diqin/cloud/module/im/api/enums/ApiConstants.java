package com.diqin.cloud.module.im.api.enums;

/**
 * IM 模块 RPC 服务名 / 路径前缀
 *
 * @author hanson
 */
public class ApiConstants {

    /**
     * 服务名
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "im-server";

    public static final String PREFIX = "/rpc-api/im";

    public static final String VERSION = "1.0.0";

}
