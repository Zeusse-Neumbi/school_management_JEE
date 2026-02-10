package com.example.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserDao userDao;
    @Mock
    private StudentDao studentDao;
    @Mock
    private TeacherDao teacherDao;
    @Mock
    private CourseDao courseDao;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getDashboardStats_ShouldReturnCorrectCounts() {
        when(userDao.findAll()).thenReturn(Arrays.asList(new User(), new User()));
        when(studentDao.findAll()).thenReturn(Collections.singletonList(new Student()));
        when(teacherDao.findAll()).thenReturn(Collections.emptyList());
        when(courseDao.findAll()).thenReturn(Arrays.asList(new Course(), new Course(), new Course()));

        Map<String, Integer> stats = adminService.getDashboardStats();

        assertEquals(2, stats.get("userCount"));
        assertEquals(1, stats.get("studentCount"));
        assertEquals(0, stats.get("teacherCount"));
        assertEquals(3, stats.get("courseCount"));
    }

    @Test
    void createUserWithRole_ShouldCreateUserAndRoleEntity() {
        User newUser = new User("test@test.com", "pass", 3, "Test", "User");
        Map<String, String> roleData = new java.util.HashMap<>();
        roleData.put("studentNumber", "S123");
        roleData.put("dateOfBirth", "2000-01-01");

        when(userDao.save(any(User.class))).thenReturn(10);

        adminService.createUserWithRole(newUser, "password", roleData);

        verify(userDao).save(any(User.class));
        verify(studentDao).save(any(Student.class));
    }

    @Test
    void deleteUser_ShouldCascadeDelete() {
        int userId = 1;

        adminService.deleteUser(userId);

        verify(studentDao).deleteByUserId(userId);
        verify(teacherDao).deleteByUserId(userId);
        verify(userDao).delete(userId);
    }
}
