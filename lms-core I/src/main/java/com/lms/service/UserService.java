package com.lms.service;

import java.util.List;

import com.lms.model.User;

public interface UserService {
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(int userId);
    User findUserById(int userId);
    List<User> findAllUsers();
    User login(String username, String password);

    // (Optional) Handy methods for the future:
    // User findUserByUsername(String username); 
    // List<User> findUsersByRole(String role);
}