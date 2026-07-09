package com.cvs.dto;

/**
 * 投票结果 VO — 返回给学生，告知投票是否正确及正确答案
 */
public class CastVoteResultVO {

    private boolean correct;           // 是否投对
    private Long correctOptionId;      // 正确答案选项ID
    private String correctOptionText;  // 正确答案文本
    private Long chosenOptionId;       // 学生所选选项ID

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public Long getCorrectOptionId() { return correctOptionId; }
    public void setCorrectOptionId(Long correctOptionId) { this.correctOptionId = correctOptionId; }

    public String getCorrectOptionText() { return correctOptionText; }
    public void setCorrectOptionText(String correctOptionText) { this.correctOptionText = correctOptionText; }

    public Long getChosenOptionId() { return chosenOptionId; }
    public void setChosenOptionId(Long chosenOptionId) { this.chosenOptionId = chosenOptionId; }
}
