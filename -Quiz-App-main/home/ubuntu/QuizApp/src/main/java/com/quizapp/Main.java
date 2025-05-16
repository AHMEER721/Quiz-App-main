package com.quizapp;

import java.util.Scanner;
import java.util.List;

public class Main {
    private static QuizDataService dataService = new QuizDataService();
    private static AuthenticationService authService = new AuthenticationService(dataService);
    private static TeacherService teacherService = new TeacherService(dataService.getUsers());
    private static StudentService studentService; // Will be initialized after student login
    private static User currentUser = null;

    public static void main(String[] args) {
        // Pass the shared data to teacher service
        teacherService.setQuestions(dataService.getQuestions());
        teacherService.setOptions(dataService.getOptions());

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Quiz Application!");

        while (currentUser == null) {
            System.out.println("\nPlease login:");
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            currentUser = authService.login(email, password);

            if (currentUser == null) {
                System.out.println("Login failed. Please try again or type 'exit' to quit.");
                System.out.print("Command: ");
                if(scanner.nextLine().equalsIgnoreCase("exit")){
                    System.out.println("Exiting application.");
                    scanner.close();
                    return;
                }
            } else {
                System.out.println("Login successful. Welcome, " + currentUser.getName() + "! Role: " + currentUser.getRole());
                if (authService.isStudent(currentUser)) {
                    studentService = new StudentService(currentUser, dataService);
                }
            }
        }

        // Main application loop
        String command = "";
        while (!command.equalsIgnoreCase("logout") && !command.equalsIgnoreCase("exit")) {
            if (authService.isTeacher(currentUser)) {
                showTeacherMenu();
            } else if (authService.isStudent(currentUser)) {
                showStudentMenu();
            }
            System.out.print("Enter command: ");
            command = scanner.nextLine();
            processCommand(command, scanner);
        }

        System.out.println("Logging out. Thank you for using the Quiz Application!");
        scanner.close();
    }

    private static void showTeacherMenu() {
        System.out.println("\n--- Teacher Menu ---");
        System.out.println("1. Add Question");
        System.out.println("2. View Questions");
        System.out.println("3. Edit Question");
        System.out.println("4. Delete Question");
        System.out.println("5. View Student Profiles");
        System.out.println("6. View Leaderboard");
        System.out.println("logout - Log out");
        System.out.println("exit - Exit application");
    }

    private static void showStudentMenu() {
        System.out.println("\n--- Student Menu ---");
        System.out.println("1. Take Quiz");
        System.out.println("2. View Dashboard");
        System.out.println("3. View Leaderboard");
        System.out.println("logout - Log out");
        System.out.println("exit - Exit application");
    }

    private static void processCommand(String command, Scanner scanner) {
        if (authService.isTeacher(currentUser)) {
            switch (command.toLowerCase()) {
                case "1":
                case "add question":
                    teacherService.addQuestion(scanner, currentUser.getUserId());
                    break;
                case "2":
                case "view questions":
                    teacherService.viewQuestions();
                    break;
                case "3":
                case "edit question":
                    teacherService.editQuestion(scanner);
                    break;
                case "4":
                case "delete question":
                    teacherService.deleteQuestion(scanner);
                    break;
                case "5":
                case "view student profiles":
                    teacherService.viewStudentProfiles();
                    break;
                case "6":
                case "view leaderboard":
                    viewLeaderboard();
                    break;
                case "logout":
                case "exit":
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        } else if (authService.isStudent(currentUser)) {
            switch (command.toLowerCase()) {
                case "1":
                case "take quiz":
                    studentService.takeQuiz(scanner);
                    break;
                case "2":
                case "view dashboard":
                    studentService.viewDashboard();
                    break;
                case "3":
                case "view leaderboard":
                    viewLeaderboard();
                    break;
                case "logout":
                case "exit":
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private static void viewLeaderboard() {
        System.out.println("\n--- Leaderboard ---");
        List<User> leaderboard = dataService.getLeaderboard();
        if (leaderboard.isEmpty()) {
            System.out.println("No students on the leaderboard yet.");
            return;
        }
        for (int i = 0; i < leaderboard.size(); i++) {
            User student = leaderboard.get(i);
            System.out.println((i + 1) + ". " + student.getName() + " - Points: " + student.getTotalPoints());
        }
        System.out.println("-------------------");
    }
}

