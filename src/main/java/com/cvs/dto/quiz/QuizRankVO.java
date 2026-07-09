package com.cvs.dto.quiz;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 排行榜VO
 */
public class QuizRankVO {

    private Long questionId;
    private String title;
    private List<RankItem> rankList;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<RankItem> getRankList() { return rankList; }
    public void setRankList(List<RankItem> rankList) { this.rankList = rankList; }

    public static class RankItem {
        private int rank;
        private String studentName;
        private LocalDateTime submitTime;

        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public LocalDateTime getSubmitTime() { return submitTime; }
        public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    }
}
