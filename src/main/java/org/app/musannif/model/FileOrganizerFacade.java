package org.app.musannif.model;

import org.app.musannif.model.category.Categories;
import org.app.musannif.model.category.FileCategorizer;
import org.app.musannif.model.category.FileCategory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Facade (structural design pattern) that hides the three-step pipeline
 * (scan → categorize → organize) behind a single {@link #organize} call.
 *
 * <p>
 * The controller or any other caller only needs to know <em>what</em> to
 * organize and <em>where</em> to put the result — all subsystem wiring
 * ({@link FileScanner}, {@link FileCategorizer}, {@link FileOrganizer}) is
 * hidden inside this class.
 * </p>
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * FileOrganizerFacade facade = new FileOrganizerFacade.Builder()
 *         .skipHidden(true)
 *         .maxDepth(1)
 *         .withDefaultCategories()
 *         .build();
 *
 * FileOrganizer.OrganizationResult result = facade.organize(sourcePath, targetPath);
 * System.out.println("Moved: " + result.movedFiles());
 * }</pre>
 */
public class FileOrganizerFacade {

    private final FileScanner scanner;
    private final FileCategorizer categorizer;
    private final FileOrganizer organizer;

    private FileOrganizerFacade(Builder builder) {
        this.scanner = new FileScanner.Builder()
                .skipHidden(builder.skipHidden)
                .maxDepth(builder.maxDepth)
                .build();

        FileCategorizer.Builder categorizerBuilder = new FileCategorizer.Builder();
        for (FileCategory category : builder.categories) {
            categorizerBuilder.register(category);
        }
        this.categorizer = categorizerBuilder.build();

        this.organizer = new FileOrganizer();
    }

    /**
     * Runs the full scan → categorize → organize pipeline.
     *
     * @param sourceDirectory directory to scan for files
     * @param targetDirectory directory where category folders will be created
     * @return summary of how many files were moved or skipped
     * @throws IOException if scanning, folder creation, or file moves fail
     */
    public FileOrganizer.OrganizationResult organize(Path sourceDirectory, Path targetDirectory) throws IOException {
        Objects.requireNonNull(sourceDirectory, "sourceDirectory must not be null");
        Objects.requireNonNull(targetDirectory, "targetDirectory must not be null");

        List<ScannedFile> files = scanner.scan(sourceDirectory);
        Map<String, List<ScannedFile>> categorized = categorizer.categorize(files);
        return organizer.applyCategorization(categorized, targetDirectory);
    }


    public static class Builder {

        private boolean skipHidden = false;
        private int maxDepth = -1;
        private final List<FileCategory> categories = new ArrayList<>();

        public Builder skipHidden(boolean skip) {
            this.skipHidden = skip;
            return this;
        }

        public Builder maxDepth(int depth) {
            if (depth < -1) throw new IllegalArgumentException("maxDepth must be >= -1");
            this.maxDepth = depth;
            return this;
        }

        public Builder addCategory(FileCategory category) {
            Objects.requireNonNull(category, "category must not be null");
            this.categories.add(category);
            return this;
        }

        /**
         * Registers the default set of categories:
         * Documents, Images, Videos, Audio, Archives.
         */
        public Builder withDefaultCategories() {
            this.categories.add(new Categories.Documents());
            this.categories.add(new Categories.Images());
            this.categories.add(new Categories.Videos());
            this.categories.add(new Categories.Audio());
            this.categories.add(new Categories.Archives());
            return this;
        }

        public FileOrganizerFacade build() {
            return new FileOrganizerFacade(this);
        }
    }
}
