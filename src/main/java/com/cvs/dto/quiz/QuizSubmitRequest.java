package com.cvs.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 提交抢答答案请求
 */
public class QuizSubmitRequest {

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotBlank(message = "所选选项不能为空")
    private String chosenOption;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getChosenOption() { return chosenOption; }
    public void setChosenOption(String chosenOption) { this.chosenOption = chosenOption; }
}
