package com.cvs.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建抢答题目请求 DTO
 */
@Data
@Schema(description = "创建抢答题目请求")
public class QuizCreateRequest {

    @NotBlank(message = "题目内容不能为空")
    @Schema(description = "题目", example = "Java中关键字'final'修饰类的作用是什么？")
    private String title;

    @NotBlank(message = "A选项不能为空")
    @Schema(description = "A选项", example = "该类可以被继承")
    private String optionA;

    @NotBlank(message = "B选项不能为空")
    @Schema(description = "B选项", example = "该类不能被继承")
    private String optionB;

    @Schema(description = "C选项", example = "该类必须实现接口")
    private String optionC;

    @Schema(description = "D选项", example = "该类是抽象的")
    private String optionD;

    @NotBlank(message = "正确答案不能为空")
    @Schema(description = "正确答案(A/B/C/D)", example = "B")
    private String correctAnswer;

    @NotNull(message = "创建人ID不能为空")
    @Schema(description = "创建人ID(教师)", example = "1")
    private Long createdBy;
}
