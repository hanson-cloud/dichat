package com.diqin.cloud.module.im.service.user.bo;

import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import lombok.Data;

import java.io.Serializable;

/**
 * 「附近的人」业务对象
 * <p>
 * 由 Service 层组装：携带用户 DO + 与请求中心的精确距离（米，Haversine 计算）。
 * 供 Controller 转成 {@code AppImNearbyUserRespVO} 返回，避免 Service 反向依赖 controller 包 VO。
 *
 * @author hanson
 */
@Data
public class NearbyUserBO implements Serializable {

    /**
     * 用户 DO
     */
    private ImUserDO user;

    /**
     * 与请求中心的精确距离，单位：米
     */
    private Double distance;
}
