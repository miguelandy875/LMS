package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Action;
import com.lms.model.Book;
import com.lms.model.User;
import com.lms.utils.DatabaseUtil;

public class ActionDAO implements GenericDAO<Action> {

    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean insert(Action action) {
        String sql = "INSERT INTO actions (action_Id, user_id_perform, user_id, book_id, action_type, action_date, action_details) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, action.getActionId());
            stmt.setInt(2, action.getPerformedBy().getUserId());
            stmt.setObject(3, action.getUserTarget() != null ? action.getUserTarget().getUserId() : null);
            stmt.setObject(4, action.getBookTarget() != null ? action.getBookTarget().getBookId() : null);    
            stmt.setString(5, action.getActionType());
            stmt.setTimestamp(6, Timestamp.valueOf(action.getActionDate()));
            stmt.setString(7, action.getActionDetails());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    action.setActionId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean update(Action action) {
        String sql = "UPDATE actions SET user_id_perform=?, user_id=?, book_id=?, action_type=?, action_date=?, action_details=? WHERE action_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, action.getActionId());
                stmt.setInt(2, action.getPerformedBy().getUserId());
                stmt.setObject(3, action.getUserTarget() != null ? action.getUserTarget().getUserId() : null);
                stmt.setObject(4, action.getBookTarget() != null ? action.getBookTarget().getBookId() : null);    
                stmt.setString(5, action.getActionType());
                stmt.setTimestamp(6, Timestamp.valueOf(action.getActionDate()));
                stmt.setString(7, action.getActionDetails());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM actions WHERE action_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Action findById(int id) {
        String sql = "SELECT * FROM actions WHERE action_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int performerId = rs.getInt("user_id_perform");
                Integer targetId = rs.getObject("user_id") != null ? rs.getInt("user_id") : null;
                Integer bookId = rs.getObject("book_id") != null ? rs.getInt("book_id") : null;

                User performer = userDAO.findById(performerId);
                User target = targetId != null ? userDAO.findById(targetId) : null;
                Book book = bookId != null ? bookDAO.findById(bookId) : null;

                return new Action(
                    rs.getInt("action_id"),
                    book,
                    target,
                    performer,
                    rs.getString("action_type"),
                    rs.getTimestamp("action_date").toLocalDateTime(),
                    rs.getString("action_details")
                   
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Action> findAll() {
        String sql = "SELECT * FROM actions";
        List<Action> list = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int performerId = rs.getInt("user_id_perform");
                Integer targetId = rs.getObject("user_id") != null ? rs.getInt("user_id") : null;
                Integer bookId = rs.getObject("book_id") != null ? rs.getInt("book_id") : null;

                User performer = userDAO.findById(performerId);
                User target = targetId != null ? userDAO.findById(targetId) : null;
                Book book = bookId != null ? bookDAO.findById(bookId) : null;

                list.add(new Action(
                    rs.getInt("action_id"),
                    book,
                    target,
                    performer,
                    rs.getString("action_type"),
                    rs.getTimestamp("action_date").toLocalDateTime(),
                    rs.getString("action_details")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}