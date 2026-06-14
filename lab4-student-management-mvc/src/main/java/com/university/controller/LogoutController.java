package com.university.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // getSession(false) returns null if no session exists — avoids creating one
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // destroys all session data on the server
        }
        // Redirect to login, passing a success message as a query parameter
        response.sendRedirect(request.getContextPath() + "/login?message=You+have+been+logged+out");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // support logout via form POST as well
    }
}