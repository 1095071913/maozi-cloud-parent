package com.maozi.ai.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author pengjinlong
 * @since 2026/8/22 15:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatGenerateImageParam extends ChatParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "生成数量")
    private Integer count;

    @Schema(description = "图片高度")
    private Integer height;

    @Schema(description = "图片宽度")
    private Integer width;

}
