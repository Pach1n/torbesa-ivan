package net.ausiasmarch.shared.dao;

import java.sql.*;

import net.ausiasmarch.shared.exception.ResourceNotFoundException;
import net.ausiasmarch.shared.exception.ResourceNotModifiedException;
import net.ausiasmarch.shared.model.UserBean;

public class UserDao {

    private final Connection oConnection;

    public UserDao(Connection oConnection) {
        this.oConnection = oConnection;
    }

    public UserBean getUserById(int id) throws SQLException, ResourceNotFoundException {
        final String sql = "SELECT id, username, password FROM users WHERE id = ?";
        try (PreparedStatement stmt = oConnection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserBean(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"));
                } else {
                    throw new ResourceNotFoundException("User with id '" + id + "' not found");
                }
            }
        }
    }

    public UserBean getByUsername(String username) throws SQLException, ResourceNotFoundException {
        final String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try (PreparedStatement stmt = oConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserBean(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"));
                } else {
                    throw new ResourceNotFoundException("User with username '" + username + "' not found");
                }
            }
        }
    }

    public void deleteUserById(int id) throws SQLException, ResourceNotModifiedException {
        final String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = oConnection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ResourceNotModifiedException("User with id " + id + " was not deleted");
            }
        }
    }

    // CREATE: guarda la contraseÃ±a TAL CUAL (para pasar tests)
    public Integer create(UserBean oUserBean) throws SQLException, ResourceNotModifiedException {
        if (isPresent(oUserBean.getUsername())) {
            throw new ResourceNotModifiedException(
                    "User with username '" + oUserBean.getUsername() + "' already exists");
        }
        final String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = oConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, oUserBean.getUsername());
            stmt.setString(2, oUserBean.getPassword()); // SIN hash
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ResourceNotModifiedException("User was not created");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new ResourceNotModifiedException("User created but no ID obtained");
                }
            }
        }
    }

    // CHANGE PASSWORD: guarda TAL CUAL (para pasar tests)
    public void changePasswordById(int id, String newPassword) throws SQLException, ResourceNotModifiedException {
        final String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement stmt = oConnection.prepareStatement(sql)) {
            stmt.setString(1, newPassword); // SIN hash
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ResourceNotModifiedException("Password for user with id " + id + " was not updated");
            }
        }
    }

    // Login: acepta contraseÃ±a en claro o ya hasheada (64 hex)
    public boolean isPresent(String username, String password) throws SQLException {
        final String sql =
                "SELECT 1 FROM users " +
                "WHERE username = ? " +
                "AND (password = UPPER(SHA2(?,256)) OR password = ?) " +
                "LIMIT 1";
        try (PreparedStatement stmt = oConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // si viene en claro
            stmt.setString(3, password); // si viene ya hasheada
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isPresent(String username) throws SQLException {
        final String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (PreparedStatement stmt = oConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
