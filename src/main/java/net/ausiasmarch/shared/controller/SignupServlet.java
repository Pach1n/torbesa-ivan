package net.ausiasmarch.shared.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

import net.ausiasmarch.shared.exception.ResourceNotModifiedException;
import net.ausiasmarch.shared.model.UserBean;
import net.ausiasmarch.shared.service.UserService;

@WebServlet("/shared/signup")
public class SignupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password"); // SHA256 hash recibido
        String errorMsg = null;

        if (username == null || username.length() < 5) {
            errorMsg = "Username must be at least 5 characters.";
        } else if (password == null || password.length() != 64 || !password.matches("[0-9a-fA-F]+")) {
            errorMsg = "Password must be a valid SHA256 hash.";
        }

        if (errorMsg != null) {
            request.setAttribute("error", errorMsg);
            RequestDispatcher dispatcher = request.getRequestDispatcher("signup.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        UserService userService = new UserService();
        try {
            userService.create(new UserBean(username, password));
            // si ha ido todo ok se debe mandar un mensaje
            request.setAttribute("info", "You've been signed up as " + username + ". Please log into the system.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("signup.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Database error.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("signup.jsp");
            dispatcher.forward(request, response);
        } catch (ResourceNotModifiedException e) {
            System.err.println("Resource not modified: " + e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
        }
    }
}
