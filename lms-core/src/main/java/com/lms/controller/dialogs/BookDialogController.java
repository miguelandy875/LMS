package com.lms.controller.dialogs;

import com.lms.dao.BookDAO;
import com.lms.dao.AuthorDAO;
import com.lms.dao.CategoryDAO;
import com.lms.model.Book;
import com.lms.model.Author;
import com.lms.model.Category;
import com.lms.util.Constants;
import com.lms.util.DialogHelper;
import com.lms.util.ValidationUtil;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BookDialogController implements Initializable {
    
    // Form Fields
    @FXML private TextField titleField;
    @FXML private TextArea authorsField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField newCategoryField;
    @FXML private Button addCategoryButton;
    @FXML private TextField pagesField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea notesArea;
    
    // Buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    // Labels for view mode
    @FXML private Label dialogTitle;
    
    // DAOs
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private CategoryDAO categoryDAO;
    
    // State
    private Book book;
    private Stage dialogStage;
    private boolean bookSaved = false;
    private boolean readOnly = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDAOs();
        initializeFields();
        setupValidation();
    }
    
    private void initializeDAOs() {
        bookDAO = new BookDAO();
        authorDAO = new AuthorDAO();
        categoryDAO = new CategoryDAO();
    }
    
    private void initializeFields() {
        // Status options
        statusComboBox.setItems(FXCollections.observableArrayList(
            "AVAILABLE", "ISSUED", "RESERVED"));
        statusComboBox.setValue("AVAILABLE");
        
        // Load categories
        loadCategories();
        
        // Set up authors field placeholder
        authorsField.setPromptText("Enter author names separated by commas\ne.g., Nice Stella, Andy Miguel");
       authorsField.setStyle("-fx-text-fill: #ffffffff;");
          notesArea.setStyle("-fx-text-fill: #ffffffff;");
        // Set up other field prompts
        titleField.setPromptText("Enter book title");
        pagesField.setPromptText("N0 pages");
        yearField.setPromptText("Pub year (YYYY)");
        newCategoryField.setPromptText("add category");
        notesArea.setPromptText("Additional notes (optional)");
    }
    
    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.findAll();
            ObservableList<Category> categoryItems = FXCollections.observableArrayList(categories);
            categoryComboBox.setItems(categoryItems);
        } catch (Exception e) {
            DialogHelper.showError("Error", "Failed to load categories: " + e.getMessage());
        }
    }
    
    private void setupValidation() {
        // Real-time validation for pages field
        pagesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pagesField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // Real-time validation for year field
        yearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,4}")) {
                yearField.setText(oldValue);
            }
        });
        
        // Enable/disable save button based on required fields
        titleField.textProperty().addListener((obs, old, text) -> updateSaveButtonState());
        authorsField.textProperty().addListener((obs, old, text) -> updateSaveButtonState());
        categoryComboBox.valueProperty().addListener((obs, old, category) -> updateSaveButtonState());
        pagesField.textProperty().addListener((obs, old, text) -> updateSaveButtonState());
        yearField.textProperty().addListener((obs, old, text) -> updateSaveButtonState());
    }
    
    private void updateSaveButtonState() {
        if (readOnly) {
            saveButton.setDisable(true);
            return;
        }
        
        boolean isValid = !titleField.getText().trim().isEmpty() &&
                         !authorsField.getText().trim().isEmpty() &&
                         categoryComboBox.getValue() != null &&
                         !pagesField.getText().trim().isEmpty() &&
                         !yearField.getText().trim().isEmpty();
        
        saveButton.setDisable(!isValid);
    }
    
    public void setBook(Book book) {
        this.book = book;
        
        if (book != null) {
            // Populate fields with existing book data
            titleField.setText(book.getTitle());
            authorsField.setText(book.getAuthorsString());
            
            // Set category
            if (book.getCategory() != null) {
                categoryComboBox.setValue(book.getCategory());
            }
            
            pagesField.setText(String.valueOf(book.getPages()));
            yearField.setText(book.getPublicationYear());
            statusComboBox.setValue(book.getStatus());
        }
        
        updateSaveButtonState();
    }
    
    public void setTitle(String title) {
        if (dialogTitle != null) {
            dialogTitle.setText(title);
        }
    }
    
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        
        // Disable all input fields in read-only mode
        titleField.setDisable(readOnly);
        authorsField.setDisable(readOnly);
        categoryComboBox.setDisable(readOnly);
        newCategoryField.setDisable(readOnly);
        addCategoryButton.setDisable(readOnly);
        pagesField.setDisable(readOnly);
        yearField.setDisable(readOnly);
        statusComboBox.setDisable(readOnly);
        notesArea.setDisable(readOnly);
        
        if (readOnly) {
            saveButton.setText("Close");
            cancelButton.setVisible(false);
        }
        
        updateSaveButtonState();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public boolean isBookSaved() {
        return bookSaved;
    }
    
    @FXML
    private void handleAddCategory(ActionEvent event) {
        String categoryName = newCategoryField.getText().trim();
        
        if (categoryName.isEmpty()) {
            DialogHelper.showWarning("Validation Error", "Please enter a category name.");
            return;
        }
        
        try {
            // Check if category already exists
            Category existingCategory = categoryDAO.findByName(categoryName);
            if (existingCategory != null) {
                DialogHelper.showWarning("Category Exists", 
                    "A category with this name already exists.");
                categoryComboBox.setValue(existingCategory);
                newCategoryField.clear();
                return;
            }
            
            // Create new category
            Category newCategory = new Category(categoryName);
            categoryDAO.save(newCategory);
            
            // Refresh categories and select the new one
            loadCategories();
            categoryComboBox.setValue(newCategory);
            newCategoryField.clear();
            
            DialogHelper.showSuccess("Success", "Category added successfully.");
            
        } catch (Exception e) {
            DialogHelper.showError("Error", "Failed to add category: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (readOnly) {
            handleCancel(event);
            return;
        }
        
        if (validateInput()) {
            try {
                Book bookToSave;
                
                if (book == null) {
                    // Creating new book
                    bookToSave = new Book();
                } else {
                    // Editing existing book
                    bookToSave = book;
                }
                
                // Set basic book properties
                bookToSave.setTitle(titleField.getText().trim());
                bookToSave.setPages(Integer.parseInt(pagesField.getText().trim()));
                bookToSave.setPublicationYear(yearField.getText().trim());
                bookToSave.setStatus(statusComboBox.getValue());
                
                // Set category
                Category selectedCategory = categoryComboBox.getValue();
                bookToSave.setCategoryId(selectedCategory.getCatId());
                bookToSave.setCategory(selectedCategory);
                
                // Process authors
                List<Author> authors = processAuthors();
                bookToSave.setAuthors(authors);
                
                // Save book
                if (book == null) {
                    bookDAO.save(bookToSave);
                    DialogHelper.showSuccess("Success", "Book added successfully.");
                } else {
                    bookDAO.update(bookToSave);
                    DialogHelper.showSuccess("Success", "Book updated successfully.");
                }
                
                bookSaved = true;
                dialogStage.close();
                
            } catch (Exception e) {
                DialogHelper.showError("Save Error", "Failed to save book: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        // Title validation
        if (titleField.getText().trim().isEmpty()) {
            errors.append("• Title is required\n");
        }
        
        // Authors validation
        if (authorsField.getText().trim().isEmpty()) {
            errors.append("• At least one author is required\n");
        }
        
        // Category validation
        if (categoryComboBox.getValue() == null) {
            errors.append("• Category is required\n");
        }
        
        // Pages validation
        String pagesText = pagesField.getText().trim();
        if (pagesText.isEmpty()) {
            errors.append("• Number of pages is required\n");
        } else {
            try {
                int pages = Integer.parseInt(pagesText);
                if (pages <= 0) {
                    errors.append("• Number of pages must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Number of pages must be a valid number\n");
            }
        }
        
        // Year validation
        String yearText = yearField.getText().trim();
        if (yearText.isEmpty()) {
            errors.append("• Publication year is required\n");
        } else if (!ValidationUtil.isValidYear(yearText)) {
            errors.append("• Publication year must be a valid 4-digit year\n");
        }
        
        if (errors.length() > 0) {
            DialogHelper.showError("Validation Errors", 
                "Please correct the following errors:\n\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    private List<Author> processAuthors() {
        List<Author> authors = new ArrayList<>();
        String authorsText = authorsField.getText().trim();
        
        // Split by comma and process each author
        String[] authorNames = authorsText.split(",");
        
        for (String authorName : authorNames) {
            authorName = authorName.trim();
            if (!authorName.isEmpty()) {
                // Find or create author
                Author author = authorDAO.findOrCreate(authorName);
                authors.add(author);
            }
        }
        
        return authors;
    }
}