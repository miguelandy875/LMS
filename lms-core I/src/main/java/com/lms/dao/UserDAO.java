package com.lms.dao;

import com.lms.model.Admin;
import com.lms.model.Librarian;
import com.lms.model.Member;
import com.lms.model.User;
import com.lms.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements GenericDAO<User> {

    @Override
    public boolean insert(User user) {
        String sql = "INSERT INTO users (user_id, user_fname, user_lname, user_sex, user_phone, user_email, user_pwd, user_role, user_status, createdat) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getUserId());
            stmt.setString(2, user.getUserFname());
            stmt.setString(3, user.getUserLname());
            stmt.setString(4, user.getUserSex());
            stmt.setString(5, user.getUserPhone());
            stmt.setString(6, user.getUserEmail());
            stmt.setString(7, user.getUserPwd());
            stmt.setString(8, user.getuserRole());
            stmt.setBoolean(9, user.isUserStatus());
            stmt.setTimestamp(10, user.getCreatedAt());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET user_fname=?, user_lname=?, user_sex=?, user_phone=?, user_email=?, user_pwd=?,user_role=?, user_status=?, createdat=? WHERE user_id=?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getUserId());
            stmt.setString(2, user.getUserFname());
            stmt.setString(3, user.getUserLname());
            stmt.setString(4, user.getUserSex());
            stmt.setString(5, user.getUserPhone());
            stmt.setString(6, user.getUserEmail());
            stmt.setString(7, user.getUserPwd());
            stmt.setString(8, user.getuserRole());
            stmt.setBoolean(9, user.isUserStatus());
            stmt.setTimestamp(10, user.getCreatedAt());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE user_id=?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id=?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String fname = rs.getString("user_fname");
        String lname = rs.getString("user_lname");
        String sex = rs.getString("user_sex");
        String phone = rs.getString("user_phone");
        String email = rs.getString("user_email");
        String pwd = rs.getString("user_pwd");
        String role = rs.getString("user_role");
        boolean status = rs.getBoolean("user_status");
        Timestamp created = rs.getTimestamp("createdat");

        switch (role.toLowerCase()) {
            case "admin":
                return new Admin(id, fname, lname, sex, role, email, phone, pwd, status, created);
            case "librarian":
                return new Librarian(id, fname, lname, sex, role, email, phone, pwd, status, created);
            case "member":
                return new Member(id, fname, lname, sex, role, email, phone, pwd, status, created);
            default:
                // fallback to anonymous user type if unknown role
                return new Member(id, fname, lname, sex, role, email, phone, pwd, status, created);
        }
    }
    public User findByCredentials(String email, String password) {
        String sql = "SELECT * FROM users WHERE user_email=? AND user_pwd=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
		return mapRowToUser(rs);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
