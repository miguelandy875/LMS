package com.lms.controller.modules;

import com.lms.model.User;
import com.lms.dao.UserDAO;
import com.lms.dao.PaginationResult;
import com.lms.util.DialogHelper;
import com.lms.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.lms.controller.dialogs.MemberDialogController;

public class MemberController implements Initializable {
    
    @FXML private TableView<User> membersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, String> createdColumn;






















    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterRole;
    @FXML private ComboBox<String> filterStatus;
    @FXML private Button addMemberBtn;
    @FXML private Button editMemberBtn;
    @FXML private Button deleteMemberBtn;
    @FXML private Button viewMemberBtn;
    @FXML private Button refreshBtn;
    @FXML private Button clearFiltersBtn;
    
    @FXML private Label totalMembersLabel;
    @FXML private Label activeMembersLabel;
    @FXML private Label inactiveMembersLabel;

    // NEW: Pagination components (add these to FXML)
    @FXML private Button firstPageBtn;
    @FXML private Button previousPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Button lastPageBtn;
    @FXML private Label pageInfoLabel;
    @FXML private ComboBox<Integer> pageSizeCombo;
    @FXML private TextField goToPageField;
    @FXML private Button goToPageBtn;

    
    
    private UserDAO userDAO;
    private ObservableList<User> membersList;
    private FilteredList<User> filteredMembers;
    private User currentUser;

    // Pagination state
    private int currentPage = 1;
    private int pageSize = 5; // Default page size
    private PaginationResult<User> currentResult;
    
    private void setupPagination() {
        // Setup page size combo
        pageSizeCombo.getItems().addAll(5);
        pageSizeCombo.setValue(pageSize);
        pageSizeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                pageSize = newVal;
                currentPage = 1; // Reset to first page
                loadMembers();
            }
        });
        
        // Setup pagination buttons
        firstPageBtn.setOnAction(e -> goToFirstPage());
        previousPageBtn.setOnAction(e -> goToPreviousPage());
        nextPageBtn.setOnAction(e -> goToNextPage());
        lastPageBtn.setOnAction(e -> goToLastPage());
        goToPageBtn.setOnAction(e -> goToSpecificPage());
        
        // Setup go to page field
        goToPageField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                goToPageField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
        
        updatePaginationControls();
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdColumn.setCellValueFactory(cellData -> {
            LocalDateTime created = cellData.getValue().getCreatedAt();
            if (created != null) {
                return new SimpleStringProperty(created.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
            return new SimpleStringProperty("N/A");
        });
        
       // Add status cell styling
        statusColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("ACTIVE".equals(status)) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Enable row selection
        membersTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        membersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateButtonStates(newSelection != null));
    }
    
    private void setupFilters() {
        // Setup role filter
        filterRole.getItems().addAll("All Roles", "ADMIN", "LIBRARIAN", "MEMBER");
        filterRole.setValue("All Roles");
        
        // Setup status filter
        filterStatus.getItems().addAll("All Status", "ACTIVE", "INACTIVE");
        filterStatus.setValue("All Status");
        
        // Setup search and filter listeners with debouncing
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1; // Reset to first page when searching
            loadMembers();
        });
        filterRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1; // Reset to first page when filtering
            loadMembers();
        });
        filterStatus.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1; // Reset to first page when filtering
            loadMembers();
        });
    }
    
    private void setupButtons() {
        updateButtonStates(false);
        
        // Setup button tooltips
        addMemberBtn.setTooltip(new Tooltip("Add new member"));
        editMemberBtn.setTooltip(new Tooltip("Edit selected member"));
        deleteMemberBtn.setTooltip(new Tooltip("Delete selected member"));
        viewMemberBtn.setTooltip(new Tooltip("View member details"));
        refreshBtn.setTooltip(new Tooltip("Refresh member list"));
    }
    
    private void updateButtonStates(boolean hasSelection) {
        editMemberBtn.setDisable(!hasSelection);
        deleteMemberBtn.setDisable(!hasSelection);
        viewMemberBtn.setDisable(!hasSelection);
    }
    
// Debug version of MemberController - Add this method to your existing MemberController

private void loadMembers() {
    System.out.println("=== DEBUG: loadMembers() called ===");
    
    try {
        String searchTerm = searchField != null ? searchField.getText() : "";
        String roleFilter = filterRole != null ? filterRole.getValue() : "All Roles";
        String statusFilter = filterStatus != null ? filterStatus.getValue() : "All Status";
        
        System.out.println("Current filters - Search: '" + searchTerm + "', Role: '" + roleFilter + "', Status: '" + statusFilter + "'");
        System.out.println("Current page: " + currentPage + ", Page size: " + pageSize);
        
        // Test database connection first
        System.out.println("Testing database connection...");
        userDAO.testDatabaseUtil();
        
        // Try to get paginated results
        System.out.println("Attempting to get paginated results...");
        
        try {
            currentResult = userDAO.findPaginated(currentPage, pageSize, searchTerm, roleFilter, statusFilter);
            System.out.println("✅ Paginated query successful!");
            
        } catch (Exception e) {
            System.err.println("❌ Paginated query failed, trying fallback method: " + e.getMessage());
            
            // Try fallback method
            try {
                currentResult = userDAO.findPaginatedFallback(currentPage, pageSize, searchTerm, roleFilter, statusFilter);
                System.out.println("✅ Fallback method successful!");
                
            } catch (Exception fallbackError) {
                System.err.println("❌ Both methods failed: " + fallbackError.getMessage());
                fallbackError.printStackTrace();
                
                // Create empty result to prevent UI crashes
                currentResult = new PaginationResult<>(new ArrayList<>(), 0, 1, pageSize);
                DialogHelper.showError("Database Error", 
                    "Could not load members. Please check the console for details.\n\nError: " + e.getMessage());
                return;
            }
        }
        
        // Update UI
        if (currentResult != null) {
            System.out.println("Updating UI with " + currentResult.getData().size() + " records");
            
            membersList = FXCollections.observableArrayList(currentResult.getData());
            membersTable.setItems(membersList);
            
            // Update pagination controls
            updatePaginationControls();
            updateStatistics();
            
            System.out.println("✅ UI updated successfully");
        } else {
            System.err.println("❌ currentResult is null!");
        }
        
    } catch (Exception e) {
        System.err.println("❌ Unexpected error in loadMembers(): " + e.getMessage());
        e.printStackTrace();
        DialogHelper.showError("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
    }
    
    System.out.println("=== DEBUG: loadMembers() finished ===");
}

// Add this initialization method to help debug FXML binding issues
@Override
public void initialize(URL location, ResourceBundle resources) {
    System.out.println("=== DEBUG: MemberController.initialize() called ===");
    
    try {
        userDAO = new UserDAO();
        currentUser = SessionManager.getInstance().getCurrentUser();
        
        System.out.println("UserDAO created: " + (userDAO != null));
        System.out.println("Current user: " + (currentUser != null ? currentUser.getFullName() : "null"));
        
        // Check FXML components
        System.out.println("Checking FXML components...");
        System.out.println("  membersTable: " + (membersTable != null));
        System.out.println("  searchField: " + (searchField != null));
        System.out.println("  filterRole: " + (filterRole != null));
        System.out.println("  filterStatus: " + (filterStatus != null));
        System.out.println("  pageSizeCombo: " + (pageSizeCombo != null));
        
        // Check pagination components
        System.out.println("Checking pagination components...");
        System.out.println("  firstPageBtn: " + (firstPageBtn != null));
        System.out.println("  previousPageBtn: " + (previousPageBtn != null));
        System.out.println("  nextPageBtn: " + (nextPageBtn != null));
        System.out.println("  lastPageBtn: " + (lastPageBtn != null));
        System.out.println("  pageInfoLabel: " + (pageInfoLabel != null));
        System.out.println("  goToPageField: " + (goToPageField != null));
        System.out.println("  goToPageBtn: " + (goToPageBtn != null));
        
        setupTableColumns();
        setupFilters();
        setupButtons();
        
        // Only setup pagination if components exist
        if (pageSizeCombo != null && firstPageBtn != null) {
            setupPagination();
            System.out.println("✅ Pagination setup completed");
        } else {
            System.err.println("❌ Pagination components missing - check FXML file");
        }
        
        loadMembers();
        updateStatistics();
        
        System.out.println("✅ MemberController initialization completed");
        
    } catch (Exception e) {
        System.err.println("❌ Error during MemberController initialization: " + e.getMessage());
        e.printStackTrace();
        DialogHelper.showError("Initialization Error", "Failed to initialize Member Controller: " + e.getMessage());
    }
}

// Safe version of updatePaginationControls with null checks
private void updatePaginationControls() {
    System.out.println("=== DEBUG: updatePaginationControls() called ===");
    
    if (currentResult == null) {
        System.err.println("❌ currentResult is null, cannot update pagination controls");
        return;
    }
    
    try {
        // Update page info label
        if (pageInfoLabel != null) {
            String pageInfo = String.format("Page %d of %d (%d-%d of %d records)",
                currentResult.getCurrentPage(),
                Math.max(1, currentResult.getTotalPages()), // Ensure at least 1 page
                currentResult.getStartRecord(),
                currentResult.getEndRecord(),
                currentResult.getTotalRecords());
            
            pageInfoLabel.setText(pageInfo);
            System.out.println("Page info updated: " + pageInfo);
        } else {
            System.err.println("❌ pageInfoLabel is null");
        }
        
        // Update button states
        if (firstPageBtn != null) firstPageBtn.setDisable(!currentResult.hasPreviousPage());
        if (previousPageBtn != null) previousPageBtn.setDisable(!currentResult.hasPreviousPage());
        if (nextPageBtn != null) nextPageBtn.setDisable(!currentResult.hasNextPage());
        if (lastPageBtn != null) lastPageBtn.setDisable(!currentResult.hasNextPage());
        
        // Update go to page field
        if (goToPageField != null) {
            goToPageField.setPromptText("1-" + Math.max(1, currentResult.getTotalPages()));
        }
        
        System.out.println("✅ Pagination controls updated successfully");
        
    } catch (Exception e) {
        System.err.println("❌ Error updating pagination controls: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private void updateStatistics() {
        if (currentResult != null) {
            // For statistics, we need total counts, not just current page
            // You might want to add a separate method to get total statistics
            totalMembersLabel.setText(String.valueOf(currentResult.getTotalRecords()));
            
            // For now, calculate from current page (you might want to optimize this)
            long active = membersList.stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
            long inactive = membersList.size() - active;
            
            activeMembersLabel.setText(String.valueOf(active));
            inactiveMembersLabel.setText(String.valueOf(inactive));
        }
    }
    
    // Pagination navigation methods
    private void goToFirstPage() {
        currentPage = 1;
        loadMembers();
    }
    
    private void goToPreviousPage() {
        if (currentResult != null && currentResult.hasPreviousPage()) {
            currentPage--;
            loadMembers();
        }
    }
    
    private void goToNextPage() {
        if (currentResult != null && currentResult.hasNextPage()) {
            currentPage++;
            loadMembers();
        }
    }
    
    private void goToLastPage() {
        if (currentResult != null) {
            currentPage = currentResult.getTotalPages();
            loadMembers();
        }
    }
    
    private void goToSpecificPage() {
        String pageText = goToPageField.getText();
        if (pageText != null && !pageText.trim().isEmpty()) {
            try {
                int targetPage = Integer.parseInt(pageText.trim());
                if (currentResult != null && targetPage >= 1 && targetPage <= currentResult.getTotalPages()) {
                    currentPage = targetPage;
                    loadMembers();
                    goToPageField.clear();
                } else {
                    DialogHelper.showError("Invalid Page", 
                        "Please enter a page number between 1 and " + 
                        (currentResult != null ? currentResult.getTotalPages() : 1));
                }
            } catch (NumberFormatException e) {
                DialogHelper.showError("Invalid Input", "Please enter a valid page number.");
            }
        }
    }
    
    // Existing CRUD methods remain the same...
    @FXML
    private void handleAddMember(ActionEvent event) {
        showMemberDialog(null, "Add New Member");
    }
    
    @FXML
    private void handleEditMember(ActionEvent event) {
        User selectedUser = membersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            showMemberDialog(selectedUser, "Edit Member");
        }
    }
    
    @FXML
    private void handleDeleteMember(ActionEvent event) {
        User selectedUser = membersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (selectedUser.getUserId() == currentUser.getUserId()) {
                DialogHelper.showError("Cannot Delete", "You cannot delete your own account.");
                return;
            }
            
            String message = String.format("Are you sure you want to delete member '%s'?\n\nThis action cannot be undone.", 
                selectedUser.getFullName());
            
            if (DialogHelper.showConfirmation("Delete Member", message)) {
                try {
                    userDAO.delete(selectedUser.getUserId());
                    loadMembers(); // Reload current page
                    DialogHelper.showSuccess("Success", "Member deleted successfully.");
                } catch (Exception e) {
                    DialogHelper.showError("Delete Error", "Could not delete member: " + e.getMessage());
                }
            }
        }
    }
    
    @FXML
    private void handleViewMember(ActionEvent event) {
        User selectedUser = membersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            showMemberDetailsDialog(selectedUser);
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadMembers();
        DialogHelper.showSuccess("Refreshed", "Member list has been refreshed.");
    }

    @FXML
    private void handleClearFilter(ActionEvent event) {
        clearFilters();
    }
    
    private void clearFilters() {
        searchField.clear();
        filterStatus.setValue("All Status");
       filterRole.setValue("All Roles");
       loadMembers();
    }
    
    
    private void showMemberDialog(User user, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/member_dialog.fxml"));
            Parent root = loader.load();
            
            MemberDialogController controller = loader.getController();
            controller.setMember(user);
            controller.setMemberController(this);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(membersTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            DialogHelper.showError("Dialog Error", "Could not open member dialog: " + e.getMessage());
        }
    }
    
    private void showMemberDetailsDialog(User user) {
        String details = String.format(
            "Member Details\n\n" +
            "ID: %d\n" +
            "Name: %s\n" +
            "Email: %s\n" +
            "Phone: %s\n" +
            "Gender: %s\n" +
            "Role: %s\n" +
            "Status: %s\n" +
            "Created: %s",
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.getSex(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt() != null ? 
                user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : "N/A"
        );
        
        DialogHelper.showSuccess("Member Details", details);
    }
    
    public void refreshTable() {
        loadMembers(); // This now refreshes the current page
    }
}