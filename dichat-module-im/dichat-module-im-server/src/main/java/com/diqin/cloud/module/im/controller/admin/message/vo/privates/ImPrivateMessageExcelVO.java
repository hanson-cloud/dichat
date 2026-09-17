package com.diqin.cloud.module.im.controller.admin.message.vo.privates;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IM 私聊消息 Excel VO
 *
 * @author hanson
 */
@Data
public class ImPrivateMessageExcelVO {

    @ExcelProperty("消息编号")
    private Long id;

    @ExcelProperty("客户端消息编号")
    private String clientMessageId;

    @ExcelProperty("发送人编号")
    private Long senderId;

    @ExcelProperty("发送人昵称")
    private String senderNickname;

    @ExcelProperty("接收人编号")
    private Long receiverId;

    @ExcelProperty("接收人昵称")
    private String receiverNickname;

    @ExcelProperty("发送方类型")
    private Integer senderType;

    @ExcelProperty("接收方类型")
    private Integer receiverType;

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
