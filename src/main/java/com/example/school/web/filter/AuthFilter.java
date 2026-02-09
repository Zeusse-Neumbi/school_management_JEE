package com.example.school.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AuthFilter
 * Intercepts requests to protected resources.
 */
@WebFilter(urlPatterns = { "/dashboard", "/eleves/*", "/profs/*", "/classes/*" })
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Check Session
        HttpSession session = req.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (isLoggedIn) {
            // User is logged in, continue
            chain.doFilter(request, response);
        } else {
            // Not logged in, redirect to login
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
