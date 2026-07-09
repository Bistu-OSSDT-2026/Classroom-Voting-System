package com.cvs.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 创建抢答题目请求
 */
public class QuizCreateRequest {

    @NotBlank(message = "题目内容不能为空")
    @Size(max = 200, message = "题目内容最长200字")
    private String title;

    @NotNull(message = "选项列表不能为空")
    @Size(min = 2, max = 6, message = "选项数量为2~6个")
    private List<String> options;

    @NotBlank(message = "正确答案不能为空")
    private String correctOption;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}
