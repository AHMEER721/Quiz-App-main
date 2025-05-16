package com.quizapp;

import java.util.Date;

public class StudentAnswer {
    private int answerId; // Assuming a primary key for this table
    private int userId;
    private int questionId;
    private Date answeredAt;

    // Constructors
    public StudentAnswer() {
    }

    public StudentAnswer(int userId, int questionId, Date answeredAt) {
        this.userId = userId;
        this.questionId = questionId;
        this.answeredAt = answeredAt;
    }

    // Getters and Setters
    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Date getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Date answeredAt) {
        this.answeredAt = answeredAt;
    }

    @Override
    public String toString() {
        return "StudentAnswer{" +
                "answerId=" + answerId +
                ", userId=" + userId +
                ", questionId=" + questionId +
                ", answeredAt=" + answeredAt +
                '}';
    }
}

