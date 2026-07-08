package com.cvs.dto;

import java.util.List;

/**
 * 投票学生情况视图对象
 * 用于展示已投票和未投票的学生列表
 */
public class VoteStudentVO {

    private int totalStudents;
    private int votedCount;
    private int notVotedCount;
    private List<StudentInfo> votedStudents;
    private List<StudentInfo> notVotedStudents;

    public static class StudentInfo {
        private Long id;
        private String realName;
        private String username;
        /** 已投票学生的选项文本（未投票学生无此项） */
        private String selectedOption;

        public static StudentInfo voted(Long id, String realName, String username, String selectedOption) {
            StudentInfo info = new StudentInfo();
            info.setId(id);
            info.setRealName(realName);
            info.setUsername(username);
            info.setSelectedOption(selectedOption);
            return info;
        }

        public static StudentInfo notVoted(Long id, String realName, String username) {
            StudentInfo info = new StudentInfo();
            info.setId(id);
            info.setRealName(realName);
            info.setUsername(username);
            return info;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getSelectedOption() { return selectedOption; }
        public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    public int getVotedCount() { return votedCount; }
    public void setVotedCount(int votedCount) { this.votedCount = votedCount; }
    public int getNotVotedCount() { return notVotedCount; }
    public void setNotVotedCount(int notVotedCount) { this.notVotedCount = notVotedCount; }
    public List<StudentInfo> getVotedStudents() { return votedStudents; }
    public void setVotedStudents(List<StudentInfo> votedStudents) { this.votedStudents = votedStudents; }
    public List<StudentInfo> getNotVotedStudents() { return notVotedStudents; }
    public void setNotVotedStudents(List<StudentInfo> notVotedStudents) { this.notVotedStudents = notVotedStudents; }
}
