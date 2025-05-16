package com.quizapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class StudentService {

    private User currentUser;
    private QuizDataService dataService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date lastDailyQuizDate = null; // Track when the last daily quiz was taken/generated

    public StudentService(User currentUser, QuizDataService dataService) {
        this.currentUser = currentUser;
        this.dataService = dataService;
        // Potentially load lastDailyQuizDate for the user if persisted
    }

    public void takeQuiz(Scanner scanner) {
        System.out.println("\n--- Take a Quiz ---");
        System.out.println("Choose quiz type:");
        System.out.println("1. Category Quiz");
        System.out.println("2. Daily Quiz");
        System.out.print("Enter your choice: ");
        String choiceStr = scanner.nextLine();
        int choice;
        try {
            choice = Integer.parseInt(choiceStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (choice == 1) {
            takeCategoryQuiz(scanner);
        } else if (choice == 2) {
            takeDailyQuiz(scanner);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void takeCategoryQuiz(Scanner scanner) {
        System.out.println("--- Category Quiz ---");
        // Display available categories
        List<Category> categories = dataService.getCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories available to choose from.");
            return;
        }
        System.out.println("Available Categories:");
        for (Category cat : categories) {
            System.out.println(cat.getCategoryId() + ". " + cat.getName());
        }
        System.out.print("Enter category ID to take a quiz from: ");
        int categoryId;
        try {
            categoryId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid category ID format.");
            return;
        }
        
        if (categories.stream().noneMatch(c -> c.getCategoryId() == categoryId)){
            System.out.println("Invalid category ID selected.");
            return;
        }

        List<Question> categoryQuestions = dataService.getQuestions().stream()
                .filter(q -> q.getCategoryId() == categoryId && !hasUserAnsweredCorrectly(currentUser.getUserId(), q.getQuestionId()))
                .collect(Collectors.toList());
        
        Collections.shuffle(categoryQuestions); // Randomize questions within the category
        List<Question> quizQuestions = categoryQuestions.stream().limit(5).collect(Collectors.toList()); // Take up to 5 questions

        if (quizQuestions.isEmpty()) {
            System.out.println("No new questions available in this category for you.");
            return;
        }
        presentQuiz(quizQuestions, scanner, "Category Quiz");
    }

    private void takeDailyQuiz(Scanner scanner) {
        System.out.println("--- Daily Quiz ---");
        Date today = new Date();
        String todayStr = dateFormat.format(today);
        String lastQuizDateStr = (lastDailyQuizDate == null) ? null : dateFormat.format(lastDailyQuizDate);

        // Simple check: allow daily quiz once per day based on lastDailyQuizDate
        // In a real app, this state (lastDailyQuizDate per user) should be persisted.
        if (lastQuizDateStr != null && lastQuizDateStr.equals(todayStr)) {
            System.out.println("You have already taken the daily quiz today. Please try again tomorrow.");
            return;
        }

        List<Question> availableQuestions = dataService.getQuestions().stream()
                .filter(q -> !hasUserAnsweredCorrectly(currentUser.getUserId(), q.getQuestionId()))
                .collect(Collectors.toList());

        if (availableQuestions.isEmpty()) {
            System.out.println("No new questions available for a daily quiz at the moment.");
            return;
        }

        Collections.shuffle(availableQuestions); // Shuffle all available unattempted questions
        List<Question> dailyQuizQuestions = availableQuestions.stream().limit(5).collect(Collectors.toList()); // Take up to 5 random questions

        if (dailyQuizQuestions.isEmpty()) {
            System.out.println("Not enough new questions to generate a daily quiz.");
            return;
        }
        presentQuiz(dailyQuizQuestions, scanner, "Daily Quiz");
        lastDailyQuizDate = today; // Update the last daily quiz date for the current session
    }

    private boolean hasUserAnsweredCorrectly(int userId, int questionId) {
        return dataService.getStudentAnswers().stream()
                .anyMatch(sa -> sa.getUserId() == userId && sa.getQuestionId() == questionId /* && sa.isCorrect() - if StudentAnswer stored correctness */);
        // For now, assuming any entry in StudentAnswers means it was answered (and we only store correct ones or all attempts)
        // The prompt says "Already correctly answered questions are excluded". So StudentAnswer should ideally store if it was correct.
        // For this implementation, we'll assume StudentAnswer implies a correct answer for exclusion purposes.
    }

    private void presentQuiz(List<Question> questionsToAsk, Scanner scanner, String quizType) {
        System.out.println("\nStarting " + quizType + " with " + questionsToAsk.size() + " questions.\n");
        int score = 0;
        for (int i = 0; i < questionsToAsk.size(); i++) {
            Question q = questionsToAsk.get(i);
            System.out.println("Question " + (i + 1) + ": " + q.getText());
            List<Option> options = dataService.getOptions().stream()
                                    .filter(opt -> opt.getQuestionId() == q.getQuestionId())
                                    .collect(Collectors.toList());
            Collections.shuffle(options); // Shuffle options for each question

            if (options.size() != 4) {
                System.out.println("Error: Question does not have exactly 4 options. Skipping question.");
                continue;
            }

            for (int j = 0; j < options.size(); j++) {
                System.out.println("  " + (j + 1) + ". " + options.get(j).getOptionText());
            }

            System.out.print("Your answer (enter option number): ");
            int answerChoice;
            try {
                answerChoice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Marked as incorrect.");
                answerChoice = -1; // Invalid choice
            }

            if (answerChoice > 0 && answerChoice <= options.size()) {
                Option selectedOption = options.get(answerChoice - 1);
                if (selectedOption.isCorrect()) {
                    System.out.println("Correct!");
                    score++;
                    // Record the correct answer
                    dataService.addStudentAnswer(new StudentAnswer(currentUser.getUserId(), q.getQuestionId(), new Date()));
                } else {
                    System.out.println("Incorrect. The correct answer was: " + options.stream().filter(Option::isCorrect).findFirst().map(Option::getOptionText).orElse("N/A"));
                }
            } else {
                System.out.println("Invalid option selected. Marked as incorrect.");
                 System.out.println("The correct answer was: " + options.stream().filter(Option::isCorrect).findFirst().map(Option::getOptionText).orElse("N/A"));
            }
            System.out.println(); // Newline for readability
        }
        System.out.println("Quiz finished. Your score: " + score + "/" + questionsToAsk.size());
        currentUser.setTotalPoints(currentUser.getTotalPoints() + score); // Update total points
        dataService.updateUser(currentUser); // Reflect point changes in the central user list
        System.out.println("Your total points are now: " + currentUser.getTotalPoints());
    }

    public void viewDashboard() {
        System.out.println("\n--- Student Dashboard ---");
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Total Points: " + currentUser.getTotalPoints());
        long questionsCorrectlyAnswered = dataService.getStudentAnswers().stream()
                                            .filter(sa -> sa.getUserId() == currentUser.getUserId())
                                            .mapToInt(StudentAnswer::getQuestionId)
                                            .distinct()
                                            .count();
        System.out.println("Unique Questions Correctly Answered: " + questionsCorrectlyAnswered);
        
        // Calculate Rank
        List<User> allStudents = dataService.getUsers().stream()
                                .filter(u -> "STUDENT".equals(u.getRole()))
                                .sorted((s1, s2) -> Integer.compare(s2.getTotalPoints(), s1.getTotalPoints()))
                                .collect(Collectors.toList());
        int rank = -1;
        for(int i=0; i<allStudents.size(); i++){
            if(allStudents.get(i).getUserId() == currentUser.getUserId()){
                rank = i + 1;
                break;
            }
        }
        System.out.println("Your Rank: " + (rank != -1 ? rank : "N/A") + " out of " + allStudents.size() + " students.");
        System.out.println("-----------------------");
    }
}

