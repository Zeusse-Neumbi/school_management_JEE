package com.example.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.school.dao.UserDao;
import com.example.school.model.User;
import com.example.school.util.PasswordUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void authenticate_ShouldReturnUser_WhenCredentialsAreValid() {
        String email = "test@test.com";
        String password = "password";
        String wrongPassword = "wrong";
        String hashedPassword = "$2a$10$testHash"; // Mocked hash

        User user = new User(1, email, hashedPassword, 3, "Test", "User");
        when(userDao.findByEmail(email)).thenReturn(Optional.of(user));

        try (MockedStatic<PasswordUtil> mockedPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
            mockedPasswordUtil.when(() -> PasswordUtil.checkPassword(password, hashedPassword)).thenReturn(true);
            mockedPasswordUtil.when(() -> PasswordUtil.checkPassword(wrongPassword, hashedPassword))
                    .thenReturn(false);

            Optional<User> result = userService.authenticate(email, password);
            assertTrue(result.isPresent());
            assertEquals(user, result.get());

            Optional<User> wrongResult = userService.authenticate(email, wrongPassword);
            assertFalse(wrongResult.isPresent());
        }
    }

    @Test
    void updateProfile_ShouldHashPassword_WhenNewPasswordProvided() {
        User user = new User(1, "old@test.com", "oldHash", 3, "Test", "User");
        String newEmail = "new@test.com";
        String newPassword = "newPassword";

        try (MockedStatic<PasswordUtil> mockedPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
            mockedPasswordUtil.when(() -> PasswordUtil.hashPassword(newPassword)).thenReturn("newHash");

            userService.updateProfile(user, newEmail, newPassword);

            verify(userDao).update(any(User.class));
            assertEquals("newHash", user.getPassword());
            assertEquals(newEmail, user.getEmail());
        }
    }
}
