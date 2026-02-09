package com.example.school.web.controller;

import com.example.school.dao.*;
import com.example.school.dao.impl.*;
import com.example.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "AdminServlet", urlPatterns = { "/admin/*" })
public class AdminServlet extends HttpServlet {

    private UserDao userDao;
    private StudentDao studentDao;
    private TeacherDao teacherDao;
    private CourseDao courseDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoSqliteImpl();
        studentDao = new StudentDaoSqliteImpl();
        teacherDao = new TeacherDaoSqliteImpl();
        courseDao = new CourseDaoSqliteImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || user.getRoleId() != 1) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String path = req.getPathInfo();
        if (path == null)
            path = "/";

        String action = req.getParameter("action");

        switch (path) {
            case "/users":
                handleUserAction(req, resp, action);
                break;
            case "/students":
                handleStudentAction(req, resp, action);
                break;
            case "/teachers":
                handleTeacherAction(req, resp, action);
                break;
            case "/courses":
                handleCourseAction(req, resp, action);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleUserAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action)) {
            int roleId = Integer.parseInt(req.getParameter("roleId"));
            String rawPassword = req.getParameter("password");
            String hashedPassword = com.example.school.util.PasswordUtil.hashPassword(rawPassword);

            User newUser = new User(0,
                    req.getParameter("email"),
                    hashedPassword,
                    roleId,
                    req.getParameter("firstName"),
                    req.getParameter("lastName"));
            int newUserId = userDao.save(newUser);

            if (newUserId != -1) {
                if (roleId == 3) { // Student
                    Student student = new Student(0,
                            newUserId,
                            req.getParameter("studentNumber"),
                            req.getParameter("email"),
                            req.getParameter("dateOfBirth"));
                    studentDao.save(student);
                } else if (roleId == 2) { // Teacher
                    Teacher teacher = new Teacher(0,
                            newUserId,
                            req.getParameter("employeeId"),
                            req.getParameter("email"),
                            req.getParameter("specialization"));
                    teacherDao.save(teacher);
                }
            }
        } else if ("update".equals(action)) {
            try {
                int userId = Integer.parseInt(req.getParameter("id"));
                java.util.Optional<User> existingUserOpt = userDao.findById(userId);

                if (existingUserOpt.isPresent()) {
                    User existingUser = existingUserOpt.get();
                    String newPassword = req.getParameter("password");
                    String passwordToSave = existingUser.getPassword();

                    if (newPassword != null && !newPassword.trim().isEmpty()) {
                        passwordToSave = com.example.school.util.PasswordUtil.hashPassword(newPassword);
                    }

                    User updatedUser = new User(
                            userId,
                            req.getParameter("email"),
                            passwordToSave,
                            Integer.parseInt(req.getParameter("roleId")),
                            req.getParameter("firstName"),
                            req.getParameter("lastName"));
                    userDao.update(updatedUser);
                }
            } catch (NumberFormatException e) {
                // Log error or ignore
                e.printStackTrace();
            }
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            // Cascade delete: remove linked Student or Teacher first
            studentDao.deleteByUserId(id);
            teacherDao.deleteByUserId(id);
            userDao.delete(id);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private void handleStudentAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {
        // Creation handled via User creation
        if ("create".equals(action)) {
            // Redirect or error since we don't create students directly anymore
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        } else if ("update".equals(action)) {
            Student student = new Student(
                    Integer.parseInt(req.getParameter("id")),
                    Integer.parseInt(req.getParameter("userId")),
                    req.getParameter("studentNumber"),
                    req.getParameter("email"),
                    req.getParameter("dateOfBirth"));
            studentDao.update(student);
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            studentDao.delete(id);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/students");
    }

    private void handleTeacherAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {
        // Creation handled via User creation
        if ("create".equals(action)) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        } else if ("update".equals(action)) {
            Teacher teacher = new Teacher(
                    Integer.parseInt(req.getParameter("id")),
                    Integer.parseInt(req.getParameter("userId")),
                    req.getParameter("employeeId"),
                    req.getParameter("email"),
                    req.getParameter("specialization"));
            teacherDao.update(teacher);
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            teacherDao.delete(id);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/teachers");
    }

    private void handleCourseAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {
        if ("create".equals(action)) {
            Course course = new Course(0,
                    req.getParameter("courseName"),
                    req.getParameter("courseCode"),
                    Integer.parseInt(req.getParameter("teacherId")),
                    Integer.parseInt(req.getParameter("credits")));
            courseDao.save(course);
        } else if ("update".equals(action)) {
            Course course = new Course(
                    Integer.parseInt(req.getParameter("id")),
                    req.getParameter("courseName"),
                    req.getParameter("courseCode"),
                    Integer.parseInt(req.getParameter("teacherId")),
                    Integer.parseInt(req.getParameter("credits")));
            courseDao.update(course);
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            courseDao.delete(id);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/courses");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (user.getRoleId() != 1) { // 1 is Admin
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/dashboard";
        }

        switch (path) {
            case "/dashboard":
                showDashboard(req, resp);
                break;
            case "/users":
                showUsers(req, resp);
                break;
            case "/students":
                showStudents(req, resp);
                break;
            case "/teachers":
                showTeachers(req, resp);
                break;
            case "/courses":
                showCourses(req, resp);
                break;
            case "/profile":
                showProfile(req, resp);
                break;
            case "/impersonate":
                startImpersonation(req, resp);
                break;
            case "/stop-impersonate":
                stopImpersonation(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdStr = req.getParameter("userId");
        if (userIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        int userId = Integer.parseInt(userIdStr);
        User targetUser = null;
        for (User u : userDao.findAll()) {
            if (u.getId() == userId) {
                targetUser = u;
                break;
            }
        }

        if (targetUser == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        req.setAttribute("targetUser", targetUser);

        if (targetUser.getRoleId() == 2) { // Teacher
            teacherDao.findByUserId(userId).ifPresent(t -> {
                req.setAttribute("targetTeacher", t);
                req.setAttribute("teacherCourses", courseDao.findByTeacherId(t.getId()));
            });
        } else if (targetUser.getRoleId() == 3) { // Student
            studentDao.findByUserId(userId).ifPresent(s -> {
                req.setAttribute("targetStudent", s);
                req.setAttribute("studentCourses", courseDao.findByStudentId(s.getId()));
            });
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(req, resp);
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("userCount", userDao.findAll().size());
        req.setAttribute("studentCount", studentDao.findAll().size());
        req.setAttribute("teacherCount", teacherDao.findAll().size());
        req.setAttribute("courseCount", courseDao.findAll().size());
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void showUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        String pageStr = req.getParameter("page");
        int page = 1;

        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        int pageSize = 10;

        java.util.List<User> users;
        int totalUsers;

        if (search != null && !search.trim().isEmpty()) {
            users = userDao.search(search, page, pageSize);
            totalUsers = userDao.count(search);
        } else {
            users = userDao.search("", page, pageSize);
            totalUsers = userDao.count();
        }

        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        req.setAttribute("users", users);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);

        // Add student and teacher maps for edit modal population
        java.util.Map<Integer, Student> studentMap = new java.util.HashMap<>();
        for (Student s : studentDao.findAll()) {
            studentMap.put(s.getUserId(), s);
        }
        req.setAttribute("studentMap", studentMap);

        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        for (Teacher t : teacherDao.findAll()) {
            teacherMap.put(t.getUserId(), t);
        }
        req.setAttribute("teacherMap", teacherMap);

        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    private void showStudents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        String pageStr = req.getParameter("page");
        int page = 1;

        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        int pageSize = 10;

        java.util.List<Student> students;
        int totalStudents;

        if (search != null && !search.trim().isEmpty()) {
            students = studentDao.search(search, page, pageSize);
            totalStudents = studentDao.count(search);
        } else {
            students = studentDao.search("", page, pageSize);
            totalStudents = studentDao.count("");
        }

        int totalPages = (int) Math.ceil((double) totalStudents / pageSize);

        java.util.List<User> users = userDao.findAll();
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (User u : users) {
            userMap.put(u.getId(), u);
        }

        req.setAttribute("students", students);
        req.setAttribute("userMap", userMap);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/students.jsp").forward(req, resp);
    }

    private void showTeachers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        String pageStr = req.getParameter("page");
        int page = 1;

        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        int pageSize = 10;

        java.util.List<Teacher> teachers;
        int totalTeachers;

        if (search != null && !search.trim().isEmpty()) {
            teachers = teacherDao.search(search, page, pageSize);
            totalTeachers = teacherDao.count(search);
        } else {
            teachers = teacherDao.search("", page, pageSize);
            totalTeachers = teacherDao.count("");
        }

        int totalPages = (int) Math.ceil((double) totalTeachers / pageSize);

        java.util.List<User> users = userDao.findAll();
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (User u : users) {
            userMap.put(u.getId(), u);
        }

        req.setAttribute("teachers", teachers);
        req.setAttribute("userMap", userMap);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/teachers.jsp").forward(req, resp);
    }

    private void showCourses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("courses", courseDao.findAll());
        java.util.List<Teacher> teachers = teacherDao.findAll();
        req.setAttribute("teachers", teachers);

        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        for (Teacher t : teachers) {
            teacherMap.put(t.getId(), t);
        }
        req.setAttribute("teacherMap", teacherMap);

        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (User u : userDao.findAll()) {
            userMap.put(u.getId(), u);
        }
        req.setAttribute("userMap", userMap);

        req.getRequestDispatcher("/WEB-INF/views/admin/courses.jsp").forward(req, resp);
    }

    private void startImpersonation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User originalAdmin = (User) session.getAttribute("user");

        String userIdStr = req.getParameter("userId");
        if (userIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        int targetUserId = Integer.parseInt(userIdStr);
        java.util.Optional<User> targetUserOpt = userDao.findById(targetUserId);

        if (targetUserOpt.isPresent() && originalAdmin.getRoleId() == 1) {
            User targetUser = targetUserOpt.get();

            // Store original admin for restoration later
            session.setAttribute("originalAdmin", originalAdmin);
            session.setAttribute("user", targetUser);
            session.setAttribute("impersonating", true);

            // Redirect to appropriate dashboard based on target role
            if (targetUser.getRoleId() == 3) { // Student
                resp.sendRedirect(req.getContextPath() + "/student/dashboard");
            } else if (targetUser.getRoleId() == 2) { // Teacher
                resp.sendRedirect(req.getContextPath() + "/teacher/dashboard");
            } else {
                // Cannot impersonate another admin, go back
                session.removeAttribute("originalAdmin");
                session.removeAttribute("impersonating");
                session.setAttribute("user", originalAdmin);
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void stopImpersonation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User originalAdmin = (User) session.getAttribute("originalAdmin");

        if (originalAdmin != null) {
            session.setAttribute("user", originalAdmin);
            session.removeAttribute("originalAdmin");
            session.removeAttribute("impersonating");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
