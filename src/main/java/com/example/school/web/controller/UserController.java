package com.example.school.web.controller;

import com.example.school.dao.RoleDao;
import com.example.school.dao.UserDao;
import com.example.school.dao.impl.RoleDaoSqliteImpl;
import com.example.school.dao.impl.UserDaoSqliteImpl;
import com.example.school.model.Role;
import com.example.school.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "UserController", urlPatterns = { "/users", "/users/create", "/users/edit", "/users/delete" })
public class UserController extends HttpServlet {

    private UserDao userDao;
    private RoleDao roleDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoSqliteImpl();
        roleDao = new RoleDaoSqliteImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if (path.equals("/users")) {
            req.setAttribute("users", userDao.findAll()); // Need to implement findAll in UserDao
            req.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(req, resp);
        } else if (path.equals("/users/create")) {
            req.getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(req, resp);
        } else if (path.equals("/users/edit")) {
            String email = req.getParameter("email");
            Optional<User> foundUser = userDao.findByEmail(email);
            if (foundUser.isPresent()) {
                req.setAttribute("user", foundUser.get());
                req.getRequestDispatcher("/WEB-INF/views/users/edit.jsp").forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/users");
            }
        } else if (path.equals("/users/delete")) {
        } else if (path.equals("/users/delete")) {
            String email = req.getParameter("email"); // Was username
            userDao.delete(email);
            resp.sendRedirect(req.getContextPath() + "/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if (path.equals("/users/create")) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String roleName = "STUDENT"; // Default

            if (userDao.findByEmail(email).isPresent()) {
                req.setAttribute("error", "Email already exists!");
                req.getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(req, resp);
                return;
            }

            Optional<Role> role = roleDao.findByName(roleName);
            int roleId = role.map(Role::getId).orElse(3); // Default to 3 (Student) if not found

            // Hash the password before saving
            String hashedPassword = com.example.school.util.PasswordUtil.hashPassword(password);

            User newUser = new User(email, hashedPassword, roleId, firstName, lastName);
            userDao.save(newUser);

            resp.sendRedirect(req.getContextPath() + "/users");

        } else if (path.equals("/users/edit")) {
            // similar logic for update
            resp.sendRedirect(req.getContextPath() + "/users");
        }
    }
}
