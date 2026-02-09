package com.example.school.dao;

import com.example.school.model.Attendance;
import java.util.List;

public interface AttendanceDao {
    void save(Attendance attendance);

    List<Attendance> findByEnrollmentId(int enrollmentId);
}
