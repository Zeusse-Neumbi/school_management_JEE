package com.example.school.service;

import com.example.school.dao.UserDao;
import com.example.school.model.User;
import com.example.school.util.PasswordUtil;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.checkPassword(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(int id) {
        return userDao.findById(id);
    }

    public void updateProfile(User user, String newEmail, String newPassword) {
        user.setEmail(newEmail);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            user.setPassword(hashedPassword);
        }
        userDao.update(user);
    }
}
