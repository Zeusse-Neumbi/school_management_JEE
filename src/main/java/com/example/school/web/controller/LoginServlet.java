package com.example.school.web.controller;

import com.example.school.dao.UserDao;
import com.example.school.dao.impl.UserDaoSqliteImpl;
import com.example.school.model.User;
import com.example.school.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoSqliteImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // If user is already logged in, redirect to their dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            redirectUser(resp, user.getRoleId());
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Optional<User> userOpt = userDao.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Verify password
            if (PasswordUtil.checkPassword(password, user.getPassword())) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                redirectUser(resp, user.getRoleId());
                return;
            }
        }

        // Login failed
        req.setAttribute("error", "Invalid email or password");
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    private void redirectUser(HttpServletResponse resp, int roleId) throws IOException {
        switch (roleId) {
            case 1: // Admin
                resp.sendRedirect("admin/dashboard");
                break;
            case 2: // Teacher
                resp.sendRedirect("teacher/dashboard");
                break;
            case 3: // Student
                resp.sendRedirect("student/dashboard");
                break;
            default:
                resp.sendRedirect("login?error=UnknownRole");
        }
    }
}
