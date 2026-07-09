package com.cvs.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排行榜 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "排行榜条目")
public class QuizRankVO {

    @Schema(description = "排名", example = "1")
    private Integer rank;

    @Schema(description = "学生姓名", example = "李明")
    private String studentName;

    @Schema(description = "提交时间", example = "2026-07-09 10:30:45.123")
    private String submitTime;
}
