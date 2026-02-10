package com.example.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherDao teacherDao;
    @Mock
    private CourseDao courseDao;
    @Mock
    private EnrollmentDao enrollmentDao;
    @Mock
    private GradeDao gradeDao;
    @Mock
    private AttendanceDao attendanceDao;
    @Mock
    private StudentDao studentDao;
    @Mock
    private UserDao userDao;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void getTeacherCourses_ShouldReturnCourses() {
        int teacherId = 1;
        Course c1 = new Course(101, "Math", "MATH101", teacherId, 3);
        when(courseDao.findByTeacherId(teacherId)).thenReturn(Collections.singletonList(c1));

        List<Course> courses = teacherService.getTeacherCourses(teacherId);

        assertEquals(1, courses.size());
        assertEquals("Math", courses.get(0).getCourseName());
    }

    @Test
    void getStudentCountForTeacher_ShouldReturnTotalStudents() {
        int teacherId = 1;
        Course c1 = new Course(101, "Math", "MATH101", teacherId, 3);
        Course c2 = new Course(102, "Science", "SCI101", teacherId, 3);
        when(courseDao.findByTeacherId(teacherId)).thenReturn(Arrays.asList(c1, c2));

        Enrollment e1 = new Enrollment(1, 1, 101, "2023-01-01");
        Enrollment e2 = new Enrollment(2, 2, 101, "2023-01-01");
        Enrollment e3 = new Enrollment(3, 3, 102, "2023-01-01");

        when(enrollmentDao.findByCourseId(101)).thenReturn(Arrays.asList(e1, e2));
        when(enrollmentDao.findByCourseId(102)).thenReturn(Collections.singletonList(e3));

        int count = teacherService.getStudentCountForTeacher(teacherId);

        assertEquals(3, count);
    }

    @Test
    void updateGrade_ShouldUpdateExistingGrade_WhenGradeExists() {
        int enrollmentId = 1;
        double gradeVal = 18.0;
        Grade existingGrade = new Grade(1, enrollmentId, 15.0, "2023-01-01");

        when(gradeDao.findByEnrollmentId(enrollmentId)).thenReturn(Collections.singletonList(existingGrade));

        teacherService.updateGrade(enrollmentId, gradeVal);

        verify(gradeDao).update(any(Grade.class));
        verify(gradeDao, never()).save(any(Grade.class));
    }

    @Test
    void updateGrade_ShouldCreateNewGrade_WhenNoGradeExists() {
        int enrollmentId = 1;
        double gradeVal = 18.0;

        when(gradeDao.findByEnrollmentId(enrollmentId)).thenReturn(Collections.emptyList());

        teacherService.updateGrade(enrollmentId, gradeVal);

        verify(gradeDao).save(any(Grade.class));
        verify(gradeDao, never()).update(any(Grade.class));
    }

    @Test
    void updateAttendance_ShouldUpdateExisting_WhenRecordExists() {
        int enrollmentId = 1;
        String date = "2023-01-15";
        String status = "PRESENT";
        Attendance existing = new Attendance(1, enrollmentId, date, "ABSENT");

        when(attendanceDao.findByEnrollmentIdAndDate(enrollmentId, date)).thenReturn(Optional.of(existing));

        teacherService.updateAttendance(enrollmentId, date, status);

        verify(attendanceDao).update(any(Attendance.class));
        verify(attendanceDao, never()).save(any(Attendance.class));
    }

    @Test
    void updateAttendance_ShouldCreateNew_WhenNoRecordExists() {
        int enrollmentId = 1;
        String date = "2023-01-15";
        String status = "PRESENT";

        when(attendanceDao.findByEnrollmentIdAndDate(enrollmentId, date)).thenReturn(Optional.empty());

        teacherService.updateAttendance(enrollmentId, date, status);

        verify(attendanceDao).save(any(Attendance.class));
        verify(attendanceDao, never()).update(any(Attendance.class));
    }
}
