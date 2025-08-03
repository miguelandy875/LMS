package com.lms.controller.modules;

import com.lms.dao.BookDAO;
import com.lms.dao.AuthorDAO;
import com.lms.dao.CategoryDAO;
import com.lms.model.Book;
import com.lms.model.Author;
import com.lms.model.Category;
import com.lms.model.BookStatistics;
import com.lms.util.Constants;
import com.lms.util.DialogHelper;
import com.lms.util.SessionManager;
import com.lms.controller.dialogs.BookDialogController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BookController implements Initializable {
    
    // Statistics Cards
    @FXML private Label totalBooksLabel;
    @FXML private Label availableBooksLabel;
    @FXML private Label issuedBooksLabel;
    @FXML private Label reservedBooksLabel;
    @FXML private Label totalAuthorsLabel;
    @FXML private Label totalCategoriesLabel;
    
    // Search and Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private Button clearFiltersButton;
    
    // Action Buttons
    @FXML private Button addBookButton;
    @FXML private Button editBookButton;
    @FXML private Button deleteBookButton;
    @FXML private Button viewBookButton;
    @FXML private Button refreshButton;
    
    // Table and Data
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorsColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private TableColumn<Book, Integer> pagesColumn;
    @FXML private TableColumn<Book, String> yearColumn;
    @FXML private TableColumn<Book, String> statusColumn;
    
    // DAOs
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private CategoryDAO categoryDAO;
    
    // Data
    private ObservableList<Book> books;
    private FilteredList<Book> filteredBooks;
    private SortedList<Book> sortedBooks;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDAOs();
        initializeTable();
        initializeFilters();
        initializeButtonStates();
        loadData();
        setupEventHandlers();
    }
    
    private void initializeDAOs() {
        bookDAO = new BookDAO();
        authorDAO = new AuthorDAO();
        categoryDAO = new CategoryDAO();
    }
    
    private void initializeTable() {
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorsColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getAuthorsString()));
        categoryColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getCategoryName()));
        pagesColumn.setCellValueFactory(new PropertyValueFactory<>("pages"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Style status column based on value
        statusColumn.setCellFactory(column -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    
                    // Apply different styles based on status
                    switch (status.toUpperCase()) {
                        case "AVAILABLE":
                            setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                            break;
                        case "ISSUED":
                            setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            break;
                        case "RESERVED":
                            setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #6c757d;");
                    }
                }
            }
        });
        
        // Initialize data lists
        books = FXCollections.observableArrayList();
        filteredBooks = new FilteredList<>(books, p -> true);
        sortedBooks = new SortedList<>(filteredBooks);
        sortedBooks.comparatorProperty().bind(booksTable.comparatorProperty());
        booksTable.setItems(sortedBooks);
        
        // Enable row selection
        booksTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        booksTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateButtonStates(newSelection != null));
    }
    
    private void initializeFilters() {
        // Status filter
        statusFilter.setItems(FXCollections.observableArrayList(
            "All Status", "AVAILABLE", "ISSUED", "RESERVED", "MAINTENANCE"));
        statusFilter.setValue("All Status");
        
        // Category filter - will be populated when data loads
        categoryFilter.setItems(FXCollections.observableArrayList());
        categoryFilter.setValue(null);
        categoryFilter.setPromptText("All Categories");
        
        // Search field
        searchField.setPromptText("Search by title, author, or category...");
    }
    
    private void initializeButtonStates() {
        updateButtonStates(false);
    }
    
    private void updateButtonStates(boolean hasSelection) {
        editBookButton.setDisable(!hasSelection);
        deleteBookButton.setDisable(!hasSelection);
        viewBookButton.setDisable(!hasSelection);
    }
    
    private void loadData() {
        try {
            // Load books
            List<Book> bookList = bookDAO.findAll();
            books.setAll(bookList);
            
            // Load categories for filter
            List<Category> categories = categoryDAO.findAll();
            ObservableList<Category> categoryItems = FXCollections.observableArrayList();
            categoryItems.add(null); // For "All Categories" option
            categoryItems.addAll(categories);
            categoryFilter.setItems(categoryItems);
            
            // Update statistics
            updateStatistics();
            
        } catch (Exception e) {
            DialogHelper.showError("Data Loading Error", 
                "Failed to load book data: " + e.getMessage());
        }
    }
    
    private void updateStatistics() {
        try {
            BookStatistics stats = calculateStatistics();
            
            totalBooksLabel.setText(String.valueOf(stats.getTotalBooks()));
            availableBooksLabel.setText(String.valueOf(stats.getAvailableBooks()));
            issuedBooksLabel.setText(String.valueOf(stats.getIssuedBooks()));
            reservedBooksLabel.setText(String.valueOf(stats.getReservedBooks()));
            totalAuthorsLabel.setText(String.valueOf(stats.getTotalAuthors()));
            totalCategoriesLabel.setText(String.valueOf(stats.getTotalCategories()));
            
        } catch (Exception e) {
            System.err.println("Error updating statistics: " + e.getMessage());
        }
    }
    
    private BookStatistics calculateStatistics() {
        BookStatistics stats = new BookStatistics();
        
        stats.setTotalBooks(bookDAO.getTotalBooks());
        stats.setAvailableBooks(bookDAO.getBooksByStatus("AVAILABLE"));
        stats.setIssuedBooks(bookDAO.getBooksByStatus("ISSUED"));
        stats.setReservedBooks(bookDAO.getBooksByStatus("RESERVED"));
        stats.setTotalAuthors(authorDAO.getTotalAuthors());
        stats.setTotalCategories(categoryDAO.getTotalCategories());
        
        return stats;
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
        
        // Status filter
        statusFilter.setOnAction(e -> applyFilters());
        
        // Category filter
        categoryFilter.setOnAction(e -> applyFilters());
        
        // Double-click to view/edit
        booksTable.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleViewBook(null);
                }
            });
            return row;
        });
    }
    
    private void applyFilters() {
        filteredBooks.setPredicate(book -> {
            // Search filter
            String searchText = searchField.getText();
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                
                if (!book.getTitle().toLowerCase().contains(lowerCaseFilter) &&
                    !book.getAuthorsString().toLowerCase().contains(lowerCaseFilter) &&
                    !book.getCategoryName().toLowerCase().contains(lowerCaseFilter)) {
                    return false;
                }
            }
            
            // Status filter
            String statusFilter = this.statusFilter.getValue();
            if (statusFilter != null && !statusFilter.equals("All Status")) {
                if (!book.getStatus().equals(statusFilter)) {
                    return false;
                }
            }
            
            // Category filter
            Category categoryFilter = this.categoryFilter.getValue();
            if (categoryFilter != null) {
                if (book.getCategoryId() != categoryFilter.getCatId()) {
                    return false;
                }
            }
            
            return true;
        });
    }
    
    @FXML
    private void handleAddBook(ActionEvent event) {
        try {
            showBookDialog(null, "Add New Book");
        } catch (Exception e) {
            DialogHelper.showError("Error", "Failed to open add book dialog: " + e.getMessage());
        }
    }
    
    @FXML  
    private void handleEditBook(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try {
                showBookDialog(selectedBook, "Edit Book");
            } catch (Exception e) {
                DialogHelper.showError("Error", "Failed to open edit book dialog: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleDeleteBook(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            // Check if book can be deleted (not issued or reserved)
            if ("ISSUED".equals(selectedBook.getStatus()) || "RESERVED".equals(selectedBook.getStatus())) {
                DialogHelper.showWarning("Cannot Delete Book", 
                    "Cannot delete a book that is currently issued or reserved.");
                return;
            }
            
            String message = String.format(
                "Are you sure you want to delete the book:\n\n" +
                "Title: %s\n" +
                "Author(s): %s\n" +
                "Category: %s\n\n" +
                "This action cannot be undone.",
                selectedBook.getTitle(),
                selectedBook.getAuthorsString(),
                selectedBook.getCategoryName());
            
            if (DialogHelper.showConfirmation("Delete Book", message)) {
                try {
                    bookDAO.delete(selectedBook.getBookId());
                    books.remove(selectedBook);
                    updateStatistics();
                    DialogHelper.showSuccess("Success", "Book deleted successfully.");
                } catch (Exception e) {
                    DialogHelper.showError("Delete Error", 
                        "Failed to delete book: " + e.getMessage());
                }
            }
        }
    }
    
    @FXML
    private void handleViewBook(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try {
                showBookDialog(selectedBook, "View Book Details");
            } catch (Exception e) {
                DialogHelper.showError("Error", "Failed to open book details: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
        clearFilters();
        DialogHelper.showSuccess("Refresh", "Book data refreshed successfully.");
    }
    
    @FXML
    private void handleClearFilters(ActionEvent event) {
        clearFilters();
    }
    
    private void clearFilters() {
        searchField.clear();
        statusFilter.setValue("All Status");
        categoryFilter.setValue(null);
        applyFilters();
    }
    
    private void showBookDialog(Book book, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/book_dialog.fxml"));
        Parent root = loader.load();
        
        BookDialogController controller = loader.getController();
        controller.setBook(book);
        controller.setTitle(title);
        controller.setReadOnly("View Book Details".equals(title));
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(addBookButton.getScene().getWindow());
        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);
        
        controller.setDialogStage(dialogStage);
        
        dialogStage.showAndWait();
        
        // Refresh data if book was saved
        if (controller.isBookSaved()) {
            loadData();
        }
    }
}