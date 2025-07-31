package com.lms.controller.dialogs;

import com.lms.controller.modules.MemberController;
import com.lms.model.User;
import com.lms.dao.UserDAO;
import com.lms.util.DialogHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class MemberDialogController implements Initializable {
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private ComboBox<String> roleCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label passwordLabel;
    @FXML private Label confirmPasswordLabel;
    
    private UserDAO userDAO;
    private User currentMember;
    private MemberController memberController;
    private boolean isEditMode = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        
        setupComboBoxes();
        setupValidation();
    }
    
    private void setupComboBoxes() {
        genderCombo.getItems().addAll("Male", "Female", "Other");
        roleCombo.getItems().addAll("ADMIN", "LIBRARIAN", "MEMBER");
        statusCombo.getItems().addAll("ACTIVE", "INACTIVE");
        
        // Set default values
        genderCombo.setValue("Male");
        roleCombo.setValue("MEMBER");
        statusCombo.setValue("ACTIVE");
    }
    
    private void setupValidation() {
        // Add email validation
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateEmail();
        });
        
        // Add phone validation
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                phoneField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    public void setMember(User member) {
        this.currentMember = member;
        this.isEditMode = (member != null);
        
        if (isEditMode) {
            populateFields();
            hidePasswordFields();
        } else {
            showPasswordFields();
        }
    }
    
    public void setMemberController(MemberController controller) {
        this.memberController = controller;
    }
    
    private void populateFields() {
        if (currentMember != null) {
            firstNameField.setText(currentMember.getFirstName());
            lastNameField.setText(currentMember.getLastName());
            emailField.setText(currentMember.getEmail());
            phoneField.setText(currentMember.getPhone());
            genderCombo.setValue(currentMember.getSex());
            roleCombo.setValue(currentMember.getRole());
            statusCombo.setValue(currentMember.getStatus());
        }
    }
    
    private void showPasswordFields() {
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);
        passwordLabel.setVisible(true);
        confirmPasswordLabel.setVisible(true);
    }
    
    private void hidePasswordFields() {
        passwordField.setVisible(false);
        confirmPasswordField.setVisible(false);
        passwordLabel.setVisible(false);
        confirmPasswordLabel.setVisible(false);
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInput()) {
            try {
                if (isEditMode) {
                    updateMember();
                } else {
                    createMember();
                }
                
                if (memberController != null) {
                    memberController.refreshTable();
                }
                
                closeDialog();
            } catch (Exception e) {
                DialogHelper.showError("Save Error", "Could not save member: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeDialog();
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        if (firstNameField.getText().trim().isEmpty()) {
            errors.append("• First name is required\n");
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            errors.append("• Last name is required\n");
        }
        
        if (emailField.getText().trim().isEmpty()) {
            errors.append("• Email is required\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("• Please enter a valid email address\n");
        } else if (!isEditMode && userDAO.emailExists(emailField.getText().trim())) {
            errors.append("• Email already exists\n");
        } else if (isEditMode && !emailField.getText().trim().equals(currentMember.getEmail()) 
                   && userDAO.emailExists(emailField.getText().trim())) {
            errors.append("• Email already exists\n");
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            errors.append("• Phone number is required\n");
        } else if (phoneField.getText().trim().length() < 10) {
            errors.append("• Phone number must be at least 10 digits\n");
        }
        
        if (!isEditMode) {
            if (passwordField.getText().isEmpty()) {
                errors.append("• Password is required\n");
            } else if (passwordField.getText().length() < 6) {
                errors.append("• Password must be at least 6 characters\n");
            }
            
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                errors.append("• Passwords do not match\n");
            }
        }
        
        if (errors.length() > 0) {
            DialogHelper.showError("Validation Error", "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void createMember() {
        User newUser = new User();
        newUser.setFirstName(firstNameField.getText().trim());
        newUser.setLastName(lastNameField.getText().trim());
        newUser.setEmail(emailField.getText().trim());
        newUser.setPhone(phoneField.getText().trim());
        newUser.setSex(genderCombo.getValue());
        newUser.setRole(roleCombo.getValue());
        newUser.setStatus(statusCombo.getValue());
        newUser.setPassword(hashPassword(passwordField.getText()));
        
        userDAO.save(newUser);
        DialogHelper.showSuccess("Success", "Member created successfully.");
    }
    
    private void updateMember() {
        currentMember.setFirstName(firstNameField.getText().trim());
        currentMember.setLastName(lastNameField.getText().trim());
        currentMember.setEmail(emailField.getText().trim());
        currentMember.setPhone(phoneField.getText().trim());
        currentMember.setSex(genderCombo.getValue());
        currentMember.setRole(roleCombo.getValue());
        currentMember.setStatus(statusCombo.getValue());
        
        userDAO.update(currentMember);
        DialogHelper.showSuccess("Success", "Member updated successfully.");
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    
    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            emailField.setStyle("-fx-border-color: red;");
        } else {
            emailField.setStyle("");
        }
    }
    
    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}