package com.example.school.service;

import com.example.school.dao.*;
import com.example.school.model.*;
import com.example.school.util.PasswordUtil;
import java.util.*;

public class AdminService {

    private final UserDao userDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final CourseDao courseDao;

    public AdminService(UserDao userDao, StudentDao studentDao, TeacherDao teacherDao, CourseDao courseDao) {
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.courseDao = courseDao;
    }

    // Dashboard Stats
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("userCount", userDao.findAll().size()); // Ideally use count() directly if available
        stats.put("studentCount", studentDao.findAll().size());
        stats.put("teacherCount", teacherDao.findAll().size());
        stats.put("courseCount", courseDao.findAll().size());
        return stats;
    }

    // User Management
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public List<User> searchUsers(String query, int page, int pageSize) {
        if (query != null && !query.trim().isEmpty()) {
            return userDao.search(query, page, pageSize);
        }
        return userDao.search("", page, pageSize);
    }

    public int countUsers(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return userDao.count(query);
        }
        return userDao.count();
    }

    public Optional<User> getUserById(int id) {
        return userDao.findById(id);
    }

    public void createUser(User user, String password) {
        if (password != null && !password.isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        userDao.save(user);
    }

    // Complex create with role-specific logic (Student/Teacher)
    public int createUserWithRole(User user, String password, Map<String, String> roleData) {
        if (password != null && !password.isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        int newUserId = userDao.save(user);

        if (newUserId != -1) {
            int roleId = user.getRoleId();
            if (roleId == 3) { // Student
                Student student = new Student(0, newUserId,
                        roleData.get("studentNumber"),
                        user.getEmail(),
                        roleData.get("dateOfBirth"));
                studentDao.save(student);
            } else if (roleId == 2) { // Teacher
                Teacher teacher = new Teacher(0, newUserId,
                        roleData.get("employeeId"),
                        user.getEmail(),
                        roleData.get("specialization"));
                teacherDao.save(teacher);
            }
        }
        return newUserId;
    }

    public void updateUser(User user, String password) {
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        userDao.update(user);
        // Cascading email updates could be handled here if needed, but simpler to keep
        // independent or handle via UI calls
    }

    public void deleteUser(int userId) {
        studentDao.deleteByUserId(userId);
        teacherDao.deleteByUserId(userId);
        userDao.delete(userId);
    }

    // Student Management
    public List<Student> searchStudents(String query, int page, int pageSize) {
        if (query != null && !query.trim().isEmpty()) {
            return studentDao.search(query, page, pageSize);
        }
        return studentDao.search("", page, pageSize);
    }

    public int countStudents(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return studentDao.count(query);
        }
        return studentDao.count("");
    }

    public void updateStudent(Student student) {
        studentDao.update(student);
    }

    public void deleteStudent(int id) {
        studentDao.delete(id);
    }

    public Optional<Student> getStudentByUserId(int userId) {
        return studentDao.findByUserId(userId);
    }

    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    // Teacher Management
    public List<Teacher> searchTeachers(String query, int page, int pageSize) {
        if (query != null && !query.trim().isEmpty()) {
            return teacherDao.search(query, page, pageSize);
        }
        return teacherDao.search("", page, pageSize);
    }

    public int countTeachers(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return teacherDao.count(query);
        }
        return teacherDao.count("");
    }

    public void updateTeacher(Teacher teacher) {
        teacherDao.update(teacher);
    }

    public void deleteTeacher(int id) {
        teacherDao.delete(id);
    }

    public Optional<Teacher> getTeacherByUserId(int userId) {
        return teacherDao.findByUserId(userId);
    }

    public List<Teacher> getAllTeachers() {
        return teacherDao.findAll();
    }

    // Course Management
    public List<Course> getAllCourses() {
        return courseDao.findAll();
    }

    public List<Course> getStudentCourses(int studentId) {
        return courseDao.findByStudentId(studentId);
    }

    public List<Course> getTeacherCourses(int teacherId) {
        return courseDao.findByTeacherId(teacherId);
    }

    public void createCourse(Course course) {
        courseDao.save(course);
    }

    public void updateCourse(Course course) {
        courseDao.update(course);
    }

    public void deleteCourse(int id) {
        courseDao.delete(id);
    }
}
