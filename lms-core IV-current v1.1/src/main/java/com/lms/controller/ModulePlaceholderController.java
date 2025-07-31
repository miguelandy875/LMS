package com.lms.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ModulePlaceholderController implements Initializable {
    
    @FXML private Label moduleIconLabel;
    @FXML private Label moduleTitleLabel;
    @FXML private Text moduleDescriptionText;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Default initialization
    }
    
    public void setModuleInfo(String title, String icon, String description) {
        moduleTitleLabel.setText(title);
        moduleIconLabel.setText(icon);
        moduleDescriptionText.setText(description);
    }
}