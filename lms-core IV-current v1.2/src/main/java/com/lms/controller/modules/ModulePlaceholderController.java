package com.lms.controller.modules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ModulePlaceholderController implements Initializable {
    
    @FXML private Label moduleIconLabel;
    @FXML private Label moduleNameLabel;
    @FXML private Label moduleDescriptionLabel;
    @FXML private Label statusLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Default status message
        if (statusLabel != null) {
            statusLabel.setText("This module will be implemented in upcoming phases.");
        }
    }
    
    public void setModuleInfo(String name, String icon, String description) {
        if (moduleIconLabel != null) {
            moduleIconLabel.setText(icon);
        }
        
        if (moduleNameLabel != null) {
            moduleNameLabel.setText(name);
        }
        
        if (moduleDescriptionLabel != null) {
            moduleDescriptionLabel.setText(description);
        }
    }
}
