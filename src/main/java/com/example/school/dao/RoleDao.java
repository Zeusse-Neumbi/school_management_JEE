package com.example.school.dao;

import com.example.school.model.Role;
import java.util.Optional;

public interface RoleDao {
    Optional<Role> findById(int id);

    Optional<Role> findByName(String name);
}
