package com.example.school.service;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.util.*;

public class StudentService {
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final EnrollmentDao enrollmentDao;
    private final GradeDao gradeDao;
    private final AttendanceDao attendanceDao;
    private final TeacherDao teacherDao;
    private final UserDao userDao;

    public StudentService(StudentDao studentDao, CourseDao courseDao,
            EnrollmentDao enrollmentDao, GradeDao gradeDao,
            AttendanceDao attendanceDao, TeacherDao teacherDao, UserDao userDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.enrollmentDao = enrollmentDao;
        this.gradeDao = gradeDao;
        this.attendanceDao = attendanceDao;
        this.teacherDao = teacherDao;
        this.userDao = userDao;
    }

    public Optional<Student> getStudentByUserId(int userId) {
        return studentDao.findByUserId(userId);
    }

    public int getCourseCount(int studentId) {
        return courseDao.findByStudentId(studentId).size();
    }

    public double calculateGPA(int studentId) {
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(studentId);
        double totalGrades = 0;
        int gradeCount = 0;
        for (Enrollment e : enrollments) {
            List<Grade> grades = gradeDao.findByEnrollmentId(e.getId());
            for (Grade g : grades) {
                totalGrades += g.getGrade();
                gradeCount++;
            }
        }
        return gradeCount > 0 ? totalGrades / gradeCount : 0.0;
    }

    public double getAttendanceRate(int studentId) {
        return attendanceDao.getAttendanceRate(studentId);
    }

    public List<Course> getEnrolledCourses(int studentId) {
        return courseDao.findByStudentId(studentId);
    }

    public List<Course> getAvailableCourses(int studentId) {
        List<Course> allCourses = courseDao.findAll();
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(studentId);
        Set<Integer> enrolledIds = new HashSet<>();
        for (Enrollment e : enrollments)
            enrolledIds.add(e.getCourseId());

        List<Course> available = new ArrayList<>();
        for (Course c : allCourses) {
            if (!enrolledIds.contains(c.getId())) {
                available.add(c);
            }
        }
        return available;
    }

    public void enroll(int studentId, int courseId) {
        if (!enrollmentDao.isEnrolled(studentId, courseId)) {
            Enrollment enrollment = new Enrollment(0, studentId, courseId, java.time.LocalDate.now().toString());
            enrollmentDao.save(enrollment);
        }
    }

    public void unenroll(int studentId, int courseId) {
        enrollmentDao.delete(studentId, courseId);
    }

    public Map<Integer, List<Grade>> getGrades(int studentId) {
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(studentId);
        Map<Integer, List<Grade>> gradesMap = new HashMap<>();
        for (Enrollment e : enrollments) {
            gradesMap.put(e.getId(), gradeDao.findByEnrollmentId(e.getId()));
        }
        return gradesMap;
    }

    public Map<Integer, List<Attendance>> getAttendance(int studentId) {
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(studentId);
        Map<Integer, List<Attendance>> attendanceMap = new HashMap<>();
        for (Enrollment e : enrollments) {
            attendanceMap.put(e.getId(), attendanceDao.findByEnrollmentId(e.getId()));
        }
        return attendanceMap;
    }

    public Course getCourse(int courseId) {
        return courseDao.findById(courseId).orElse(null);
    }

    public String getTeacherName(int courseId) {
        Optional<Course> courseOpt = courseDao.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Optional<Teacher> teacherOpt = teacherDao.findById(course.getTeacherId());
            if (teacherOpt.isPresent()) {
                Optional<User> userOpt = userDao.findById(teacherOpt.get().getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    return user.getFirstName() + " " + user.getLastName();
                }
            }
        }
        return "Unknown";
    }

    public java.util.List<Enrollment> getEnrollments(int studentId) {
        return enrollmentDao.findByStudentId(studentId);
    }
}
