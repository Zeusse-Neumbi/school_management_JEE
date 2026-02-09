package com.example.school.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProfController", urlPatterns = { "/profs" })
public class ProfController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Auth handled by Filter
        req.getRequestDispatcher("/WEB-INF/views/profs/list.jsp").forward(req, resp);
    }
}
