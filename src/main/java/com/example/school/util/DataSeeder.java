package com.example.school.util;

import com.example.school.dao.db.DatabaseManager;
import net.datafaker.Faker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;

public class DataSeeder {

    private final Faker faker;
    private final Random random;

    public DataSeeder() {
        this.faker = new Faker();
        this.random = new Random();
    }

    public void seed() {
        if (isDatabaseEmpty()) {
            System.out.println("Seeding database...");
            seedRoles();
            seedUsersAndRelatedEntities();
            seedCourses();
            seedEnrollmentsWrappers();
            System.out.println("Database seeding completed.");
        } else {
            System.out.println("Database already contains data. Skipping seeding.");
        }
    }

    private boolean isDatabaseEmpty() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void seedRoles() {
        // Roles are already handled in DatabaseManager.initDatabase(), but we ensure
        // they exist.
        // Assuming IDs: 1=ADMIN, 2=TEACHER, 3=STUDENT
    }

    private void seedUsersAndRelatedEntities() {
        String insertUser = "INSERT INTO users (email, password, role_id, first_name, last_name) VALUES (?, ?, ?, ?, ?)";
        String insertStudent = "INSERT INTO students (user_id, student_number, email, date_of_birth) VALUES (?, ?, ?, ?)";
        String insertTeacher = "INSERT INTO teachers (user_id, employee_id, email, specialization) VALUES (?, ?, ?, ?)";

        String defaultPass = PasswordUtil.hashPassword("password123");
        String adminPass = PasswordUtil.hashPassword("admin123");

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmtUser = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstmtStudent = conn.prepareStatement(insertStudent);
                PreparedStatement pstmtTeacher = conn.prepareStatement(insertTeacher)) {

            // 1. Create Admin
            pstmtUser.setString(1, "admin@school.com");
            pstmtUser.setString(2, adminPass);
            pstmtUser.setInt(3, 1); // ADMIN
            pstmtUser.setString(4, "Super");
            pstmtUser.setString(5, "Admin");
            pstmtUser.executeUpdate();

            // 2. Create 15 Teachers
            java.util.Set<String> usedEmployeeIds = new java.util.HashSet<>();
            for (int i = 0; i < 15; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@school.com";

                pstmtUser.setString(1, email);
                pstmtUser.setString(2, defaultPass);
                pstmtUser.setInt(3, 2); // TEACHER
                pstmtUser.setString(4, firstName);
                pstmtUser.setString(5, lastName);
                pstmtUser.executeUpdate();

                try (java.sql.ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        pstmtTeacher.setInt(1, userId);

                        String empId;
                        do {
                            empId = "EMP" + faker.number().digits(4);
                        } while (usedEmployeeIds.contains(empId));
                        usedEmployeeIds.add(empId);

                        pstmtTeacher.setString(2, empId);
                        pstmtTeacher.setString(3, email);
                        pstmtTeacher.setString(4, faker.educator().course());
                        pstmtTeacher.executeUpdate();
                    }
                }
            }

            // 3. Create 100 Students
            java.util.Set<String> usedStudentNumbers = new java.util.HashSet<>();
            for (int i = 0; i < 100; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@student.school.com";

                pstmtUser.setString(1, email);
                pstmtUser.setString(2, defaultPass);
                pstmtUser.setInt(3, 3); // STUDENT
                pstmtUser.setString(4, firstName);
                pstmtUser.setString(5, lastName);
                pstmtUser.executeUpdate();

                try (java.sql.ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        pstmtStudent.setInt(1, userId);

                        String studentNumber;
                        do {
                            studentNumber = "24G" + faker.number().numberBetween(1000, 9999);
                        } while (usedStudentNumbers.contains(studentNumber));
                        usedStudentNumbers.add(studentNumber);

                        pstmtStudent.setString(2, studentNumber);
                        pstmtStudent.setString(3, email);

                        LocalDate dob = faker.date().birthday(18, 25).toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        pstmtStudent.setString(4, dob.toString());

                        pstmtStudent.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedCourses() {
        String sql = "INSERT INTO courses (course_name, course_code, teacher_id, credits) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Get Teacher IDs
            java.util.List<Integer> teacherIds = getIds("teachers");
            if (teacherIds.isEmpty())
                return;

            for (int i = 0; i < 12; i++) {
                pstmt.setString(1, faker.educator().course());
                pstmt.setString(2, "CRS" + faker.number().digits(3));
                pstmt.setInt(3, teacherIds.get(random.nextInt(teacherIds.size())));
                pstmt.setInt(4, faker.number().numberBetween(2, 6)); // Credits
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedEnrollmentsWrappers() {
        // Enrolling students in random courses
        String insertEnrollment = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
        String insertGrade = "INSERT INTO grades (enrollment_id, grade, date_recorded) VALUES (?, ?, ?)";
        // Attendance seeding could go here too

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmtEnroll = conn.prepareStatement(insertEnrollment,
                        Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstmtGrade = conn.prepareStatement(insertGrade)) {

            java.util.List<Integer> studentIds = getIds("students");
            java.util.List<Integer> courseIds = getIds("courses");

            if (studentIds.isEmpty() || courseIds.isEmpty())
                return;

            // Enroll each student in 3 to 6 courses
            for (int studentId : studentIds) {
                int coursesCount = random.nextInt(4) + 3; // 3-6 courses
                java.util.Set<Integer> assignedCourses = new java.util.HashSet<>();
                while (assignedCourses.size() < coursesCount) {
                    int courseId = courseIds.get(random.nextInt(courseIds.size()));

                    if (assignedCourses.contains(courseId)) {
                        continue;
                    }
                    assignedCourses.add(courseId);

                    try {
                        pstmtEnroll.setInt(1, studentId);
                        pstmtEnroll.setInt(2, courseId);
                        pstmtEnroll.setString(3, LocalDate.now().minusMonths(random.nextInt(5)).toString());
                        pstmtEnroll.executeUpdate();

                        // Add Grade
                        try (java.sql.ResultSet rs = pstmtEnroll.getGeneratedKeys()) {
                            if (rs.next()) {
                                int enrollmentId = rs.getInt(1);

                                // Seed Grades
                                pstmtGrade.setInt(1, enrollmentId);
                                pstmtGrade.setInt(2, random.nextInt(21));
                                pstmtGrade.setString(3, LocalDate.now().toString());
                                pstmtGrade.executeUpdate();

                                // Seed Attendance (Random 80% Present, 20% Absent/Late)
                                String attendanceSql = "INSERT INTO attendance (enrollment_id, attendance_date, status) VALUES (?, ?, ?)";
                                try (PreparedStatement pstmtAttendance = conn.prepareStatement(attendanceSql)) {
                                    for (int k = 0; k < 5; k++) { // 5 attendance records per enrollment
                                        pstmtAttendance.setInt(1, enrollmentId);
                                        pstmtAttendance.setString(2, LocalDate.now().minusDays(k * 7).toString()); // Weekly
                                        String status = random.nextDouble() > 0.2 ? "PRESENT"
                                                : (random.nextBoolean() ? "ABSENT" : "LATE");
                                        pstmtAttendance.setString(3, status);
                                        pstmtAttendance.executeUpdate();
                                    }
                                }
                            }
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private java.util.List<Integer> getIds(String tableName) {
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        String sql = "SELECT id FROM " + tableName;
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
