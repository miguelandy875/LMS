package com.lms.service.impl;

import com.lms.dao.UserDAO;
import com.lms.model.User;
import com.lms.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    @Override
    public void addUser(User user) {
        userDAO.insert(user);
    }

    @Override
    public void updateUser(User user) {
        userDAO.update(user);
    }

    @Override
    public void deleteUser(int userId) {
        userDAO.delete(userId);
    }

    @Override
    public User findUserById(int userId) {
        return userDAO.findById(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public User login(String username, String password) {
        return userDAO.findByCredentials(username, password);
    }

    // @Override
    // public User findUserByUsername(String username) {
    //     return userDAO.findByUsername(username);
    // }

    // @Override
    // public List<User> findUsersByRole(String role) {
    //     return userDAO.findByRole(role);
    // }
}