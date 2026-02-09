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
import java.util.List;
import java.util.Optional;

@WebServlet(name = "TeacherServlet", urlPatterns = { "/teacher/*" })
public class TeacherServlet extends HttpServlet {

    private TeacherDao teacherDao;
    private CourseDao courseDao;
    private EnrollmentDao enrollmentDao;

    @Override
    public void init() throws ServletException {
        teacherDao = new TeacherDaoSqliteImpl();
        courseDao = new CourseDaoSqliteImpl();
        enrollmentDao = new EnrollmentDaoSqliteImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check if this is an admin impersonating a teacher
        Boolean impersonating = (Boolean) session.getAttribute("impersonating");
        if (user.getRoleId() != 2 && (impersonating == null || !impersonating)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        // Fetch Teacher Entity
        Optional<Teacher> teacherOpt = teacherDao.findByUserId(user.getId());
        if (teacherOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Teacher profile not found.");
            return;
        }
        Teacher teacher = teacherOpt.get();
        req.setAttribute("teacher", teacher);

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/dashboard";
        }

        switch (path) {
            case "/dashboard":
                showDashboard(req, resp, teacher);
                break;
            case "/courses":
                showCourses(req, resp, teacher);
                break;
            case "/grades":
                showGradesManagement(req, resp, teacher);
                break;
            case "/attendance":
                showAttendanceManagement(req, resp, teacher);
                break;
            case "/profile":
                showProfile(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = courseDao.findByTeacherId(teacher.getId());
        req.setAttribute("courseCount", courses.size());

        int totalStudents = 0;
        for (Course c : courses) {
            List<Enrollment> enrollments = enrollmentDao.findByCourseId(c.getId());
            totalStudents += enrollments.size();
        }
        req.setAttribute("studentCount", totalStudents);

        req.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(req, resp);
    }

    private void showCourses(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = courseDao.findByTeacherId(teacher.getId());
        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/views/teacher/my_courses.jsp").forward(req, resp);
    }

    private void showGradesManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = courseDao.findByTeacherId(teacher.getId());
        req.setAttribute("courses", courses);
        // Logic to fetch students for a selected course could go here if query param
        // present
        req.getRequestDispatcher("/WEB-INF/views/teacher/grades_management.jsp").forward(req, resp);
    }

    private void showAttendanceManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = courseDao.findByTeacherId(teacher.getId());
        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/views/teacher/attendance_management.jsp").forward(req, resp);
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/teacher/profile.jsp").forward(req, resp);
    }
}
