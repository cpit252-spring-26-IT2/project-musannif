package org.app.musannif.model.category;

/**
 * Each class is a thin declaration — just a name and an extension list.
 * To expand a category, add extensions to its constructor call.
 * To add a brand-new category, create a new class extending ExtensionCategory
 * (or implementing FileCategory directly) and register it in FileCategorizer.
 */
public final class Categories {


    // -------------------------------------------------------------------------

    public static class Documents extends ExtensionCategory {
        public Documents() {
            super("Documents",
                    "pdf", "doc", "docx", "odt", "rtf", "txt", "tex",
                    "md", "csv", "xls", "xlsx", "ods", "ppt", "pptx", "odp"
            );
        }
    }

    // -------------------------------------------------------------------------

    public static class Images extends ExtensionCategory {
        public Images() {
            super("Images",
                    "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg",
                    "tiff", "tif", "ico", "heic", "heif", "raw", "cr2",
                    "nef", "avif"
            );
        }
    }

    // -------------------------------------------------------------------------

    public static class Videos extends ExtensionCategory {
        public Videos() {
            super("Videos",
                    "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm",
                    "m4v", "mpeg", "mpg", "3gp", "ogv", "ts", "vob"
            );
        }
    }

    // -------------------------------------------------------------------------

    public static class Audio extends ExtensionCategory {
        public Audio() {
            super("Audio",
                    "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a",
                    "opus", "aiff", "alac", "mid", "midi", "amr"
            );
        }
    }

    // -------------------------------------------------------------------------

    public static class Archives extends ExtensionCategory {
        public Archives() {
            super("Archives",
                    "zip", "tar", "gz", "bz2", "xz", "7z", "rar",
                    "iso", "tgz", "cab", "deb", "rpm", "jar", "war"
            );
        }
    }
}
