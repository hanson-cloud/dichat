package com.diqin.cloud.module.im.dal.dataobject.customer_service;

import lombok.Data;

/**
 * 客服「当前活跃会话数」统计结果（非持久化实体，仅用于管理后台列表负载展示）
 *
 * @author 速构构
 */
@Data
public class ImCustomerServiceActiveStatDO {

    /**
     * 客服 id（对应 im_customer_service.id）
     */
    private Long csId;

    /**
     * 窗口内活跃会话数（与不同对端的私聊数）
     */
    private Integer activeConversations;

}
