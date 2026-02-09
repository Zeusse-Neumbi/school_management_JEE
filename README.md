# School Management System

A comprehensive, MVC-based Java Web Application for managing school operations, including user management, course enrollment, grading, and attendance tracking. Built with modern Jakarta EE technologies and a SQLite database.

## 🚀 Features

### **Admin Portal**
*   **Dashboard**: Overview of total users, students, teachers, and courses.
*   **User Management**: create, update, and delete users (Admin, Teacher, Student).
*   **Course Management**: Create and manage courses, assign teachers to courses.
*   **Impersonation**: specialized feature allowing Admins to "log in" as any Student or Teacher to view the system from their perspective.

### **Teacher Portal**
*   **Dashboard**: View assigned courses and total student count.
*   **Course Management**: View details of assigned courses and enrolled students.
*   **Grading System**: Enter and update student grades for specific enrollments.
*   **Attendance Tracking**: Mark and update student attendance (Present, Absent, Late) for specific dates.
*   **Profile**: Update personal account details.

### **Student Portal**
*   **Dashboard**: View GPA, Attendance Rate, and enrolled user summary.
*   **Course Enrollment**: Browse available courses and self-enroll or unenroll.
*   **Academic Records**: View grades and attendance history for all enrolled courses.
*   **Profile**: Update personal account details.

---

## 🛠 Tech Stack

*   **Language**: Java 17
*   **Frameworks**: Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL 3.0)
*   **Database**: SQLite (via `sqlite-jdbc`)
*   **Build Tool**: Maven
*   **Server**: Jetty 12 (Embedded via Maven Plugin)
*   **Utilities**:
    *   `jbcrypt`: For secure password hashing.
    *   `datafaker`: For generating realistic seed data.

---

## 📂 Project Structure

```
src/main/java/com/example/school
├── dao             # Data Access Objects (Interfaces & Implementations)
├── model           # Entity Classes (User, Student, Course, etc.)
├── util            # Utilities (DataSeeder, PasswordUtil)
├── web/controller  # Servlets (AdminServlet, StudentServlet, etc.)

src/main/webapp
├── WEB-INF/views   # JSP Views (Organized by role: admin, teacher, student)
├── assets          # Static resources (CSS, JS)
```

---

## 💾 Database Schema

The application uses a relational SQLite database (`school.db`) with the following tables:

*   `roles`: User roles (Admin, Teacher, Student).
*   `users`: Base user accounts (email, password, name).
*   `students`: specific student data (student number, DOB), linked to `users`.
*   `teachers`: Specific teacher data (employee ID, specialization), linked to `users`.
*   `courses`: Course details, linked to a `teacher`.
*   `enrollments`: Links `students` to `courses`.
*   `grades`: Academic performance records linked to `enrollments`.
*   `attendance`: Daily attendance records linked to `enrollments`.

---

## ⚡ Setup & Installation

### Prerequisites
*   JDK 17 or higher
*   Maven 3.6+

### Steps

1.  **Clone the Repository**
    ```bash
    git clone <repository-url>
    cd school-management-system
    ```

2.  **Build the Project**
    ```bash
    mvn clean install
    ```

3.  **Run the Application**
    ```bash
    mvn jetty:run
    ```

4.  **Access the App**
    Open your browser to: [http://localhost:9091/](http://localhost:9091/)

---

## 🔐 Default Credentials (Seeded Data)

On the first run, the application automatically seeds the database with sample data using `DataSeeder`.

### **Administrator**
*   **Email**: `admin@school.com`
*   **Password**: `admin123`

### **Teachers** (15 Generated)
*   **Email Pattern**: `firstname.lastname@school.com`
*   **Password**: `password123`
*   *Example*: Check the `users` table or database logs for generated names.

### **Students** (100 Generated)
*   **Email Pattern**: `firstname.lastname@student.school.com`
*   **Password**: `password123`
*   *Example*: Check the `users` table or database logs for generated names.

> **Note**: If you need to reset the database, simply delete the `school.db` file in the project root and restart the application.

---

## 📜 License

This project is licensed under the MIT License.
