package com.cvs.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交答案结果 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "提交答案结果")
public class QuizResultVO {

    @Schema(description = "是否正确", example = "true")
    private Boolean correct;

    @Schema(description = "排名（答对时返回，答错为null）", example = "1")
    private Integer rank;

    @Schema(description = "正确答案", example = "B")
    private String correctAnswer;

    @Schema(description = "当前答对总人数", example = "5")
    private Integer totalCorrect;
}
