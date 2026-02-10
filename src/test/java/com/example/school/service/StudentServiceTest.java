package com.example.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentDao studentDao;
    @Mock
    private CourseDao courseDao;
    @Mock
    private EnrollmentDao enrollmentDao;
    @Mock
    private GradeDao gradeDao;
    @Mock
    private AttendanceDao attendanceDao;
    @Mock
    private TeacherDao teacherDao;
    @Mock
    private UserDao userDao;

    @InjectMocks
    private StudentService studentService;

    @Test
    void calculateGPA_ShouldReturnCorrectAverage() {
        // Arrange
        int studentId = 1;
        Enrollment e1 = new Enrollment(1, studentId, 101, "2023-01-01");
        Enrollment e2 = new Enrollment(2, studentId, 102, "2023-01-01");

        when(enrollmentDao.findByStudentId(studentId)).thenReturn(Arrays.asList(e1, e2));

        Grade g1 = new Grade(1, 1, 15.0, "2023-01-10");
        Grade g2 = new Grade(2, 2, 10.0, "2023-01-12");

        when(gradeDao.findByEnrollmentId(1)).thenReturn(Collections.singletonList(g1));
        when(gradeDao.findByEnrollmentId(2)).thenReturn(Collections.singletonList(g2));

        // Act
        double gpa = studentService.calculateGPA(studentId);

        // Assert
        assertEquals(12.5, gpa, 0.01);
    }

    @Test
    void calculateGPA_ShouldReturnZeroWhenNoGrades() {
        // Arrange
        int studentId = 1;
        when(enrollmentDao.findByStudentId(studentId)).thenReturn(Collections.emptyList());

        // Act
        double gpa = studentService.calculateGPA(studentId);

        // Assert
        assertEquals(0.0, gpa, 0.01);
    }

    @Test
    void getTeacherName_ShouldReturnName_WhenTeacherExists() {
        // Arrange
        int courseId = 101;
        int teacherId = 5;
        int userId = 10;

        Course course = new Course(courseId, "Math", "Mathematics", teacherId, 3);
        Teacher teacher = new Teacher(teacherId, userId, "EMP001", "teacher@school.com", "Math Dept");
        User user = new User(userId, "john@example.com", "pass", 2, "John", "Doe");

        when(courseDao.findById(courseId)).thenReturn(Optional.of(course));
        when(teacherDao.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String teacherName = studentService.getTeacherName(courseId);

        // Assert
        assertEquals("John Doe", teacherName);
    }

    @Test
    void getTeacherName_ShouldReturnUnknown_WhenCourseNotFound() {
        when(courseDao.findById(anyInt())).thenReturn(Optional.empty());

        String name = studentService.getTeacherName(999);

        assertEquals("Unknown", name);
    }

    @Test
    void getAvailableCourses_ShouldExcludeEnrolledCourses() {
        // Arrange
        int studentId = 1;
        Course c1 = new Course(101, "Math", "Math", 3, 5);
        Course c2 = new Course(102, "Science", "Sci", 3, 6);
        Course c3 = new Course(103, "History", "Hist", 3, 7);

        when(courseDao.findAll()).thenReturn(Arrays.asList(c1, c2, c3));

        Enrollment e1 = new Enrollment(1, studentId, 101, "2023-01-01");
        when(enrollmentDao.findByStudentId(studentId)).thenReturn(Collections.singletonList(e1));

        // Act
        List<Course> available = studentService.getAvailableCourses(studentId);

        // Assert
        assertEquals(2, available.size());
        assertTrue(available.contains(c2));
        assertTrue(available.contains(c3));
        assertFalse(available.contains(c1));
    }

    @Test
    void enroll_ShouldCallDao_WhenCourseIsAvailable() {
        int studentId = 1;
        int courseId = 101;

        studentService.enroll(studentId, courseId);

        verify(enrollmentDao).save(any(Enrollment.class));
    }

    @Test
    void unenroll_ShouldCallDao_WhenEnrolled() {
        int studentId = 1;
        int courseId = 101;

        studentService.unenroll(studentId, courseId);

        verify(enrollmentDao).delete(studentId, courseId);
    }

    @Test
    void getGrades_ShouldReturnGradesGroupedByEnrollment() {
        int studentId = 1;
        Enrollment e1 = new Enrollment(1, studentId, 101, "2023-01-01");
        Enrollment e2 = new Enrollment(2, studentId, 102, "2023-01-01");

        when(enrollmentDao.findByStudentId(studentId)).thenReturn(Arrays.asList(e1, e2));

        Grade g1 = new Grade(1, 1, 15.0, "2023-01-10");
        Grade g2 = new Grade(2, 2, 10.0, "2023-01-12");

        when(gradeDao.findByEnrollmentId(1)).thenReturn(Collections.singletonList(g1));
        when(gradeDao.findByEnrollmentId(2)).thenReturn(Collections.singletonList(g2));

        java.util.Map<Integer, List<Grade>> grades = studentService.getGrades(studentId);

        assertEquals(2, grades.size());
        assertEquals(1, grades.get(1).size());
        assertEquals(15.0, grades.get(1).get(0).getGrade());
    }
}
