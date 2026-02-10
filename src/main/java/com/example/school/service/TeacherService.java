package com.example.school.service;

import com.example.school.dao.*;
import com.example.school.model.*;
import com.example.school.util.PasswordUtil;
import java.time.LocalDate;
import java.util.*;

public class TeacherService {

    private final TeacherDao teacherDao;
    private final CourseDao courseDao;
    private final EnrollmentDao enrollmentDao;
    private final StudentDao studentDao;
    private final UserDao userDao;
    private final GradeDao gradeDao;
    private final AttendanceDao attendanceDao;

    public TeacherService(TeacherDao teacherDao, CourseDao courseDao, EnrollmentDao enrollmentDao,
            StudentDao studentDao, UserDao userDao, GradeDao gradeDao, AttendanceDao attendanceDao) {
        this.teacherDao = teacherDao;
        this.courseDao = courseDao;
        this.enrollmentDao = enrollmentDao;
        this.studentDao = studentDao;
        this.userDao = userDao;
        this.gradeDao = gradeDao;
        this.attendanceDao = attendanceDao;
    }

    public Optional<Teacher> getTeacherByUserId(int userId) {
        return teacherDao.findByUserId(userId);
    }

    public List<Course> getTeacherCourses(int teacherId) {
        return courseDao.findByTeacherId(teacherId);
    }

    public int getStudentCountForTeacher(int teacherId) {
        List<Course> courses = getTeacherCourses(teacherId);
        int totalStudents = 0;
        for (Course c : courses) {
            totalStudents += enrollmentDao.findByCourseId(c.getId()).size();
        }
        return totalStudents;
    }

    public Map<Integer, Integer> getStudentCountsPerCourse(List<Course> courses) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Course c : courses) {
            counts.put(c.getId(), enrollmentDao.findByCourseId(c.getId()).size());
        }
        return counts;
    }

    public List<Map<String, Object>> getCourseStudentsData(int courseId, String searchQuery) {
        List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
        List<Map<String, Object>> studentsData = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Optional<Student> sOpt = studentDao.findById(e.getStudentId());
            if (sOpt.isPresent()) {
                Student s = sOpt.get();
                Optional<User> uOpt = userDao.findById(s.getUserId());
                if (uOpt.isPresent()) {
                    User u = uOpt.get();

                    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                        String q = searchQuery.toLowerCase();
                        if (!s.getStudentNumber().toLowerCase().contains(q) &&
                                !u.getFirstName().toLowerCase().contains(q) &&
                                !u.getLastName().toLowerCase().contains(q)) {
                            continue;
                        }
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("student", s);
                    map.put("user", u);
                    map.put("enrollmentId", e.getId());
                    studentsData.add(map);
                }
            }
        }
        return studentsData;
    }

    public Map<Integer, Double> getGradesForEnrollments(List<Map<String, Object>> studentsData) {
        Map<Integer, Double> gradesMap = new HashMap<>();
        for (Map<String, Object> data : studentsData) {
            Integer enrollmentId = (Integer) data.get("enrollmentId");
            List<Grade> gList = gradeDao.findByEnrollmentId(enrollmentId);
            if (!gList.isEmpty()) {
                gradesMap.put(enrollmentId, gList.get(0).getGrade());
            }
        }
        return gradesMap;
    }

    public void updateGrade(int enrollmentId, double gradeVal) {
        List<Grade> existingGrades = gradeDao.findByEnrollmentId(enrollmentId);
        if (!existingGrades.isEmpty()) {
            Grade grade = existingGrades.get(0);
            grade.setGrade(gradeVal);
            grade.setDateRecorded(LocalDate.now().toString());
            gradeDao.update(grade);
        } else {
            Grade grade = new Grade();
            grade.setEnrollmentId(enrollmentId);
            grade.setGrade(gradeVal);
            grade.setDateRecorded(LocalDate.now().toString());
            gradeDao.save(grade);
        }
    }

    public Optional<Attendance> getAttendance(int enrollmentId, String date) {
        return attendanceDao.findByEnrollmentIdAndDate(enrollmentId, date);
    }

    public void updateAttendance(int enrollmentId, String date, String status) {
        Optional<Attendance> existing = attendanceDao.findByEnrollmentIdAndDate(enrollmentId, date);
        if (existing.isPresent()) {
            Attendance attendance = existing.get();
            attendance.setStatus(status);
            attendanceDao.update(attendance);
        } else {
            Attendance attendance = new Attendance();
            attendance.setEnrollmentId(enrollmentId);
            attendance.setAttendanceDate(date);
            attendance.setStatus(status);
            attendanceDao.save(attendance);
        }
    }

    public void updateProfile(User user, Teacher teacher, String email, String password) {
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
            teacher.setEmail(email);
        }
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        userDao.update(user);
        teacherDao.update(teacher);
    }
}
