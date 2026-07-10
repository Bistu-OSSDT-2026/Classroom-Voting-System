package com.cvs.dto.quiz;

/**
 * 提交答案结果VO
 */
public class QuizResultVO {

    private boolean correct;
    private Integer rank;
    private String correctOption;

    public QuizResultVO() {}

    public QuizResultVO(boolean correct, Integer rank, String correctOption) {
        this.correct = correct;
        this.rank = rank;
        this.correctOption = correctOption;
    }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }
}
