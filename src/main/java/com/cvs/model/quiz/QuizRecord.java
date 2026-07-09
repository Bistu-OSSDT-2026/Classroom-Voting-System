package com.cvs.model.quiz;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 抢答记录实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("quiz_record")
public class QuizRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("question_id")
    private Long questionId;

    @TableField("student_id")
    private Long studentId;

    @TableField("selected_answer")
    private String selectedAnswer;

    @TableField("is_correct")
    private Boolean isCorrect;

    @TableField("submit_time")
    private LocalDateTime submitTime;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
