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
            String categoryName = sanitizeFolderName(entry.getKey());
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
                Path destination = resolveAvailableDestination(categoryDirectory, source.getFileName());

                Files.move(source, destination);
                movedFiles++;
            }
        }

        return new OrganizationResult(movedFiles, skippedFiles);
    }

    /**
     * Creates a safe folder name from a category name.
     * Invalid Windows filename characters are replaced with underscores.
     */
    private String sanitizeFolderName(String folderName) {
        if (folderName == null || folderName.isBlank()) {
            return FileCategorizer.FALLBACK_CATEGORY;
        }

        return folderName.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    /**
     * Resolves a destination path that does not overwrite an existing file.
     *
     * <p>Example:
     * <ul>
     *   <li>photo.jpg</li>
     *   <li>photo (1).jpg</li>
     *   <li>photo (2).jpg</li>
     * </ul>
     */
    private Path resolveAvailableDestination(Path directory, Path fileName) {
        Path destination = directory.resolve(fileName);

        if (!Files.exists(destination)) {
            return destination;
        }

        String originalFileName = fileName.toString();
        String baseName = getBaseName(originalFileName);
        String extension = getExtensionWithDot(originalFileName);

        int counter = 1;
        while (Files.exists(destination)) {
            destination = directory.resolve(baseName + " (" + counter + ")" + extension);
            counter++;
        }

        return destination;
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
