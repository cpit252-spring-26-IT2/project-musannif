package org.app.musannif.model.category;

import org.app.musannif.model.ScannedFile;

import java.util.*;
import java.util.stream.Stream;




/**
 * Categorizes a collection of ScannedFiles into a map keyed by category name.
 *
 * <h3>Design notes</h3>
 * <ul>
 *   <li>Depends only on the {@link FileCategory} interface — never on concrete classes.</li>
 *   <li>Categories are checked in registration order; first match wins.</li>
 *   <li>Files that no category claims land in {@value #FALLBACK_CATEGORY}.</li>
 *   <li>Use {@link Builder} to compose any set of categories at runtime.</li>
 * </ul>
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * FileCategorizer categorizer = new FileCategorizer.Builder()
 *         .register(new Categories.Documents())
 *         .register(new Categories.Images())
 *         .register(new Categories.Videos())
 *         .register(new Categories.Audio())
 *         .register(new Categories.Archives())
 *         .build();
 *
 * List<ScannedFile> files = scanner.scan(somePath);
 * Map<String, List<ScannedFile>> result = categorizer.categorize(files);
 * }</pre>
 */
public class FileCategorizer {

    public static final String FALLBACK_CATEGORY = "Other";

    /** Ordered list — iteration order determines priority (first match wins). */
    private final List<FileCategory> categories;

    private FileCategorizer(Builder builder) {
        // Defensive copy; preserves insertion order (= priority order).
        this.categories = Collections.unmodifiableList(new ArrayList<>(builder.categories));
    }

    // -------------------------------------------------------------------------
    //  Core API
    // -------------------------------------------------------------------------

    /**
     * Categorizes the given files.
     *
     * @param files any collection of ScannedFile objects (e.g. from FileScanner)
     * @return a map from category name → list of files in that category.
     *         The map always contains at least the registered category names
     *         as keys (with empty lists if no files matched); uncategorized
     *         files appear under {@value #FALLBACK_CATEGORY}.
     */
    public Map<String, List<ScannedFile>> categorize(Collection<ScannedFile> files) {
        // Pre-populate map with all known category names so callers always get
        // a consistent key set, even for empty categories.
        Map<String, List<ScannedFile>> result = new LinkedHashMap<>();
        for (FileCategory cat : categories) {
            result.put(cat.getName(), new ArrayList<>());
        }
        result.put(FALLBACK_CATEGORY, new ArrayList<>());

        for (ScannedFile file : files) {
            String targetCategory = resolve(file.extension());
            result.get(targetCategory).add(file);
        }

        return result;
    }

    /** Convenience overload that accepts a Stream (e.g. from FileScanner.scanAsStream). */
    public Map<String, List<ScannedFile>> categorize(Stream<ScannedFile> files) {
        return categorize(files.toList());
    }

    // -------------------------------------------------------------------------
    //  Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the name of the first category that accepts the extension,
     * or {@value #FALLBACK_CATEGORY} if none does.
     */
    private String resolve(String extension) {
        for (FileCategory cat : categories) {
            if (cat.accepts(extension)) {
                return cat.getName();
            }
        }
        return FALLBACK_CATEGORY;
    }

    // -------------------------------------------------------------------------
    //  Builder
    // -------------------------------------------------------------------------

    public static class Builder {

        private final List<FileCategory> categories = new ArrayList<>();

        public Builder register(FileCategory category) {
            Objects.requireNonNull(category, "category must not be null");
            categories.add(category);
            return this;
        }

        public FileCategorizer build() {
            return new FileCategorizer(this);
        }
    }
}