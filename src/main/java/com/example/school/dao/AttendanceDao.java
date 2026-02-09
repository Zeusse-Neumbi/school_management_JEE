package com.example.school.dao;

import com.example.school.model.Attendance;
import java.util.List;

public interface AttendanceDao {
    void save(Attendance attendance);

    List<Attendance> findByEnrollmentId(int enrollmentId);

    java.util.Optional<Attendance> findByEnrollmentIdAndDate(int enrollmentId, String date);

    void update(Attendance attendance);

    double getAttendanceRate(int studentId);
}
