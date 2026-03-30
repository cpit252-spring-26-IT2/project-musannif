package org.app.musannif.model;

import java.nio.file.Path;
import java.time.Instant;

public record ScannedFile(Path path, String extension, long sizeBytes, Instant lastModified) {

    @Override
    public String toString() {
        return String.format("ScannedFile{path=%s, ext=%s, size=%d B, modified=%s}",
                path, extension, sizeBytes, lastModified);
    }
}
