package com.lms.model;

import java.time.LocalDateTime;

/**
 * Represents an action performed in the LMS system
 * An action can be performed by a user (admin/librarian) on either a user or a book.
 */
public class Action {

    private int actionId;
    private Book bookTarget;               // Targeted book (optional)
    private User userTarget;              // Targeted user (optional)
    private User performedBy;             // The admin or librarian who performed the action
    private String actionType;
    private LocalDateTime actionDate;
    private String actionDetails;
        
        public Action(){
            //constructeur par défaut
            }
        public Action(int actionId, Book bookTarget, User userTarget, User performedBy,
                  String actionType, LocalDateTime actionDate, String actionDetails) {
        this.actionId = actionId;
        this.bookTarget = bookTarget;
        this.userTarget = userTarget;
        this.performedBy = performedBy;
        this.actionType = actionType;
        this.actionDate = actionDate;
        this.actionDetails = actionDetails;
    }

    // Getters and setters
    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public Book getBookTarget() {
        return bookTarget;
    }

    public void setBookTarget(Book bookTarget) {
        this.bookTarget = bookTarget;
    }

    public User getUserTarget() {
        return userTarget;
    }

    public void setUserTarget(User userTarget) {
        this.userTarget = userTarget;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }
}