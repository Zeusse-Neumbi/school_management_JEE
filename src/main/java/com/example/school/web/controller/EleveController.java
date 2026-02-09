package com.example.school.web.controller;

import com.example.school.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "EleveController", urlPatterns = { "/eleves/moyenne" })
public class EleveController extends HttpServlet {

    private StudentService studentService;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/eleves/moyenne.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nom = req.getParameter("nom");
        String note1Str = req.getParameter("note1");
        String note2Str = req.getParameter("note2");

        try {
            double note1 = Double.parseDouble(note1Str);
            double note2 = Double.parseDouble(note2Str);

            double moyenne = studentService.calculateAverage(note1, note2);

            req.setAttribute("nom", nom);
            req.setAttribute("moyenne", moyenne);

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Please enter valid numbers.");
        }

        req.getRequestDispatcher("/WEB-INF/views/eleves/moyenne.jsp").forward(req, resp);
    }
}
