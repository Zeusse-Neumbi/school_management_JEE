package com.example.school.service;

import com.example.school.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> authenticate(String email, String password);
}
