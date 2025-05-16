package com.quizapp;

import java.util.Date;

public class Question {
    private int questionId;
    private String text;
    private int categoryId;
    private String difficulty; // "easy", "medium", "hard"
    private int teacherId;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Question() {
    }

    public Question(int questionId, String text, int categoryId, String difficulty, int teacherId, Date createdAt, Date updatedAt) {
        this.questionId = questionId;
        this.text = text;
        this.categoryId = categoryId;
        this.difficulty = difficulty;
        this.teacherId = teacherId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", text=\'" + text + "\'" +
                ", categoryId=" + categoryId +
                ", difficulty=\'" + difficulty + "\'" +
                ", teacherId=" + teacherId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

