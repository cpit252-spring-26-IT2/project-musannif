package org.app.musannif.model;

import org.app.musannif.model.category.FileCategorizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileOrganizer {

    /**
     * Applies a categorization result by creating category folders in the given target directory
     * and moving each file into its matching category folder.
     *
     * @param categorizedFiles map from category name to files in that category
     * @param targetDirectory  user-selected directory where category folders will be created
     * @return summary of the organization operation
     * @throws IOException if folders cannot be created or files cannot be moved
     */
    public OrganizationResult applyCategorization(
            Map<String, List<ScannedFile>> categorizedFiles,
            Path targetDirectory
    ) throws IOException {
        Objects.requireNonNull(categorizedFiles, "categorizedFiles must not be null");
        Objects.requireNonNull(targetDirectory, "targetDirectory must not be null");

        Files.createDirectories(targetDirectory);

        int movedFiles = 0;
        int skippedFiles = 0;

        for (Map.Entry<String, List<ScannedFile>> entry : categorizedFiles.entrySet()) {
            String categoryName = entry.getKey();
            List<ScannedFile> files = entry.getValue();

            if (files == null || files.isEmpty()) {
                continue;
            }

            Path categoryDirectory = targetDirectory.resolve(categoryName);
            Files.createDirectories(categoryDirectory);

            for (ScannedFile scannedFile : files) {
                if (scannedFile == null || scannedFile.path() == null || !Files.exists(scannedFile.path())) {
                    skippedFiles++;
                    continue;
                }

                Path source = scannedFile.path();
                Path destination = categoryDirectory;

                Files.move(source, destination);
                movedFiles++;
            }
        }

        return new OrganizationResult(movedFiles, skippedFiles);
    }


    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex <= 0) {
            return fileName;
        }

        return fileName.substring(0, dotIndex);
    }

    private String getExtensionWithDot(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex);
    }

    public record OrganizationResult(int movedFiles, int skippedFiles) {
    }
}
