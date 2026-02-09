package com.example.school.dao;

import com.example.school.model.Grade;
import java.util.List;

public interface GradeDao {
    void save(Grade grade);

    List<Grade> findByEnrollmentId(int enrollmentId);
}
