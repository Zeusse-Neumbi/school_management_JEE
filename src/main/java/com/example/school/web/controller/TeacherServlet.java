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
    private StudentDao studentDao;
    private UserDao userDao;
    private GradeDao gradeDao;
    private AttendanceDao attendanceDao;

    @Override
    public void init() throws ServletException {
        teacherDao = new TeacherDaoSqliteImpl();
        courseDao = new CourseDaoSqliteImpl();
        enrollmentDao = new EnrollmentDaoSqliteImpl();
        studentDao = new StudentDaoSqliteImpl();
        userDao = new UserDaoSqliteImpl();
        gradeDao = new GradeDaoSqliteImpl();
        attendanceDao = new AttendanceDaoSqliteImpl();
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

        java.util.Map<Integer, Integer> studentCountMap = new java.util.HashMap<>();
        for (Course c : courses) {
            List<Enrollment> enrollments = enrollmentDao.findByCourseId(c.getId());
            studentCountMap.put(c.getId(), enrollments.size());
        }
        req.setAttribute("studentCountMap", studentCountMap);

        req.getRequestDispatcher("/WEB-INF/views/teacher/my_courses.jsp").forward(req, resp);
    }

    private void showGradesManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = courseDao.findByTeacherId(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
                java.util.List<java.util.Map<String, Object>> students = new java.util.ArrayList<>();

                java.util.Map<Integer, Double> gradesMap = new java.util.HashMap<>();

                for (Enrollment e : enrollments) {
                    Optional<Student> sOpt = studentDao.findById(e.getStudentId());
                    if (sOpt.isPresent()) {
                        Student s = sOpt.get();
                        Optional<User> uOpt = userDao.findById(s.getUserId());
                        if (uOpt.isPresent()) {
                            java.util.Map<String, Object> map = new java.util.HashMap<>();
                            map.put("student", s);
                            map.put("user", uOpt.get());
                            map.put("enrollmentId", e.getId());
                            students.add(map);

                            // Fetch grade
                            List<Grade> gList = gradeDao.findByEnrollmentId(e.getId());
                            if (!gList.isEmpty()) {
                                gradesMap.put(e.getId(), gList.get(0).getGrade());
                            }
                        }
                    }
                }
                req.setAttribute("students", students);
                req.setAttribute("gradesMap", gradesMap);

            } catch (NumberFormatException e) {
                // ignore
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/grades_management.jsp").forward(req, resp);
    }

    private void showAttendanceManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws ServletException, IOException {
        List<Course> courses = courseDao.findByTeacherId(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        String dateParam = req.getParameter("date");
        String searchQuery = req.getParameter("search");
        String pageParam = req.getParameter("page");

        if (dateParam == null || dateParam.isEmpty()) {
            dateParam = java.time.LocalDate.now().toString();
        }
        req.setAttribute("currentDate", dateParam);

        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                // Fetch all enrollments first (naive filtering/pagination in memory for SQLite
                // simplicity)
                // In a real app, do this in SQL
                List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);

                java.util.List<java.util.Map<String, Object>> allStudents = new java.util.ArrayList<>();

                for (Enrollment e : enrollments) {
                    Optional<Student> sOpt = studentDao.findById(e.getStudentId());
                    if (sOpt.isPresent()) {
                        Student s = sOpt.get();
                        Optional<User> uOpt = userDao.findById(s.getUserId());
                        if (uOpt.isPresent()) {
                            User u = uOpt.get();

                            // Filter by search query
                            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                                String q = searchQuery.toLowerCase();
                                if (!s.getStudentNumber().toLowerCase().contains(q) &&
                                        !u.getFirstName().toLowerCase().contains(q) &&
                                        !u.getLastName().toLowerCase().contains(q)) {
                                    continue;
                                }
                            }

                            java.util.Map<String, Object> map = new java.util.HashMap<>();
                            map.put("student", s);
                            map.put("user", u);
                            map.put("enrollmentId", e.getId());

                            // Fetch existing attendance status
                            Optional<Attendance> attOpt = attendanceDao.findByEnrollmentIdAndDate(e.getId(), dateParam);
                            if (attOpt.isPresent()) {
                                map.put("status", attOpt.get().getStatus());
                            }

                            allStudents.add(map);
                        }
                    }
                }

                // Pagination
                int pageSize = 1000000;
                int page = 1;
                if (pageParam != null && !pageParam.isEmpty()) {
                    try {
                        page = Integer.parseInt(pageParam);
                    } catch (NumberFormatException e) {
                    }
                }

                int totalStudents = allStudents.size();
                int totalPages = (int) Math.ceil((double) totalStudents / pageSize);

                int start = (page - 1) * pageSize;
                int end = Math.min(start + pageSize, totalStudents);

                List<java.util.Map<String, Object>> pagedStudents;
                if (start < totalStudents) {
                    pagedStudents = allStudents.subList(start, end);
                } else {
                    pagedStudents = new java.util.ArrayList<>();
                }

                req.setAttribute("students", pagedStudents);
                req.setAttribute("totalPages", totalPages);
                req.setAttribute("currentPage", page);
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
        // Expects: enrollmentId, grade
        try {
            int enrollmentId = Integer.parseInt(req.getParameter("enrollmentId"));
            double gradeVal = Double.parseDouble(req.getParameter("grade"));

            // Check if grade exists
            List<Grade> existingGrades = gradeDao.findByEnrollmentId(enrollmentId);
            if (!existingGrades.isEmpty()) {
                // Update existing (assuming 1 grade per enrollment for now as discussed)
                Grade grade = existingGrades.get(0);
                grade.setGrade(gradeVal);
                grade.setDateRecorded(java.time.LocalDate.now().toString());
                gradeDao.update(grade);
            } else {
                // Create new
                Grade grade = new Grade();
                grade.setEnrollmentId(enrollmentId);
                grade.setGrade(gradeVal);
                grade.setDateRecorded(java.time.LocalDate.now().toString());
                gradeDao.save(grade);
            }

            // Redirect back to grades management with success
            String referrer = req.getHeader("referer");
            resp.sendRedirect(referrer != null ? referrer : req.getContextPath() + "/teacher/grades");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Grade Data");
        }
    }

    private void handleAttendanceSubmission(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        // Expects: date, and a list of status_<enrollmentId>
        String date = req.getParameter("date");
        if (date == null || date.isEmpty()) {
            date = java.time.LocalDate.now().toString();
        }

        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam != null) {
            int courseId = Integer.parseInt(courseIdParam);
            List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
            for (Enrollment e : enrollments) {
                String status = req.getParameter("status_" + e.getId());
                if (status != null) {
                    Optional<Attendance> existing = attendanceDao.findByEnrollmentIdAndDate(e.getId(), date);
                    if (existing.isPresent()) {
                        Attendance attendance = existing.get();
                        attendance.setStatus(status);
                        attendanceDao.update(attendance);
                    } else {
                        Attendance attendance = new Attendance();
                        attendance.setEnrollmentId(e.getId());
                        attendance.setAttendanceDate(date);
                        attendance.setStatus(status);
                        attendanceDao.save(attendance);
                    }
                }
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
                String password = req.getParameter("password");
                String email = req.getParameter("email");

                if (email != null && !email.trim().isEmpty()) {
                    user.setEmail(email);
                }

                if (password != null && !password.trim().isEmpty()) {
                    // Hash the password before saving
                    String hashedPassword = com.example.school.util.PasswordUtil.hashPassword(password);
                    user.setPassword(hashedPassword);
                }

                userDao.update(user);

                Optional<Teacher> teacherOpt = teacherDao.findByUserId(user.getId());
                if (teacherOpt.isPresent()) {
                    Teacher teacher = teacherOpt.get();
                    if (email != null && !email.trim().isEmpty()) {
                        teacher.setEmail(email);
                        teacherDao.update(teacher);
                    }
                }

                session.setAttribute("user", user);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/teacher/profile?success=true");
    }

}
