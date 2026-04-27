package org.app.musannif.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class helperMethods {
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    public static String formatDateTime(Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }



    public static void addFolderIcons(Path targetFolder) {
        String systemRoot = System.getenv("SystemRoot");
        if (systemRoot == null) systemRoot = "C:\\Windows";
        String iconDll = systemRoot + "\\System32\\imageres.dll";

        Map<String, Integer> iconMap = Map.of(
                "Documents", 107,
                "Images",    108,
                "Videos",    178,
                "Audio",     103,
                "Archives",  165,
                "Other",     3
        );

        for (Map.Entry<String, Integer> entry : iconMap.entrySet()) {
            Path categoryFolder = targetFolder.resolve(entry.getKey());
            if (!Files.exists(categoryFolder)) continue;

            Path desktopIni = categoryFolder.resolve("desktop.ini");
            try {
                String content = "[.ShellClassInfo]\r\n"
                        + "IconFile=" + iconDll + "\r\n"
                        + "IconIndex=" + entry.getValue() + "\r\n";
                Files.writeString(desktopIni, content);

                DosFileAttributeView iniAttrs = Files.getFileAttributeView(desktopIni, DosFileAttributeView.class);
                iniAttrs.setHidden(true);
                iniAttrs.setSystem(true);

                DosFileAttributeView folderAttrs = Files.getFileAttributeView(categoryFolder, DosFileAttributeView.class);
                folderAttrs.setSystem(true);

            } catch (IOException ex) {
                Logger.getLogger().info("Failed to set icon for: " + categoryFolder);
            }
        }
    }

}
