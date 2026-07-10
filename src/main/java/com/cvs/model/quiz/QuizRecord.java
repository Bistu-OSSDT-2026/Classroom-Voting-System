package com.cvs.model.quiz;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 抢答记录表
 * 流程：先抢名额（前3人）→ 再提交答案
 */
@Entity
@Table(name = "quiz_record",
       uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "student_id"}),
       indexes = @Index(columnList = "question_id, grab_order"))
public class QuizRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /** 抢答顺序（1/2/3，仅前3人有值） */
    @Column(name = "grab_order")
    private Integer grabOrder;

    /** 抢到名额的时间 */
    @Column(name = "grab_time")
    private LocalDateTime grabTime;

    /** 所选选项（抢到名额后填写） */
    @Column(name = "chosen_option", length = 10)
    private String chosenOption;

    /** 是否正确 */
    private Boolean isCorrect;

    /** 提交答案的时间 */
    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    public QuizRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Integer getGrabOrder() { return grabOrder; }
    public void setGrabOrder(Integer grabOrder) { this.grabOrder = grabOrder; }
    public LocalDateTime getGrabTime() { return grabTime; }
    public void setGrabTime(LocalDateTime grabTime) { this.grabTime = grabTime; }
    public String getChosenOption() { return chosenOption; }
    public void setChosenOption(String chosenOption) { this.chosenOption = chosenOption; }
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
}
