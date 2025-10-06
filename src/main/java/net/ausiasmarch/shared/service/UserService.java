package net.ausiasmarch.shared.service;

import java.sql.Connection;
import java.sql.SQLException;

import net.ausiasmarch.shared.connection.HikariPool;
import net.ausiasmarch.shared.dao.UserDao;
import net.ausiasmarch.shared.model.UserBean;

public class UserService {

    public Integer create(UserBean oUserBean) throws SQLException {
        try (Connection oConnection = HikariPool.getConnection()) {
            UserDao userDao = new UserDao(oConnection);
            return userDao.create(oUserBean);
        }
    }

    public boolean authenticate(String username, String password) throws SQLException {
    try (Connection con = HikariPool.getConnection()) {
        UserDao dao = new UserDao(con);
        return dao.isPresent(username, password);
    }
}

    public Boolean isPresent(String username) throws SQLException {
        try (Connection oConnection = HikariPool.getConnection()) {
            UserDao userDao = new UserDao(oConnection);
            return userDao.isPresent(username);
        }
    }

    public UserBean getByUsername(String username) throws SQLException {
        try (Connection oConnection = HikariPool.getConnection()) {
            UserDao userDao = new UserDao(oConnection);
            return userDao.getByUsername(username);
        }
    }

}
