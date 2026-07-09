package com.cvs.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交抢答答案请求 DTO
 */
@Data
@Schema(description = "提交抢答答案请求")
public class QuizSubmitRequest {

    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID", example = "1")
    private Long questionId;

    @NotNull(message = "学生ID不能为空")
    @Schema(description = "学生ID", example = "1")
    private Long studentId;

    @NotBlank(message = "选择的答案不能为空")
    @Schema(description = "选择的答案(A/B/C/D)", example = "B")
    private String selectedAnswer;
}
