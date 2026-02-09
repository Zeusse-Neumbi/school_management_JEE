package com.example.school.dao;

import com.example.school.model.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherDao {
    void save(Teacher teacher);

    void update(Teacher teacher);

    void delete(int id);

    Optional<Teacher> findById(int id);

    Optional<Teacher> findByUserId(int userId);

    void deleteByUserId(int userId);

    Optional<Teacher> findByEmployeeId(String employeeId);

    List<Teacher> findAll();

    List<Teacher> search(String query, int page, int pageSize);

    int count(String query);
}
