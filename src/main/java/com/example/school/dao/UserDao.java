package com.example.school.dao;

import com.example.school.model.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);

    Optional<User> findById(int id);

    int save(User user);

    void update(User user);

    void delete(String email);

    void delete(int id);

    java.util.List<User> findAll();

    java.util.List<User> search(String query, int page, int pageSize);

    int count();

    int count(String query);
}
