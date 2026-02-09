package com.example.school.dao;

import com.example.school.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDao {
    void save(Student student);

    void update(Student student);

    void delete(int id);

    Optional<Student> findById(int id);

    Optional<Student> findByStudentNumber(String studentNumber);

    Optional<Student> findByUserId(int userId);

    void deleteByUserId(int userId);

    List<Student> findAll();

    List<Student> search(String query, int page, int pageSize);

    int count(String query);
}
