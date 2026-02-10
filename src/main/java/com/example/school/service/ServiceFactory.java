package com.example.school.service;

import com.example.school.dao.impl.*;

public class ServiceFactory {

    private static UserService userService;
    private static StudentService studentService;
    private static TeacherService teacherService;
    private static AdminService adminService;

    public static synchronized UserService getUserService() {
        if (userService == null) {
            userService = new UserService(new UserDaoSqliteImpl());
        }
        return userService;
    }

    public static synchronized StudentService getStudentService() {
        if (studentService == null) {
            studentService = new StudentService(
                    new StudentDaoSqliteImpl(),
                    new CourseDaoSqliteImpl(),
                    new EnrollmentDaoSqliteImpl(),
                    new GradeDaoSqliteImpl(),
                    new AttendanceDaoSqliteImpl(),
                    new TeacherDaoSqliteImpl(),
                    new UserDaoSqliteImpl());
        }
        return studentService;
    }

    public static synchronized TeacherService getTeacherService() {
        if (teacherService == null) {
            teacherService = new TeacherService(
                    new TeacherDaoSqliteImpl(),
                    new CourseDaoSqliteImpl(),
                    new EnrollmentDaoSqliteImpl(),
                    new StudentDaoSqliteImpl(),
                    new UserDaoSqliteImpl(),
                    new GradeDaoSqliteImpl(),
                    new AttendanceDaoSqliteImpl());
        }
        return teacherService;
    }

    public static synchronized AdminService getAdminService() {
        if (adminService == null) {
            adminService = new AdminService(
                    new UserDaoSqliteImpl(),
                    new StudentDaoSqliteImpl(),
                    new TeacherDaoSqliteImpl(),
                    new CourseDaoSqliteImpl());
        }
        return adminService;
    }
}
