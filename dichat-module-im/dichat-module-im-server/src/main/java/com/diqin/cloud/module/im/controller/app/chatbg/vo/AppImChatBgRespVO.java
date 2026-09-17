package com.diqin.cloud.module.im.controller.app.chatbg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户APP - 聊天背景 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppImChatBgRespVO {

    @Schema(description = "背景类型：1=图片(url) 2=颜色(hex)", example = "1")
    private Integer bgType;

    @Schema(description = "背景值：图片URL 或 颜色十六进制（如 #409EFF）", example = "https://cdn.example.com/bg.jpg")
    private String bgValue;
}
