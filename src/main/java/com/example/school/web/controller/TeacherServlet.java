package com.example.school.web.controller;

import com.example.school.model.*;
import com.example.school.service.ServiceFactory;
import com.example.school.service.TeacherService;
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

    private TeacherService teacherService;

    @Override
    public void init() throws ServletException {
        teacherService = ServiceFactory.getTeacherService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null)
            path = "/";

        switch (path) {
            case "/grades":
                handleGradeSubmission(req, resp);
                break;
            case "/attendance":
                handleAttendanceSubmission(req, resp);
                break;
            case "/profile":
                handleProfileUpdate(req, resp);
                break;
            default:
                processRequest(req, resp);
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        Optional<Teacher> teacherOpt = teacherService.getTeacherByUserId(user.getId());
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
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courseCount", courses.size());
        req.setAttribute("studentCount", teacherService.getStudentCountForTeacher(teacher.getId()));

        req.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(req, resp);
    }

    private void showCourses(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);
        req.setAttribute("studentCountMap", teacherService.getStudentCountsPerCourse(courses));

        req.getRequestDispatcher("/WEB-INF/views/teacher/my_courses.jsp").forward(req, resp);
    }

    private void showGradesManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                List<java.util.Map<String, Object>> students = teacherService.getCourseStudentsData(courseId, null);
                req.setAttribute("students", students);
                req.setAttribute("gradesMap", teacherService.getGradesForEnrollments(students));

            } catch (NumberFormatException e) {
                // ignore
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/grades_management.jsp").forward(req, resp);
    }

    private void showAttendanceManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        String dateParam = req.getParameter("date");
        String searchQuery = req.getParameter("search");

        if (dateParam == null || dateParam.isEmpty()) {
            dateParam = java.time.LocalDate.now().toString();
        }
        req.setAttribute("currentDate", dateParam);

        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                List<java.util.Map<String, Object>> allStudents = teacherService.getCourseStudentsData(courseId,
                        searchQuery);

                for (java.util.Map<String, Object> map : allStudents) {
                    int enrollmentId = (Integer) map.get("enrollmentId");
                    teacherService.getAttendance(enrollmentId, dateParam).ifPresent(att -> {
                        map.put("status", att.getStatus());
                    });
                }

                req.setAttribute("students", allStudents);
                req.setAttribute("totalPages", 1);
                req.setAttribute("currentPage", 1);
                req.setAttribute("searchQuery", searchQuery);

            } catch (NumberFormatException e) {
                // ignore
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/attendance_management.jsp").forward(req, resp);
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/teacher/profile.jsp").forward(req, resp);
    }

    // POST Handlers

    private void handleGradeSubmission(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            int enrollmentId = Integer.parseInt(req.getParameter("enrollmentId"));
            double gradeVal = Double.parseDouble(req.getParameter("grade"));

            teacherService.updateGrade(enrollmentId, gradeVal);

            String referrer = req.getHeader("referer");
            resp.sendRedirect(referrer != null ? referrer : req.getContextPath() + "/teacher/grades");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Grade Data");
        }
    }

    private void handleAttendanceSubmission(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String date = req.getParameter("date");
        if (date == null || date.isEmpty()) {
            date = java.time.LocalDate.now().toString();
        }

        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam != null) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                List<java.util.Map<String, Object>> students = teacherService.getCourseStudentsData(courseId, null);

                for (java.util.Map<String, Object> map : students) {
                    int enrollmentId = (Integer) map.get("enrollmentId");
                    String status = req.getParameter("status_" + enrollmentId);
                    if (status != null) {
                        teacherService.updateAttendance(enrollmentId, date, status);
                    }
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        resp.sendRedirect(
                req.getContextPath() + "/teacher/attendance?courseId=" + courseIdParam + "&date=" + date);
    }

    private void handleProfileUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                Optional<Teacher> teacherOpt = teacherService.getTeacherByUserId(user.getId());
                if (teacherOpt.isPresent()) {
                    Teacher teacher = teacherOpt.get();
                    String password = req.getParameter("password");
                    String email = req.getParameter("email");

                    teacherService.updateProfile(user, teacher, email, password);
                    session.setAttribute("user", user);
                }
            }
        }
        resp.sendRedirect(req.getContextPath() + "/teacher/profile?success=true");
    }

}
