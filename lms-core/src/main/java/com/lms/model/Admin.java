package com.lms.model;

import java.sql.Timestamp;

import java.util.Collections;
import java.util.List;

/**
 * Admin user: has full access to manage users and view reports.
 */
public class Admin extends User {

    // === Constructor ===
    public Admin() {
        super();
    }

    public Admin(int userId, String userFname, String userLname, String userSex, String userEmail,
                 String userPhone, String userPwd, String userRole, boolean userStatus, Timestamp createdAt) {
        super(userId, userFname, userLname, userSex, userEmail, userPhone, userPwd, userRole, userStatus, createdAt);
    }

    // === Admin-specific methods ===

    public void createUser(User usr) {
        // TODO: Hook into UserDAO.create()
    }

    public void suspendUser(int userId) {
        // TODO: UserDAO.setStatus(userId, false)
    }

    public void deleteUser(int userId) {
        // TODO: UserDAO.delete(userId)
    }

    public List<Action> viewSystemReports() {
        // TODO: ActionDAO.getAll() or filtered logs
        return Collections.emptyList();
    }

    @Override
    public void showDashboard() {
        System.out.println("Admin Dashboard");
    }
    @Override
    public String toString() {
        return "[ID=" + getUserId() + ", Name=" + getUserFname() + " " + getUserLname() + "]";
    }
}
