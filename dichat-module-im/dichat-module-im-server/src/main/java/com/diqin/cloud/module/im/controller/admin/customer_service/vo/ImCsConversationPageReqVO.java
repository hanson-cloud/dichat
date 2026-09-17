package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - 客服工作台会话分页 Request VO
 *
 * @author 速构构
 */
@Schema(description = "管理后台 - 客服工作台会话分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImCsConversationPageReqVO extends PageParam {

}
