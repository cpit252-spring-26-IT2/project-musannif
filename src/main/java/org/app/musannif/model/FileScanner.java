package org.app.musannif.model;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

public class FileScanner {

    private final boolean          skipHidden;
    private final int              maxDepth;        // put -1 for unlimited
    private final Set<String>      extensions;      // if empty then no filter will be applied


    private FileScanner(Builder builder) {
        this.skipHidden = builder.skipHidden;
        this.maxDepth   = builder.maxDepth;
        this.extensions = Collections.unmodifiableSet(builder.extensions);
    }


    public List<ScannedFile> scan(Path root) throws IOException {
        List<ScannedFile> results = new ArrayList<>();

        int depth = (maxDepth == -1) ? Integer.MAX_VALUE : maxDepth;

        Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), depth,
                new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                            throws IOException {

                        // Skip the root itself from the hidden check
                        if (!dir.equals(root) && skipHidden && isHidden(dir)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (skipHidden && isHidden(file))  return FileVisitResult.CONTINUE;

                        String ext = extractExtension(file);

                        if (!extensions.isEmpty() && !extensions.contains(ext))
                            return FileVisitResult.CONTINUE;

                        results.add(new ScannedFile(
                                file,
                                ext,
                                attrs.size(),
                                attrs.lastModifiedTime().toInstant()
                        ));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        System.err.println("Could not access: " + file + " — " + exc.getMessage());
                        return FileVisitResult.CONTINUE; // skip unreadable files gracefully
                    }
                });

        return results;
    }


    // Convenience: get a Stream directly
    public Stream<ScannedFile> scanAsStream(Path root) throws IOException {
        return scan(root).stream();
    }


    //  Helper methods
    private boolean isHidden(Path path) {
        try {
            return Files.isHidden(path);
        } catch (IOException e) {
            return false; // if we can't tell, don't skip it
        }
    }

    private String extractExtension(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return (dot != -1 && dot < name.length() - 1)
                ? name.substring(dot + 1).toLowerCase()
                : "";
    }



    //  Builder design pattern
    public static class Builder {
        private boolean     skipHidden = false;
        private int         maxDepth   = -1;
        private Set<String> extensions = new HashSet<>();

        public Builder skipHidden(boolean skip) {
            this.skipHidden = skip;
            return this;
        }

        public Builder maxDepth(int depth) {
            if (depth < -1) throw new IllegalArgumentException("maxDepth must be >= -1");
            this.maxDepth = depth;
            return this;
        }

        public Builder filterByExtensions(String... exts) {
            for (String e : exts)
                this.extensions.add(e.toLowerCase().replace(".", ""));
            return this;
        }

        public FileScanner build() {
            return new FileScanner(this);
        }
    }
}