package com.example.school.dao;

import com.example.school.model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentDao {
    void save(Enrollment enrollment);

    Optional<Enrollment> findById(int id);

    List<Enrollment> findByStudentId(int studentId);

    List<Enrollment> findByCourseId(int courseId);

    boolean isEnrolled(int studentId, int courseId);

    void delete(int studentId, int courseId);
}
