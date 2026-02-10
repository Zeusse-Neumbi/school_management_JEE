package com.example.school.web.controller;

import com.example.school.model.*;
import com.example.school.service.ServiceFactory;
import com.example.school.service.StudentService;
import com.example.school.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "StudentServlet", urlPatterns = { "/student/*" })
public class StudentServlet extends HttpServlet {

    private StudentService studentService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        studentService = ServiceFactory.getStudentService();
        userService = ServiceFactory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check if this is an admin impersonating a student
        Boolean impersonating = (Boolean) session.getAttribute("impersonating");
        if (user.getRoleId() != 3 && (impersonating == null || !impersonating)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        // Fetch Student Entity
        Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
        if (studentOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Student profile not found.");
            return;
        }
        Student student = studentOpt.get();
        req.setAttribute("student", student);

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/dashboard";
        }

        switch (path) {
            case "/dashboard":
                showDashboard(req, resp, student);
                break;
            case "/courses":
                showCourses(req, resp, student);
                break;
            case "/grades":
                showGrades(req, resp, student);
                break;
            case "/attendance":
                showAttendance(req, resp, student);
                break;
            case "/profile":
                showProfile(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Fetch Student Entity
        Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
        if (studentOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Student profile not found.");
            return;
        }
        Student student = studentOpt.get();

        String path = req.getPathInfo();
        if ("/enroll".equals(path)) {
            handleEnroll(req, resp, student);
        } else if ("/profile".equals(path)) {
            handleProfileUpdate(req, resp, student);
        } else if ("/unenroll".equals(path)) {
            handleUnenroll(req, resp, student);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleUnenroll(HttpServletRequest req, HttpServletResponse resp, Student student) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            studentService.unenroll(student.getId(), courseId);
            resp.sendRedirect(req.getContextPath() + "/student/courses");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        }
    }

    private void handleEnroll(HttpServletRequest req, HttpServletResponse resp, Student student) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            studentService.enroll(student.getId(), courseId);
            resp.sendRedirect(req.getContextPath() + "/student/courses");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        }
    }

    private void handleProfileUpdate(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Email cannot be empty");
            showProfile(req, resp);
            return;
        }

        Optional<User> userOpt = userService.findById(student.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userService.updateProfile(user, email, password);

            // Update session
            req.getSession().setAttribute("user", user);

            req.setAttribute("success", "Profile updated successfully");
        } else {
            req.setAttribute("error", "User not found");
        }
        showProfile(req, resp);
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        req.setAttribute("courseCount", studentService.getCourseCount(student.getId()));

        double gpa = studentService.calculateGPA(student.getId());
        req.setAttribute("gpa", String.format("%.2f", gpa));

        double attendanceRate = studentService.getAttendanceRate(student.getId());
        req.setAttribute("attendanceRate", String.format("%.0f", attendanceRate));

        req.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(req, resp);
    }

    private void showCourses(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());
        List<Course> availableCourses = studentService.getAvailableCourses(student.getId());

        Map<Integer, String> teacherNames = new HashMap<>();

        // Combine both lists to fetch teacher names efficiently (or just loop both)
        for (Course c : enrolledCourses) {
            teacherNames.put(c.getId(), studentService.getTeacherName(c.getId()));
        }
        for (Course c : availableCourses) {
            teacherNames.put(c.getId(), studentService.getTeacherName(c.getId()));
        }

        req.setAttribute("enrolledCourses", enrolledCourses);
        req.setAttribute("availableCourses", availableCourses);
        req.setAttribute("teacherNames", teacherNames);
        req.getRequestDispatcher("/WEB-INF/views/student/courses.jsp").forward(req, resp);
    }

    private void showGrades(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        List<Enrollment> enrollments = studentService.getEnrollments(student.getId());
        Map<Integer, Course> courseMap = new HashMap<>();
        Map<Integer, List<Grade>> gradesMap = new HashMap<>();

        for (Enrollment funcEnrollment : enrollments) {
            Course course = studentService.getCourse(funcEnrollment.getCourseId());
            if (course != null) {
                courseMap.put(funcEnrollment.getCourseId(), course);
            }
            // Utilizing the service to get grades would be cleaner, but we already have a
            // bulk Access method?
            // studentService.getGrades(studentId) returns Map<EnrollmentId, List<Grade>>
            // Let's use that instead of looping here if possible, but the view expects
            // specific structure.
            // For now, let's stick to the existing logic but use Service where possible or
            // keep it simple.
            // Actually, studentService.getGrades returns the map we need!
            // But we also need the enrollments list for the iteration in JSP.
        }

        // Re-fetching efficient maps
        gradesMap = studentService.getGrades(student.getId());

        // We still need courseMap
        for (Enrollment e : enrollments) {
            Course course = studentService.getCourse(e.getCourseId());
            if (course != null) {
                courseMap.put(e.getCourseId(), course);
            }
        }

        req.setAttribute("enrollments", enrollments);
        req.setAttribute("courseMap", courseMap);
        req.setAttribute("gradesMap", gradesMap);
        req.getRequestDispatcher("/WEB-INF/views/student/grades.jsp").forward(req, resp);
    }

    private void showAttendance(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        List<Enrollment> enrollments = studentService.getEnrollments(student.getId());
        Map<Integer, Course> courseMap = new HashMap<>();
        Map<Integer, List<Attendance>> attendanceMap = new HashMap<>();

        attendanceMap = studentService.getAttendance(student.getId());

        for (Enrollment funcEnrollment : enrollments) {
            Course course = studentService.getCourse(funcEnrollment.getCourseId());
            if (course != null) {
                courseMap.put(funcEnrollment.getCourseId(), course);
            }
        }

        req.setAttribute("enrollments", enrollments);
        req.setAttribute("courseMap", courseMap);
        req.setAttribute("attendanceMap", attendanceMap);
        req.getRequestDispatcher("/WEB-INF/views/student/attendance.jsp").forward(req, resp);
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
    }
}
