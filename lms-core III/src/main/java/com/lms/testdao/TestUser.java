package com.lms.testdao;

import com.lms.dao.UserDAO;
import com.lms.model.*;
import java.util.List;

import java.sql.Timestamp;


public class TestUser {
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        Librarian Librarian = new Librarian();
        Librarian.setUserId(1);
        Librarian.setUserFname("NICE");
        Librarian.setUserLname("STELLA");
        Librarian.setUserSex("F");
        Librarian.setUserPhone("0000000");
        Librarian.setUserEmail("nice@lms.com");
        Librarian.setUserPwd("nicepass");
        Librarian.setUserRole("Librarian");
        Librarian.setUserStatus(true);
        Librarian.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        

        dao.insert(Librarian);

        // User found = dao.findById(1);
        // System.out.println("Found user: " + found.getUserFname() + " (" + found.getUserRole() + ")");

        // found.setUserPwd("newpass123");
        // dao.update(found);

        // List<User> users = dao.findAll();
        // System.out.println("Total users: " + users.size());

        
    }
}