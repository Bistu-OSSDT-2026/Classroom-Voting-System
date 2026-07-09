package com.cvs.model.quiz;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 抢答题目实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("quiz_question")
public class QuizQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("option_a")
    private String optionA;

    @TableField("option_b")
    private String optionB;

    @TableField("option_c")
    private String optionC;

    @TableField("option_d")
    private String optionD;

    @TableField("correct_answer")
    private String correctAnswer;

    @TableField("status")
    private String status;

    @TableField("created_by")
    private Long createdBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("started_at")
    private LocalDateTime startedAt;

    @TableField("closed_at")
    private LocalDateTime closedAt;

    /** 状态常量 */
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CLOSED = "CLOSED";
}
