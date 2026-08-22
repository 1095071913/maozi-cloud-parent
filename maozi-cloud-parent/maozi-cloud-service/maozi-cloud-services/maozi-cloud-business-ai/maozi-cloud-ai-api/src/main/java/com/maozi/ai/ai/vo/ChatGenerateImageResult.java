package com.maozi.ai.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * @author pengjinlong
 * @since 2026/8/22 20:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGenerateImageResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "图片列表")
    private Set<String> images;

}
