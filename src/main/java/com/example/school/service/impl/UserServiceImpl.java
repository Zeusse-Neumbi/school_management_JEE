package com.example.school.service.impl;

import com.example.school.dao.UserDao;
import com.example.school.dao.impl.UserDaoSqliteImpl;
import com.example.school.model.User;
import com.example.school.service.UserService;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    // Emulating Dependency Injection
    public UserServiceImpl() {
        this.userDao = new UserDaoSqliteImpl();
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        return userDao.findByEmail(email)
                .filter(u -> com.example.school.util.PasswordUtil.checkPassword(password, u.getPassword()));
    }
}
