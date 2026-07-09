package com.cvs.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 题目状态 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "题目状态")
public class QuizStatusVO {

    @Schema(description = "题目ID", example = "1")
    private Long id;

    @Schema(description = "题目", example = "Java中关键字'final'修饰类的作用是什么？")
    private String title;

    @Schema(description = "A选项")
    private String optionA;

    @Schema(description = "B选项")
    private String optionB;

    @Schema(description = "C选项")
    private String optionC;

    @Schema(description = "D选项")
    private String optionD;

    @Schema(description = "状态:PENDING-待开始,ACTIVE-进行中,CLOSED-已结束", example = "ACTIVE")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startedAt;

    @Schema(description = "已提交人数")
    private Integer submittedCount;

    @Schema(description = "答对人数")
    private Integer correctCount;
}
