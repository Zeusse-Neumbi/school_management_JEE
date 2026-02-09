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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.school.util.PasswordUtil;

@WebServlet(name = "StudentServlet", urlPatterns = { "/student/*" })
public class StudentServlet extends HttpServlet {

    private StudentDao studentDao;
    private CourseDao courseDao;
    private EnrollmentDao enrollmentDao;
    private GradeDao gradeDao;
    private AttendanceDao attendanceDao;
    private TeacherDao teacherDao;
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        studentDao = new StudentDaoSqliteImpl();
        courseDao = new CourseDaoSqliteImpl();
        enrollmentDao = new EnrollmentDaoSqliteImpl();
        gradeDao = new GradeDaoSqliteImpl();
        attendanceDao = new AttendanceDaoSqliteImpl();
        teacherDao = new TeacherDaoSqliteImpl();
        userDao = new UserDaoSqliteImpl();
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
        Optional<Student> studentOpt = studentDao.findByUserId(user.getId());
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
        Optional<Student> studentOpt = studentDao.findByUserId(user.getId());
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
            enrollmentDao.delete(student.getId(), courseId);
            resp.sendRedirect(req.getContextPath() + "/student/courses");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        }
    }

    private void handleEnroll(HttpServletRequest req, HttpServletResponse resp, Student student) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            if (!enrollmentDao.isEnrolled(student.getId(), courseId)) {
                Enrollment enrollment = new Enrollment(0, student.getId(), courseId,
                        java.time.LocalDate.now().toString());
                enrollmentDao.save(enrollment);
            }
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

        Optional<User> userOpt = userDao.findById(student.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmail(email);
            if (password != null && !password.trim().isEmpty()) {
                // Hash the password before saving
                String hashedPassword = PasswordUtil.hashPassword(password);
                user.setPassword(hashedPassword);
            }
            userDao.update(user);

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
        List<Course> courses = courseDao.findByStudentId(student.getId());
        req.setAttribute("courseCount", courses.size());

        // Calculate GPA (Mock calculation based on seeded grades)
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(student.getId());
        double totalGrades = 0;
        int gradeCount = 0;
        for (Enrollment e : enrollments) {
            List<Grade> grades = gradeDao.findByEnrollmentId(e.getId());
            for (Grade g : grades) {
                totalGrades += g.getGrade();
                gradeCount++;
            }
        }
        double gpa = gradeCount > 0 ? totalGrades / gradeCount : 0.0;
        req.setAttribute("gpa", String.format("%.2f", gpa));

        // Calculate Attendance Rate
        double attendanceRate = attendanceDao.getAttendanceRate(student.getId());
        req.setAttribute("attendanceRate", String.format("%.0f", attendanceRate));

        req.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(req, resp);
    }

    private void showCourses(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        List<Course> allCourses = courseDao.findAll();
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(student.getId());

        java.util.Set<Integer> enrolledCourseIds = new java.util.HashSet<>();
        for (Enrollment e : enrollments) {
            enrolledCourseIds.add(e.getCourseId());
        }

        List<Course> enrolledCourses = new java.util.ArrayList<>();
        List<Course> availableCourses = new java.util.ArrayList<>();

        Map<Integer, String> teacherNames = new HashMap<>();

        for (Course course : allCourses) {
            if (enrolledCourseIds.contains(course.getId())) {
                enrolledCourses.add(course);
            } else {
                availableCourses.add(course);
            }

            // Fetch teacher name
            teacherDao.findById(course.getTeacherId()).ifPresent(t -> userDao.findById(t.getUserId())
                    .ifPresent(u -> teacherNames.put(course.getId(), u.getFirstName() + " " + u.getLastName())));
        }

        req.setAttribute("enrolledCourses", enrolledCourses);
        req.setAttribute("availableCourses", availableCourses);
        req.setAttribute("teacherNames", teacherNames);
        req.getRequestDispatcher("/WEB-INF/views/student/courses.jsp").forward(req, resp);
    }

    private void showGrades(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(student.getId());
        Map<Integer, Course> courseMap = new HashMap<>();
        Map<Integer, List<Grade>> gradesMap = new HashMap<>();

        for (Enrollment funcEnrollment : enrollments) {
            courseDao.findById(funcEnrollment.getCourseId())
                    .ifPresent(c -> courseMap.put(funcEnrollment.getCourseId(), c));
            List<Grade> grades = gradeDao.findByEnrollmentId(funcEnrollment.getId());
            gradesMap.put(funcEnrollment.getId(), grades);
        }

        req.setAttribute("enrollments", enrollments);
        req.setAttribute("courseMap", courseMap);
        req.setAttribute("gradesMap", gradesMap);
        req.getRequestDispatcher("/WEB-INF/views/student/grades.jsp").forward(req, resp);
    }

    private void showAttendance(HttpServletRequest req, HttpServletResponse resp, Student student)
            throws ServletException, IOException {
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(student.getId());
        Map<Integer, Course> courseMap = new HashMap<>();
        Map<Integer, List<Attendance>> attendanceMap = new HashMap<>();

        for (Enrollment funcEnrollment : enrollments) {
            courseDao.findById(funcEnrollment.getCourseId())
                    .ifPresent(c -> courseMap.put(funcEnrollment.getCourseId(), c));
            List<Attendance> attendances = attendanceDao.findByEnrollmentId(funcEnrollment.getId());
            attendanceMap.put(funcEnrollment.getId(), attendances);
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
