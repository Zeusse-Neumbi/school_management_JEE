package com.example.school.web.controller;

import com.example.school.model.*;
import com.example.school.service.AdminService;
import com.example.school.service.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "AdminServlet", urlPatterns = { "/admin/*" })
public class AdminServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = ServiceFactory.getAdminService();
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
            User newUser = new User(0,
                    req.getParameter("email"),
                    null,
                    roleId,
                    req.getParameter("firstName"),
                    req.getParameter("lastName"));

            String password = req.getParameter("password");

            java.util.Map<String, String> roleData = new java.util.HashMap<>();
            if (roleId == 3) { // Student
                roleData.put("studentNumber", req.getParameter("studentNumber"));
                roleData.put("dateOfBirth", req.getParameter("dateOfBirth"));
            } else if (roleId == 2) { // Teacher
                roleData.put("employeeId", req.getParameter("employeeId"));
                roleData.put("specialization", req.getParameter("specialization"));
            }

            adminService.createUserWithRole(newUser, password, roleData);

        } else if ("update".equals(action)) {
            try {
                int userId = Integer.parseInt(req.getParameter("id"));
                Optional<User> existingUserOpt = adminService.getUserById(userId);

                if (existingUserOpt.isPresent()) {
                    User existingUser = existingUserOpt.get();
                    String newPassword = req.getParameter("password");

                    User updatedUser = new User(
                            userId,
                            req.getParameter("email"),
                            existingUser.getPassword(),
                            Integer.parseInt(req.getParameter("roleId")),
                            req.getParameter("firstName"),
                            req.getParameter("lastName"));

                    adminService.updateUser(updatedUser, newPassword);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            adminService.deleteUser(id);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private void handleStudentAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {
        // Creation handled via User creation
        if ("create".equals(action)) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        } else if ("update".equals(action)) {
            try {
                Student student = new Student(
                        Integer.parseInt(req.getParameter("id")),
                        Integer.parseInt(req.getParameter("userId")),
                        req.getParameter("studentNumber"),
                        req.getParameter("email"),
                        req.getParameter("dateOfBirth"));
                adminService.updateStudent(student);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                adminService.deleteStudent(id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
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
            try {
                Teacher teacher = new Teacher(
                        Integer.parseInt(req.getParameter("id")),
                        Integer.parseInt(req.getParameter("userId")),
                        req.getParameter("employeeId"),
                        req.getParameter("email"),
                        req.getParameter("specialization"));
                adminService.updateTeacher(teacher);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                adminService.deleteTeacher(id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/teachers");
    }

    private void handleCourseAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {
        try {
            if ("create".equals(action)) {
                Course course = new Course(0,
                        req.getParameter("courseName"),
                        req.getParameter("courseCode"),
                        Integer.parseInt(req.getParameter("teacherId")),
                        Integer.parseInt(req.getParameter("credits")));
                adminService.createCourse(course);
            } else if ("update".equals(action)) {
                Course course = new Course(
                        Integer.parseInt(req.getParameter("id")),
                        req.getParameter("courseName"),
                        req.getParameter("courseCode"),
                        Integer.parseInt(req.getParameter("teacherId")),
                        Integer.parseInt(req.getParameter("credits")));
                adminService.updateCourse(course);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                adminService.deleteCourse(id);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
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

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/dashboard";
        }

        boolean isAdmin = (user.getRoleId() == 1);
        boolean isImpersonating = session.getAttribute("impersonating") != null
                && (Boolean) session.getAttribute("impersonating");

        if (!isAdmin) {
            // If not admin, we only allow access if impersonating AND path is
            // stop-impersonate
            if (!isImpersonating || !"/stop-impersonate".equals(path)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }
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

        try {
            int userId = Integer.parseInt(userIdStr);
            Optional<User> targetUserOpt = adminService.getUserById(userId);

            if (targetUserOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            User targetUser = targetUserOpt.get();

            req.setAttribute("targetUser", targetUser);

            if (targetUser.getRoleId() == 2) { // Teacher
                adminService.getTeacherByUserId(userId).ifPresent(t -> {
                    req.setAttribute("targetTeacher", t);
                    req.setAttribute("teacherCourses", adminService.getTeacherCourses(t.getId()));
                });
            } else if (targetUser.getRoleId() == 3) { // Student
                adminService.getStudentByUserId(userId).ifPresent(s -> {
                    req.setAttribute("targetStudent", s);
                    req.setAttribute("studentCourses", adminService.getStudentCourses(s.getId()));
                });
            }

            req.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        java.util.Map<String, Integer> stats = adminService.getDashboardStats();
        req.setAttribute("userCount", stats.get("userCount"));
        req.setAttribute("studentCount", stats.get("studentCount"));
        req.setAttribute("teacherCount", stats.get("teacherCount"));
        req.setAttribute("courseCount", stats.get("courseCount"));
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

        List<User> users = adminService.searchUsers(search, page, pageSize);
        int totalUsers = adminService.countUsers(search);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        req.setAttribute("users", users);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);

        java.util.Map<Integer, Student> studentMap = new java.util.HashMap<>();
        for (Student s : adminService.getAllStudents()) {
            studentMap.put(s.getUserId(), s);
        }
        req.setAttribute("studentMap", studentMap);

        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        for (Teacher t : adminService.getAllTeachers()) {
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

        List<Student> students = adminService.searchStudents(search, page, pageSize);
        int totalStudents = adminService.countStudents(search);
        int totalPages = (int) Math.ceil((double) totalStudents / pageSize);

        List<User> users = adminService.getAllUsers();
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

        List<Teacher> teachers = adminService.searchTeachers(search, page, pageSize);
        int totalTeachers = adminService.countTeachers(search);
        int totalPages = (int) Math.ceil((double) totalTeachers / pageSize);

        List<User> users = adminService.getAllUsers();
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
        req.setAttribute("courses", adminService.getAllCourses());
        List<Teacher> teachers = adminService.getAllTeachers();
        req.setAttribute("teachers", teachers);

        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        for (Teacher t : teachers) {
            teacherMap.put(t.getId(), t);
        }
        req.setAttribute("teacherMap", teacherMap);

        List<User> users = adminService.getAllUsers();
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (User u : users) {
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

        try {
            int targetUserId = Integer.parseInt(userIdStr);
            Optional<User> targetUserOpt = adminService.getUserById(targetUserId);

            if (targetUserOpt.isPresent() && originalAdmin.getRoleId() == 1) {
                User targetUser = targetUserOpt.get();

                session.setAttribute("originalAdmin", originalAdmin);
                session.setAttribute("user", targetUser);
                session.setAttribute("impersonating", true);

                if (targetUser.getRoleId() == 3) { // Student
                    resp.sendRedirect(req.getContextPath() + "/student/dashboard");
                } else if (targetUser.getRoleId() == 2) { // Teacher
                    resp.sendRedirect(req.getContextPath() + "/teacher/dashboard");
                } else {
                    session.removeAttribute("originalAdmin");
                    session.removeAttribute("impersonating");
                    session.setAttribute("user", originalAdmin);
                    resp.sendRedirect(req.getContextPath() + "/admin/users");
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            }
        } catch (NumberFormatException e) {
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
