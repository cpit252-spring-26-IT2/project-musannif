package org.app.musannif.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.app.musannif.model.FileScanner;
import org.app.musannif.model.ScannedFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class MainController {
    private ObservableList<ScannedFile> scannedFiles = FXCollections.observableArrayList();
    private Path selectedFolder;
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
    private Label lblStatus;
    @FXML
    private TableView<ScannedFile> fileTable;
    @FXML
    private TableColumn<ScannedFile, String> colName;
    @FXML
    private TableColumn<ScannedFile, String> colExt;
    @FXML
    private TableColumn<ScannedFile, String> colSize;
    @FXML
    private TableColumn<ScannedFile, String> colModified;

    @FXML
    private void handleBrowse(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder to Organize");

        // Default to user's home directory
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));

        Stage stage = (Stage) btnBrowse.getScene().getWindow();
        File chosen = chooser.showDialog(stage);
        if (chosen != null) {
            selectedFolder = chosen.toPath();
            txtFolderPath.setText(chosen.getAbsolutePath());
            btnScan.setDisable(false);
            lblStatus.setText("Folder selected. Click Scan Folder to continue.");
            // Clear any previous results
            scannedFiles.clear();
        }
    }

    @FXML
    private void handleScan(ActionEvent event) {
        if (selectedFolder == null) {
            lblStatus.setText("Please select a folder first.");
            return;
        }
        // Reset state
        scannedFiles.clear();
        btnScan.setDisable(true);
        lblStatus.setText("Scanning...");

        // Run scan on background thread so the UI stays responsive
        Task<List<ScannedFile>> scanTask = new Task<>() {
            @Override
            protected List<ScannedFile> call() throws Exception {
                FileScanner scanner = new FileScanner.Builder()
                        .skipHidden(true)
                        .maxDepth(-1)       // unlimited depth
                        .build();

                return scanner.scan(selectedFolder);
            }
        };

        scanTask.setOnSucceeded(e -> {
            scannedFiles.addAll(scanTask.getValue());
            btnScan.setDisable(false);
            lblStatus.setText("Scan complete — " + scannedFiles.size() + " files found.");
        });

        scanTask.setOnFailed(e -> {
            btnScan.setDisable(false);
            lblStatus.setText("Scan failed: " + scanTask.getException().getMessage());
        });
        new Thread(scanTask).start();
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

