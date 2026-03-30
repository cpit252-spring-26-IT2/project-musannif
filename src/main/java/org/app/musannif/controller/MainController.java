package org.app.musannif.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class MainController {
    @FXML
    private TextField txtFolderPath;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnScan;
    @FXML
    private ProgressBar scanProgress;

    @FXML
    private Button organizeBtn;

    @FXML
    private Button previewBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private void handleBrowse(ActionEvent event) {
        System.out.println("Browse button clicked!");
        // We will add the DirectoryChooser code here later
    }

    @FXML
    private void handleScan(ActionEvent event) {
        System.out.println("Scan button clicked!");
        // We will add the FileScanner code here later
    }

    @FXML
    private void handleApply(ActionEvent event) {
        System.out.println("Apply button clicked!");
        // We will add the Apply code here later
    }

    @FXML
    private void handleOrganize(ActionEvent event) {
        System.out.println("Organize button clicked!");
        // We will add the Organize code here later
    }

    @FXML
    private void handlePreview(ActionEvent event) {
        System.out.println("Preview button clicked!");
        // We will add the Preview code here later
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        System.out.println("History button clicked!");
        // We will add the History code here later
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        System.out.println("Settings button clicked!");
        // We will add the Settings code here later
    }
}

