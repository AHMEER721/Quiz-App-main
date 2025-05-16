package com.quizapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TeacherService {

    private List<Question> questions = new ArrayList<>(); // In-memory store for questions
    private List<Option> options = new ArrayList<>(); // In-memory store for options
    private List<User> users; // To view student profiles
    private int nextQuestionId = 1;
    private int nextOptionId = 1;

    public TeacherService(List<User> users) {
        this.users = users;
    }
    
    // Method to allow adding questions from a central source, e.g. Main app
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        if (!questions.isEmpty()) {
            this.nextQuestionId = questions.stream().mapToInt(Question::getQuestionId).max().orElse(0) + 1;
        }
    }

    public void setOptions(List<Option> options) {
        this.options = options;
        if (!options.isEmpty()) {
            this.nextOptionId = options.stream().mapToInt(Option::getOptionId).max().orElse(0) + 1;
        }
    }

    public void addQuestion(Scanner scanner, int teacherId) {
        System.out.println("--- Add New Question ---");
        System.out.println("Enter question text:");
        String text = scanner.nextLine();
        System.out.println("Enter category ID (e.g., 1 for Science, 2 for History):");
        int categoryId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter difficulty (easy, medium, hard):");
        String difficulty = scanner.nextLine().toLowerCase();
        while (!difficulty.equals("easy") && !difficulty.equals("medium") && !difficulty.equals("hard")) {
            System.out.println("Invalid difficulty. Please enter easy, medium, or hard:");
            difficulty = scanner.nextLine().toLowerCase();
        }

        Question newQuestion = new Question();
        int currentQuestionId = nextQuestionId++;
        newQuestion.setQuestionId(currentQuestionId);
        newQuestion.setText(text);
        newQuestion.setCategoryId(categoryId);
        newQuestion.setDifficulty(difficulty);
        newQuestion.setTeacherId(teacherId); // Set the actual teacher ID
        newQuestion.setCreatedAt(new Date());
        newQuestion.setUpdatedAt(new Date());

        List<Option> questionOptions = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            System.out.println("Enter text for option " + i + ":");
            String optionText = scanner.nextLine();
            System.out.println("Is option " + i + " correct? (yes/no):");
            boolean isCorrect = scanner.nextLine().equalsIgnoreCase("yes");
            Option option = new Option(nextOptionId++, currentQuestionId, optionText, isCorrect);
            questionOptions.add(option);
        }
        
        // Validate exactly one correct answer
        long correctAnswersCount = questionOptions.stream().filter(Option::isCorrect).count();
        if (correctAnswersCount != 1) {
            System.out.println("Error: Exactly one option must be correct. Question not added.");
            nextQuestionId--; // Rollback ID increment if question not added
            // Rollback option IDs if necessary, though less critical for in-memory
            nextOptionId -= 4;
            return;
        }

        questions.add(newQuestion);
        options.addAll(questionOptions);
        System.out.println("Question and options added successfully.");
    }

    public void viewQuestions() {
        System.out.println("--- All Questions ---");
        if (questions.isEmpty()) {
            System.out.println("No questions available.");
            return;
        }
        for (Question q : questions) {
            System.out.println("ID: " + q.getQuestionId() + ", Text: " + q.getText() + ", CategoryID: " + q.getCategoryId() + ", Difficulty: " + q.getDifficulty());
            List<Option> qOptions = options.stream().filter(opt -> opt.getQuestionId() == q.getQuestionId()).collect(Collectors.toList());
            for(Option opt : qOptions) {
                System.out.println("  Option ID: " + opt.getOptionId() + ", Text: " + opt.getOptionText() + (opt.isCorrect() ? " (Correct)" : ""));
            }
        }
    }

    public void editQuestion(Scanner scanner) {
        System.out.println("--- Edit Question ---");
        System.out.println("Enter ID of question to edit:");
        int id = Integer.parseInt(scanner.nextLine());
        Question questionToEdit = findQuestionById(id);

        if (questionToEdit == null) {
            System.out.println("Question not found.");
            return;
        }

        System.out.println("Editing Question ID: " + questionToEdit.getQuestionId());
        System.out.println("Current Text: " + questionToEdit.getText() + ". Enter new text or press Enter to keep current:");
        String newText = scanner.nextLine();
        if (!newText.isEmpty()) {
            questionToEdit.setText(newText);
        }

        System.out.println("Current Category ID: " + questionToEdit.getCategoryId() + ". Enter new category ID or press Enter to keep current:");
        String newCategoryIdStr = scanner.nextLine();
        if (!newCategoryIdStr.isEmpty()) {
            questionToEdit.setCategoryId(Integer.parseInt(newCategoryIdStr));
        }

        System.out.println("Current Difficulty: " + questionToEdit.getDifficulty() + ". Enter new difficulty (easy, medium, hard) or press Enter to keep current:");
        String newDifficulty = scanner.nextLine();
        if (!newDifficulty.isEmpty()) {
            questionToEdit.setDifficulty(newDifficulty);
        }
        
        // Edit options
        List<Option> currentOptions = options.stream().filter(opt -> opt.getQuestionId() == id).collect(Collectors.toList());
        System.out.println("Current Options:");
        for(int i=0; i<currentOptions.size(); i++){
            Option opt = currentOptions.get(i);
            System.out.println((i+1) + ". " + opt.getOptionText() + (opt.isCorrect() ? " (Correct)" : ""));
        }
        System.out.println("Do you want to edit options? (yes/no)");
        if(scanner.nextLine().equalsIgnoreCase("yes")){
            List<Option> newOptionsList = new ArrayList<>();
            for(int i=0; i<4; i++){
                 System.out.println("Enter new text for option " + (i+1) + " (current: " + (i < currentOptions.size() ? currentOptions.get(i).getOptionText() : "N/A") + ") or press Enter to keep:");
                 String optText = scanner.nextLine();
                 System.out.println("Is this option correct? (yes/no) (current: "+ (i < currentOptions.size() ? (currentOptions.get(i).isCorrect() ? "yes" : "no") : "N/A") + ") or press Enter to keep:");
                 String optCorrectStr = scanner.nextLine();

                 String finalText = (optText.isEmpty() && i < currentOptions.size()) ? currentOptions.get(i).getOptionText() : optText;
                 boolean finalCorrect = (optCorrectStr.isEmpty() && i < currentOptions.size()) ? currentOptions.get(i).isCorrect() : optCorrectStr.equalsIgnoreCase("yes");
                 
                 if(i < currentOptions.size()){
                    currentOptions.get(i).setOptionText(finalText);
                    currentOptions.get(i).setCorrect(finalCorrect);
                    newOptionsList.add(currentOptions.get(i));
                 } else { // Should not happen if we always have 4 options
                    Option newOpt = new Option(nextOptionId++, id, finalText, finalCorrect);
                    newOptionsList.add(newOpt);
                 }
            }
            long correctAnswersCount = newOptionsList.stream().filter(Option::isCorrect).count();
            if (correctAnswersCount != 1) {
                System.out.println("Error: Exactly one option must be correct. Options not updated.");
            } else {
                // Remove old options and add new/updated ones
                options.removeIf(opt -> opt.getQuestionId() == id);
                options.addAll(newOptionsList);
                System.out.println("Options updated successfully.");
            }
        }

        questionToEdit.setUpdatedAt(new Date());
        System.out.println("Question updated successfully.");
    }

    public void deleteQuestion(Scanner scanner) {
        System.out.println("--- Delete Question ---");
        System.out.println("Enter ID of question to delete:");
        int id = Integer.parseInt(scanner.nextLine());
        Question questionToRemove = findQuestionById(id);

        if (questionToRemove != null) {
            questions.remove(questionToRemove);
            options.removeIf(opt -> opt.getQuestionId() == id); // Also remove associated options
            System.out.println("Question and its options deleted successfully.");
        } else {
            System.out.println("Question not found.");
        }
    }

    private Question findQuestionById(int id) {
        return questions.stream().filter(q -> q.getQuestionId() == id).findFirst().orElse(null);
    }

    public void viewStudentProfiles() {
        System.out.println("--- Student Profiles ---");
        List<User> students = users.stream().filter(u -> "STUDENT".equals(u.getRole())).collect(Collectors.toList());
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        for (User student : students) {
            // In a real app, answered questions count would come from StudentAnswer table
            System.out.println("Student ID: " + student.getUserId() + ", Name: " + student.getName() + ", Email: " + student.getEmail() + ", Total Points: " + student.getTotalPoints());
            // To show questions answered, we'd need access to StudentAnswer data, which is not directly in TeacherService yet.
            // This part would require more complex data retrieval.
            System.out.println("  (Detailed answered questions list not shown here for brevity)"); 
        }
    }
}

