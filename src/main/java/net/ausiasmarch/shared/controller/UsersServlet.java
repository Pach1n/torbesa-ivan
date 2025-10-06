package net.ausiasmarch.shared.controller;

import net.ausiasmarch.shared.connection.HikariPool;
import net.ausiasmarch.shared.dao.UserDao;
import net.ausiasmarch.shared.exception.ResourceNotModifiedException;
import net.ausiasmarch.shared.model.UserBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@WebServlet("/shared/users")
public class UsersServlet extends HttpServlet {

    // SHA-256 en MAYÚSCULAS (sin clase externa)
    private static String sha256Upper(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02X", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // GET: listado público
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<UserBean> all = new ArrayList<>();
        try (Connection con = HikariPool.getConnection()) {
            try (var ps = con.prepareStatement(
                    "SELECT id, username, password FROM users ORDER BY id");
                 var rs = ps.executeQuery()) {
                while (rs.next()) {
                    all.add(new UserBean(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        req.setAttribute("users", all);
        req.getRequestDispatcher("/WEB-INF/views/users.jsp").forward(req, resp);
    }

    // POST: crear/editar/borrar — SOLO autenticados
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession(false);
        UserBean sessionUser = (session == null) ? null : (UserBean) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Login required");
            return;
        }

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action");
            return;
        }

        try (Connection con = HikariPool.getConnection()) {
            UserDao dao = new UserDao(con);

            switch (action.toLowerCase()) {
                case "create": {
                    String username = req.getParameter("username");
                    String password = req.getParameter("password"); // en claro
                    if (username == null || username.isBlank() || password == null) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "username/password required");
                        return;
                    }
                    String hash = sha256Upper(password);
                    dao.create(new UserBean(username, hash));
                    break;
                }
                case "update": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    String username = req.getParameter("username");
                    if (username == null || username.isBlank()) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "username required");
                        return;
                    }
                    try (var ps = con.prepareStatement("UPDATE users SET username=? WHERE id=?")) {
                        ps.setString(1, username);
                        ps.setInt(2, id);
                        if (ps.executeUpdate() == 0)
                            throw new ResourceNotModifiedException("No update");
                    }
                    break;
                }
                case "setpass": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    String password = req.getParameter("password");
                    if (password == null) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "password required");
                        return;
                    }
                    String hash = sha256Upper(password);
                    dao.changePasswordById(id, hash);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    dao.deleteUserById(id);
                    break;
                }
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
                    return;
            }
        } catch (SQLException | ResourceNotModifiedException e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/shared/users");
    }
}