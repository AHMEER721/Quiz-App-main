package com.quizapp;

import java.util.Scanner;

public class AuthenticationService {

    private static final String TEACHER_ROLE = "TEACHER";
    private static final String STUDENT_ROLE = "STUDENT";

    // In a real application, this would involve a database and proper password hashing.
    // For simplicity, we'll use a mock user store here.
    private static final User MOCK_TEACHER = new User(1, "Teacher User", "teacher@example.com", "password123", TEACHER_ROLE, 0);
    private static final User MOCK_STUDENT = new User(2, "Student User", "student@example.com", "password456", STUDENT_ROLE, 0);

    public User login(String email, String password) {
        // Simulate database lookup and password verification
        if (MOCK_TEACHER.getEmail().equals(email) && MOCK_TEACHER.getPasswordHash().equals(password)) {
            return MOCK_TEACHER;
        }
        if (MOCK_STUDENT.getEmail().equals(email) && MOCK_STUDENT.getPasswordHash().equals(password)) {
            return MOCK_STUDENT;
        }
        return null; // Login failed
    }

    public boolean isTeacher(User user) {
        return user != null && TEACHER_ROLE.equals(user.getRole());
    }

    public boolean isStudent(User user) {
        return user != null && STUDENT_ROLE.equals(user.getRole());
    }
}

