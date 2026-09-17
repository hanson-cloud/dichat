package com.diqin.cloud.module.im.controller.admin.message.vo.group;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IM 群聊消息 Excel VO
 *
 * @author hanson
 */
@Data
public class ImGroupMessageExcelVO {

    @ExcelProperty("消息编号")
    private Long id;

    @ExcelProperty("客户端消息编号")
    private String clientMessageId;

    @ExcelProperty("群编号")
    private Long groupId;

    @ExcelProperty("群名称")
    private String groupName;

    @ExcelProperty("发送人编号")
    private Long senderId;

    @ExcelProperty("发送人昵称")
    private String senderNickname;

    @ExcelProperty("消息类型")
    private Integer type;

    @ExcelProperty("消息内容(JSON)")
    private String content;

    @ExcelProperty("消息状态")
    private Integer status;

    @ExcelProperty("发送时间")
    private LocalDateTime sendTime;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
