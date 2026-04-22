package org.app.musannif.model.category;

/**
 * To add a new category (e.g. "Fonts"), simply create a class that implements
 * this interface and register it with FileCategorizer — no other code changes needed.
 */
public interface FileCategory {

    /**
     * The display name used as the key in the categorization result map.
     * e.g. "Documents", "Images", "Other"
     */
    String getName();

    /**
     * @param extension lowercase extension without a leading dot (e.g. "pdf", "mp4", "")
     */
    boolean accepts(String extension);
}
