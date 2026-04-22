package org.app.musannif.model.category;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Convenience base for extension-based categories.
 *
 * Subclasses only need to supply a name and a list of extensions;
 * the accepts() logic is handled here.
 *
 * You can also bypass this base entirely and implement FileCategory
 * directly for categories with non-trivial matching logic
 */

public abstract class ExtensionCategory implements FileCategory {

    private final String      name;
    private final Set<String> extensions;

    /**
     * @param name       display name, used as the map key in categorization results
     * @param extensions lowercase extensions without dots (e.g. "pdf", "jpg")
     */
    protected ExtensionCategory(String name, String... extensions) {
        this.name       = name;
        this.extensions = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(extensions))
        );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean accepts(String extension) {
        return extensions.contains(extension);
    }

    /** Exposes the set so subclasses can inspect or log it if needed. */
    protected Set<String> getExtensions() {
        return extensions;
    }
}
