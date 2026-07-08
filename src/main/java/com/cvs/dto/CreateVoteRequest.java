package com.cvs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateVoteRequest {

    @NotBlank(message = "投票标题不能为空")
    private String title;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "知识点ID不能为空")
    private Long knowledgePointId;

    @NotNull(message = "选项列表不能为空")
    private List<OptionItem> options;

    /** true=匿名投票(不显示谁选了啥), false=实名投票(显示学生用户名) */
    private boolean anonymous = false;

    public static class OptionItem {
        @NotBlank
        private String text;

        @NotNull
        private Boolean isCorrect;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public Boolean getIsCorrect() { return isCorrect; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public List<OptionItem> getOptions() { return options; }
    public void setOptions(List<OptionItem> options) { this.options = options; }
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
}
