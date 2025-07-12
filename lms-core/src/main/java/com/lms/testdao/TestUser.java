package com.lms.testdao;

import com.lms.dao.UserDAO;
import com.lms.model.*;
import java.util.List;

import java.sql.Timestamp;


public class TestUser {
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        Admin admin = new Admin();
        admin.setUserId(5);
        admin.setUserFname("NICE");
        admin.setUserLname("STELLA");
        admin.setUserSex("F");
        admin.setUserPhone("0000000");
        admin.setUserEmail("nice@lms.com");
        admin.setUserPwd("nicepass");
        admin.setuserRole("Admin");
        admin.setUserStatus(true);
        admin.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        

        dao.insert(admin);

        User found = dao.findById(1);
        System.out.println("Found user: " + found.getUserFname() + " (" + found.getuserRole() + ")");

        // found.setUserPwd("newpass123");
        // dao.update(found);

        List<User> users = dao.findAll();
        System.out.println("Total users: " + users.size());

        
    }
}