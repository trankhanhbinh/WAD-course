package com.university.controller;

import com.university.dao.UserDAO;
import com.university.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    /**
     * GET /login — display the login form.
     * If the user already has an authenticated session, skip the form and
     * redirect.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // do not create a new session
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/views/login.jsp").forward(request,
                response);
    }

    /**
     * POST /login — process the login form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // Server-side validation — never rely on the browser alone
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }
        User user = userDAO.authenticate(username.trim(), password);
        if (user != null) {
            // --- Session fixation protection ---
            // If a session exists from before login (anonymous browsing),
            // destroy it so the attacker cannot reuse the pre-login session ID.
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            // Create a brand-new session with a fresh, unpredictable ID
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());
            session.setAttribute("fullName", user.getFullName());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes of inactivity
           
            // Role-based redirect
            String redirect = user.isAdmin() ? "/dashboard" : "/student?action=list";
            response.sendRedirect(request.getContextPath() + redirect);
        } else {
            // Authentication failed — do not say whether username or password was wrong
            request.setAttribute("error", "Invalid username or password");
            request.setAttribute("username", username); // pre-fill username field
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }

}
