package com.cvs.dto.quiz;

/**
 * 抢名额结果VO
 */
public class QuizGrabVO {
    private boolean success;
    private int grabOrder;
    private String message;

    public QuizGrabVO() {}

    public QuizGrabVO(boolean success, int grabOrder, String message) {
        this.success = success;
        this.grabOrder = grabOrder;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public int getGrabOrder() { return grabOrder; }
    public void setGrabOrder(int grabOrder) { this.grabOrder = grabOrder; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
