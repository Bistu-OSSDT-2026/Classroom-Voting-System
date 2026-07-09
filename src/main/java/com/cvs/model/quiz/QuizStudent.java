package com.cvs.model.quiz;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学生实体（抢答模块专用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("quiz_student")
public class QuizStudent {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_no")
    private String studentNo;

    @TableField("real_name")
    private String realName;

    @TableField("class_name")
    private String className;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
