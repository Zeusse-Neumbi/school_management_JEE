package com.example.school.dao.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

        // Changed DB name to ensure fresh schema creation
        private static final String URL = "jdbc:sqlite:school_managed.db";

        static {
                try {
                        Class.forName("org.sqlite.JDBC");
                        initDatabase();
                } catch (ClassNotFoundException e) {
                        throw new RuntimeException("SQLite JDBC Driver not found", e);
                }
        }

        public static Connection getConnection() throws SQLException {
                return DriverManager.getConnection(URL);
        }

        private static void initDatabase() {
                try (Connection conn = getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Enable Foreign Keys
                        stmt.execute("PRAGMA foreign_keys = ON;");

                        // 1. Roles
                        stmt.execute("CREATE TABLE IF NOT EXISTS roles (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "role_name TEXT UNIQUE NOT NULL" +
                                        ");");

                        // 2. Users (Email instead of Username)
                        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "email TEXT UNIQUE NOT NULL, " +
                                        "password TEXT NOT NULL, " +
                                        "role_id INTEGER NOT NULL, " +
                                        "first_name TEXT NOT NULL, " +
                                        "last_name TEXT NOT NULL, " +
                                        "FOREIGN KEY(role_id) REFERENCES roles(id)" +
                                        ");");

                        // 3. Students
                        stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "user_id INTEGER UNIQUE NOT NULL, " +
                                        "student_number TEXT UNIQUE NOT NULL, " +
                                        "email TEXT, " +
                                        "date_of_birth TEXT, " +
                                        "FOREIGN KEY(user_id) REFERENCES users(id)" +
                                        ");");

                        // 4. Teachers
                        stmt.execute("CREATE TABLE IF NOT EXISTS teachers (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "user_id INTEGER UNIQUE NOT NULL, " +
                                        "employee_id TEXT UNIQUE NOT NULL, " +
                                        "email TEXT, " +
                                        "specialization TEXT, " +
                                        "FOREIGN KEY(user_id) REFERENCES users(id)" +
                                        ");");

                        // 5. Courses
                        stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "course_name TEXT NOT NULL, " +
                                        "course_code TEXT UNIQUE NOT NULL, " +
                                        "teacher_id INTEGER NOT NULL, " +
                                        "credits INTEGER, " +
                                        "FOREIGN KEY(teacher_id) REFERENCES teachers(id)" +
                                        ");");

                        // 6. Enrollments
                        stmt.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "student_id INTEGER NOT NULL, " +
                                        "course_id INTEGER NOT NULL, " +
                                        "enrollment_date TEXT, " +
                                        "FOREIGN KEY(student_id) REFERENCES students(id), " +
                                        "FOREIGN KEY(course_id) REFERENCES courses(id)" +
                                        ");");

                        // 7. Grades
                        stmt.execute("CREATE TABLE IF NOT EXISTS grades (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "enrollment_id INTEGER NOT NULL, " +
                                        "grade REAL CHECK(grade >= 0 AND grade <= 20), " +
                                        "date_recorded TEXT, " +
                                        "FOREIGN KEY(enrollment_id) REFERENCES enrollments(id)" +
                                        ");");

                        // 8. Attendance
                        stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "enrollment_id INTEGER NOT NULL, " +
                                        "attendance_date TEXT NOT NULL, " +
                                        "status TEXT NOT NULL, " +
                                        "FOREIGN KEY(enrollment_id) REFERENCES enrollments(id)" +
                                        ");");

                        // Seed Roles
                        stmt.execute("INSERT OR IGNORE INTO roles (id, role_name) VALUES (1, 'ADMIN');");
                        stmt.execute("INSERT OR IGNORE INTO roles (id, role_name) VALUES (2, 'TEACHER');");
                        stmt.execute("INSERT OR IGNORE INTO roles (id, role_name) VALUES (3, 'STUDENT');");

                        // Call DataSeeder only if Users table is empty
                        com.example.school.dao.UserDao userDao = new com.example.school.dao.impl.UserDaoSqliteImpl();
                        if (userDao.count() == 0) {
                                new com.example.school.util.DataSeeder().seed();
                        }

                } catch (SQLException e) {
                        throw new RuntimeException("Error initializing database", e);
                }
        }
}
