package org.app.musannif.controller;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.app.musannif.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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
    private Button btnApply;

    @FXML
    private Button btnInfo;

    @FXML
    private Button genTestBtn;

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
    private CheckBox chkByExtension;

    @FXML
    private void initialize() {
        previewBtn.setDisable(true);
        historyBtn.setDisable(true);
        fileTable.setItems(scannedFiles);
        btnScan.setDisable(true);
        btnApply.setDisable(true);
        lblStatus.setText("Select a folder to begin.");
        colName.setCellValueFactory(cellData->
                new SimpleStringProperty(cellData.getValue().path().getFileName().toString()));
        colExt.setCellValueFactory(cellData->
                new SimpleStringProperty(cellData.getValue().extension()));

        colSize.setCellValueFactory(cellData ->
                new SimpleStringProperty(helperMethods.formatFileSize(cellData.getValue().sizeBytes())));

        colModified.setCellValueFactory(cellData ->
                new SimpleStringProperty(helperMethods.formatDateTime(cellData.getValue().lastModified())));
        Logger.getLogger().info("Application Started");

    }

    @FXML
    private void handleBrowse(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder to Organize");
        Logger.getLogger().info("Prompt user to select folder");

        // Default to user's home directory
        chooser.setInitialDirectory(new File(System.getProperty("user.home")+"/.musannif-test"));

        Stage stage = (Stage) btnBrowse.getScene().getWindow();
        File chosen = chooser.showDialog(stage);
        if (chosen != null) {
            selectedFolder = chosen.toPath();
            txtFolderPath.setText(chosen.getAbsolutePath());
            btnScan.setDisable(false);
            lblStatus.setText("Folder selected. Click Scan Folder to continue.");
            Logger.getLogger().info("Folder Selected");
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
        Logger.getLogger().info("Folders scanning");


        // Run scan on background thread so the UI stays responsive
        Task<List<ScannedFile>> scanTask = new Task<>() {
            @Override
            protected List<ScannedFile> call() throws Exception {
                FileScanner scanner = new FileScanner.Builder()
                        .skipHidden(true)
                        .maxDepth(1)       // -1 for unlimited depth
                        .build();
                Logger.getLogger().info("FileScanner Object Created");
                return scanner.scan(selectedFolder);
            }
        };

        scanTask.setOnSucceeded(e -> {
            scannedFiles.addAll(scanTask.getValue());
            btnScan.setDisable(false);
            btnApply.setDisable(false);
            lblStatus.setText("Scan complete — " + scannedFiles.size() + " files found.");
            Logger.getLogger().info("Folder scanning Complete");

        });

        scanTask.setOnFailed(e -> {
            btnScan.setDisable(false);
            lblStatus.setText("Scan failed: " + scanTask.getException().getMessage());
            Logger.getLogger().info("Folder scanning Failed");
        });
        new Thread(scanTask).start();
    }

    @FXML
    private void handleApply(ActionEvent event) throws IOException {
        if (selectedFolder == null || scannedFiles.isEmpty()){
            lblStatus.setText("Nothing to Organize, Scan a folder first.");
            return;
        }

        btnApply.setDisable(true);
        btnScan.setDisable(true);

        lblStatus.setText("Organizing");
        Logger.getLogger().info("Organizing Files in: "+selectedFolder);

        Task<FileOrganizer.OrganizationResult> organizeTask =
                new Task<>() {
                    @Override
                    protected FileOrganizer.OrganizationResult call() throws Exception {
                        FileOrganizerFacade facade = new FileOrganizerFacade.Builder()
                                .skipHidden(true)
                                .maxDepth(1)
                                .withDefaultCategories()
                                .build();
                        return facade.organize(selectedFolder, selectedFolder);
                    }
                };


                   organizeTask.setOnSucceeded(e -> {
                       FileOrganizer.OrganizationResult result = organizeTask.getValue();
                       if (chkByExtension.isSelected())
                           helperMethods.addFolderIcons(selectedFolder);
                       scannedFiles.clear();
                       btnScan.setDisable(false);
                       btnApply.setDisable(false);
                       lblStatus.setText("Done —" + result.movedFiles() + " files, " + result.skippedFiles() + " skipped.");
                       Logger.getLogger().info("Organization Complete: "+ result.movedFiles() + " files moved, " + result.skippedFiles() + " skipped.");
                   });

                   organizeTask.setOnFailed(e -> {
                       btnScan.setDisable(false);
                       btnApply.setDisable(false);
                       lblStatus.setText("Organization Failed: " + organizeTask.getException().getMessage());
                       Logger.getLogger().info("Organization Failed: "+ organizeTask.getException().getMessage());
                   });

                   Desktop.getDesktop().open((selectedFolder).toFile());
                   new Thread(organizeTask).start();



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

    @FXML
    private void handleInfo(ActionEvent event) {

        Hyperlink authorLink1 = new Hyperlink("@nullMuath");
        authorLink1.setOnAction(e -> openURL("https://github.com/nullMuath"));

        Hyperlink authorLink2 = new Hyperlink("@MeCaveman");
        authorLink2.setOnAction(e -> openURL("https://github.com/MeCaveman"));

        Hyperlink repoLink = new Hyperlink("github.com/cpit252-spring-26-IT2/project-musannif");
        repoLink.setOnAction(e -> openURL("https://github.com/cpit252-spring-26-IT2/project-musannif"));

        HBox authors = new HBox(2, authorLink1, new Label("&"), authorLink2);
        authors.setAlignment(Pos.CENTER);
        Label devLabel = new Label("Developed by");
        devLabel.setMaxWidth(Double.MAX_VALUE);
        devLabel.setAlignment(Pos.CENTER);

        VBox content = new VBox(8,
                new Label("a javafx desktop app that organizes and sorts your files."),
                new Label("Version 0.2"),
                devLabel,
                authors,
                new Separator(),
                new Label("Project Repository:"),
                repoLink
        );

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("About Musannif");
        alert.setHeaderText("Musannif - مصنف");
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    private void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleGenerateTestFiles(ActionEvent event) throws IOException, InterruptedException {
        Logger.getLogger().info("Generate Test Files Button Clicked");
        TestFilesGenerator.generate();
        lblStatus.setText("Generating Test Files");
        lblStatus.setText("Files Generated!");
        Desktop.getDesktop().open(new File(System.getProperty("user.home")+"/.musannif-test"));
    }
}

