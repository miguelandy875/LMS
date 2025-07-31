package com.lms.testdao;

import java.time.LocalDateTime;
import java.util.List;

import com.lms.dao.ActionDAO;
import com.lms.dao.BookDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Action;
import com.lms.model.Book;
import com.lms.model.User;

public class TestAction{
        public static void main(String[] args) {
        ActionDAO actionDAO = new ActionDAO();
        BookDAO bookDAO = new BookDAO();
        UserDAO userDAO = new UserDAO();

        // Retrieve existing Book and Users (assuming their IDs exist in the DB)
        Book book = bookDAO.findById(1);  // Replace with valid book ID
        User performer = userDAO.findById(1); // Replace with valid Admin/Librarian ID
        User targetUser = userDAO.findById(1); // Replace with valid target user ID (optional)

        if (book == null || performer == null || targetUser == null) {
            System.out.println("Missing required Book, Target User or Performer User. Check your DB.");
            return;
        }

        // Create a new Action
        Action action = new Action();
        action.setActionId(2);
        action.setPerformedBy(performer);
        action.setUserTarget(targetUser);
        action.setBookTarget(book);
        action.setActionType("delete user");
        action.setActionDate(LocalDateTime.now());
        action.setActionDetails("deleted member account");

        actionDAO.insert(action);
        System.out.println("Inserted Action ID: " + action.getActionId());

        action.setActionType("deleted");
        action.setActionDetails("delete a member");
        actionDAO.update(action);

        Action found = actionDAO.findById(action.getActionId());
        System.out.println("Found: " + found.getActionDetails());

        List<Action> all = actionDAO.findAll();
        System.out.println("Total Actions: " + all.size());

       
    
    }
}
        
