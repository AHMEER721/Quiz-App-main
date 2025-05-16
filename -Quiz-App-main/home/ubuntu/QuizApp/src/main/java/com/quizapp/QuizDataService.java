package com.quizapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuizDataService {
    private List<User> users = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<Option> options = new ArrayList<>();
    private List<StudentAnswer> studentAnswers = new ArrayList<>();

    private int nextUserId = 1;
    private int nextCategoryId = 1;
    private int nextQuestionId = 1;
    private int nextOptionId = 1;
    private int nextStudentAnswerId = 1;

    public QuizDataService() {
        // Initialize with some default data for testing
        // Add a default teacher and student
        addUser(new User(0, "Default Teacher", "teacher@quiz.com", "teacher123", "TEACHER", 0));
        addUser(new User(0, "Default Student", "student@quiz.com", "student123", "STUDENT", 0));
        
        // Add some categories
        addCategory(new Category(0, "Science"));
        addCategory(new Category(0, "History"));
    }

    // User methods
    public List<User> getUsers() {
        return users;
    }

    public User getUserById(int userId) {
        return users.stream().filter(u -> u.getUserId() == userId).findFirst().orElse(null);
    }
    
    public User getUserByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    public void addUser(User user) {
        if (user.getUserId() == 0) { // Assign new ID if not set
            user.setUserId(nextUserId++);
        }
        this.users.add(user);
    }

    public void updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == updatedUser.getUserId()) {
                users.set(i, updatedUser);
                return;
            }
        }
    }

    // Category methods
    public List<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        if (category.getCategoryId() == 0) {
            category.setCategoryId(nextCategoryId++);
        }
        this.categories.add(category);
    }

    // Question methods
    public List<Question> getQuestions() {
        return questions;
    }

    public Question getQuestionById(int questionId) {
        return questions.stream().filter(q -> q.getQuestionId() == questionId).findFirst().orElse(null);
    }

    public void addQuestion(Question question) {
        if (question.getQuestionId() == 0) {
            question.setQuestionId(nextQuestionId++);
        }
        this.questions.add(question);
    }
    
    public void removeQuestion(int questionId) {
        questions.removeIf(q -> q.getQuestionId() == questionId);
        options.removeIf(o -> o.getQuestionId() == questionId); // Also remove associated options
        studentAnswers.removeIf(sa -> sa.getQuestionId() == questionId); // And student answers for this question
    }

    // Option methods
    public List<Option> getOptions() {
        return options;
    }
    
    public List<Option> getOptionsByQuestionId(int questionId) {
        return options.stream().filter(o -> o.getQuestionId() == questionId).collect(Collectors.toList());
    }

    public void addOption(Option option) {
        if (option.getOptionId() == 0) {
            option.setOptionId(nextOptionId++);
        }
        this.options.add(option);
    }
    
    public void addOptions(List<Option> newOptions) {
        for(Option opt : newOptions) {
            if (opt.getOptionId() == 0) {
                opt.setOptionId(nextOptionId++);
            }
            this.options.add(opt);
        }
    }
    
    public void removeOptionsByQuestionId(int questionId) {
        options.removeIf(o -> o.getQuestionId() == questionId);
    }

    // StudentAnswer methods
    public List<StudentAnswer> getStudentAnswers() {
        return studentAnswers;
    }

    public void addStudentAnswer(StudentAnswer studentAnswer) {
        if (studentAnswer.getAnswerId() == 0) {
            studentAnswer.setAnswerId(nextStudentAnswerId++);
        }
        this.studentAnswers.add(studentAnswer);
    }

    // Leaderboard logic
    public List<User> getLeaderboard() {
        return users.stream()
                .filter(user -> "STUDENT".equals(user.getRole()))
                .sorted(Comparator.comparingInt(User::getTotalPoints).reversed())
                .collect(Collectors.toList());
    }
}

