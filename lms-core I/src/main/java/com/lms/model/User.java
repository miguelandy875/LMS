package com.lms.model;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * Abstract base class for all user types in the system.
 * Implements Serializable to support session and data transfers.
 */
public abstract class User implements Serializable {

    // === Fields ===
    private int userId;
    private String userFname;
    private String userLname;
    private String userSex;
    private String userEmail;
    private String userPhone;
    private String userPwd;
    private String userRole;   // role e.g. Admin, Member, Librarian
    private boolean userStatus;
    private Timestamp createdAt;

    // === Constructors ===

    public User() {
        // Default constructor required for frameworks or libraries
    }

    public User(int userId, String userFname, String userLname, String userSex, String userEmail, String userPhone,
                String userPwd, String userRole, boolean userStatus, Timestamp createdAt) {
        this.userId = userId;
        this.userFname = userFname;
        this.userLname = userLname;
        this.userSex = userSex;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPwd = userPwd;
        this.userRole = userRole;
        this.userStatus = userStatus;
        this.createdAt = createdAt;
    }

    // === Getters and Setters ===

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getuserRole() {
        return userRole;
    }

    public void setuserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // === Behavior Methods ===

    public boolean login() {
        // Implement login logic here later
        return false;
    }

    public void logout() {
        // Implement logout logic here later
    }

    // Each subclass will define its own dashboard
    public abstract void showDashboard();
}
