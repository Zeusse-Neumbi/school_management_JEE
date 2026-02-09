package com.example.school.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DashboardController
 * Serves the dashboard page.
 * Auth is handled by AuthFilter.
 */
@WebServlet(name = "DashboardController", urlPatterns = { "/dashboard" })
public class DashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
