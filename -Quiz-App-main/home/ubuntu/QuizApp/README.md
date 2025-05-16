# Quiz Application

This is a console-based Quiz Application written in Java, built with Maven.

## Prerequisites

- Java Development Kit (JDK) version 8 or higher.
- Apache Maven.

## Project Structure

- `pom.xml`: Maven project configuration file.
- `src/main/java/com/quizapp/`: Contains all the Java source code for the application.
    - `Main.java`: The main entry point of the application.
    - `User.java`, `Category.java`, `Question.java`, `Option.java`, `StudentAnswer.java`: Model/Entity classes.
    - `AuthenticationService.java`: Handles user login and role verification.
    - `TeacherService.java`: Contains logic for teacher-specific functionalities.
    - `StudentService.java`: Contains logic for student-specific functionalities.
    - `QuizDataService.java`: In-memory data store and service layer for managing quiz data (users, questions, etc.).

## How to Compile and Run

1.  **Navigate to the project root directory** (the directory containing `pom.xml`, which is `QuizApp`):
    ```bash
    cd path/to/QuizApp
    ```

2.  **Compile the project and create an executable JAR using Maven**:
    This command will compile the source code, run any tests (if configured), and package the application into a single executable JAR file in the `target` directory. The H2 database dependency will be included in this JAR.
    ```bash
    mvn clean package
    ```

3.  **Run the application**:
    After the build is successful, you can run the application using the following command from the project root directory:
    ```bash
    java -jar target/QuizApp-1.0-SNAPSHOT.jar
    ```

## Default Users for Testing

The application is initialized with the following default users for demonstration purposes. You can log in with these credentials:

-   **Teacher**:
    -   Email: `teacher@quiz.com`
    -   Password: `teacher123`
-   **Student**:
    -   Email: `student@quiz.com`
    -   Password: `student123`

## Features

-   **Authentication**: Users log in with email and password.
-   **Roles**: Teacher and Student roles with different functionalities.
-   **Teacher Features**:
    -   Add Multiple Choice Questions (MCQs) with 4 options (exactly one correct).
    -   Assign category and difficulty (easy, medium, hard) to questions.
    -   View, edit, and delete questions.
    -   View a list of students and their profiles (name, email, total points).
-   **Student Features**:
    -   Take quizzes by selecting a category or a daily random quiz.
    -   Earn points for each correct answer.
    -   View a personal dashboard: total points, number of correctly answered questions, and rank.
-   **Quiz Logic**:
    -   Daily quiz: A new set of random questions (up to 5) is available each day (simulated by first login of the day for the current session).
    -   Category quiz: Random questions (up to 5) from a selected category.
    -   Already correctly answered questions are excluded from future quizzes for that student.
-   **Leaderboard**: Ranking of students by total points, visible to both teachers and students.

## Notes

-   The application uses an in-memory data store (`QuizDataService.java`). This means all data (users, questions, scores) will be reset when the application restarts.
-   The "Daily Quiz" feature is simulated for the current session. For a persistent daily quiz that resets at midnight for all users, a more robust scheduling mechanism and persistent storage for the last quiz date per user would be required.
-   Password hashing is not implemented for simplicity; passwords are stored as plain text in the mock user data. In a real-world application, secure password hashing (e.g., bcrypt) is crucial.

