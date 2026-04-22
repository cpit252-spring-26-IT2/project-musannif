package org.app.musannif.model;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TestFilesGenerator {
    private static final List<String> fileTypes = List.of( // a list of file types, 2 files of the same type
            // documents
            "report.pdf", "final_report.pdf",
            "notes.docx", "meeting_notes.docx",
            "readme.txt", "changelog.txt",
            "data.xlsx", "summary_data.xlsx",
            "slides.pptx", "presentation.pptx",
            // images
            "photo.png", "profile_photo.png",
            "banner.jpg", "header_banner.jpg",
            "icon.gif", "loading_icon.gif",
            "logo.svg", "dark_logo.svg",
            "thumbnail.webp", "preview_thumbnail.webp",
            // videos
            "clip.mp4", "intro_clip.mp4",
            "movie.mkv", "backup_movie.mkv",
            "recording.avi", "screen_recording.avi",
            // audio
            "song.mp3", "acoustic_song.mp3",
            "voice.wav", "voice_memo.wav",
            // archives
            "backup.zip", "project_backup.zip",
            "assets.rar", "old_assets.rar",
            "bundle.7z", "release_bundle.7z");


    public static void generate() {
        File userDirectory = new File(System.getProperty("user.home"));
        Path testDir = userDirectory.toPath().resolve(".musannif-test");
        try {

            if (Files.exists(testDir)) {
                deleteRecursively(testDir);
            }

            Files.createDirectory(testDir);

            for (int i = 0; i < fileTypes.size(); i++) {  // loops through the file types list
                String file = fileTypes.get(i);
                Path createdFile = Files.createFile(testDir.resolve(file));
                Instant modifiedTime = Instant.now().plus(i*3, ChronoUnit.DAYS); //modify the date of each file
                Files.setLastModifiedTime(createdFile, FileTime.from(modifiedTime));
                Logger.getLogger().info("File: [" + file + "] has been created in " + testDir);
            }

        } catch (IOException e){
        Logger.getLogger().info("Failed To Generate Test Files: "+e.getMessage());
        }

    }    private static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var entries = Files.list(path)) {
                for (Path entry : entries.toList()) {
                    deleteRecursively(entry);
            }
            }
        }
        Files.delete(path);
    }

}
