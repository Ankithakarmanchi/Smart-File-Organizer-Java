package logic;

import java.io.File;
import java.nio.file.*;
import java.util.*;

import utils.FileUtils;

public class FileOrganizer {

    public static Map<File, File> organize(File folder, List<String> output) {

        Map<File, File> moveHistory = new HashMap<>();

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {

                if (file.isFile()) {

                    String name = file.getName();
                    String category = FileUtils.getCategory(name);
                    String newName = FileUtils.cleanFileName(name);

                    File categoryFolder = new File(folder, category);
                    if (!categoryFolder.exists()) {
                        categoryFolder.mkdir();
                    }

                    try {
                        Path source = file.toPath();
                        Path destination = new File(categoryFolder, newName).toPath();

                        Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);

                        moveHistory.put(destination.toFile(), source.toFile());

                        output.add(name + " → " + category);

                    } catch (Exception e) {
                        output.add(name + " → error");
                    }
                }
            }
        }

        return moveHistory;
    }

    public static void undo(Map<File, File> moveHistory) {
        for (Map.Entry<File, File> entry : moveHistory.entrySet()) {
            try {
                Files.move(entry.getKey().toPath(),
                        entry.getValue().toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.println("Undo failed");
            }
        }
    }
}