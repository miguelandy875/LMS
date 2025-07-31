package com.lms.controller.modules;

import com.lms.model.User;
import com.lms.dao.UserDAO;
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
    
    @FXML private Label totalMembersLabel;
    @FXML private Label activeMembersLabel;
    @FXML private Label inactiveMembersLabel;
    
    private UserDAO userDAO;
    private ObservableList<User> membersList;
    private FilteredList<User> filteredMembers;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        currentUser = SessionManager.getInstance().getCurrentUser();
        
        setupTableColumns();
        setupFilters();
        setupButtons();
        loadMembers();
        updateStatistics();
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
        
        // Setup search and filter listeners
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterRole.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterStatus.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
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
    
    private void loadMembers() {
        try {
            List<User> users = userDAO.findAll();
            membersList = FXCollections.observableArrayList(users);
            filteredMembers = new FilteredList<>(membersList);
            membersTable.setItems(filteredMembers);
            
            updateStatistics();
        } catch (Exception e) {
            DialogHelper.showError("Database Error", "Could not load members: " + e.getMessage());
        }
    }
    
    private void applyFilters() {
        if (filteredMembers != null) {
            filteredMembers.setPredicate(user -> {
                // Search filter
                String searchText = searchField.getText().toLowerCase().trim();
                boolean matchesSearch = searchText.isEmpty() || 
                    user.getFullName().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText) ||
                    user.getPhone().contains(searchText);
                
                // Role filter
                String roleFilter = filterRole.getValue();
                boolean matchesRole = "All Roles".equals(roleFilter) || 
                    roleFilter.equals(user.getRole());
                
                // Status filter
                String statusFilter = filterStatus.getValue();
                boolean matchesStatus = "All Status".equals(statusFilter) || 
                    statusFilter.equals(user.getStatus());
                
                return matchesSearch && matchesRole && matchesStatus;
            });
        }
    }
    
    private void updateStatistics() {
        if (membersList != null) {
            int total = membersList.size();
            long active = membersList.stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
            long inactive = total - active;
            
            totalMembersLabel.setText(String.valueOf(total));
            activeMembersLabel.setText(String.valueOf(active));
            inactiveMembersLabel.setText(String.valueOf(inactive));
        }
    }
    
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
                    membersList.remove(selectedUser);
                    updateStatistics();
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
        loadMembers();
    }
}