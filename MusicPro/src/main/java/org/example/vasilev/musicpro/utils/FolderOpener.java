package org.example.vasilev.musicpro.utils;

import java.awt.Desktop;
import java.io.File;

public class FolderOpener {

    public static boolean openFolder(String folderPath) {
        File folder = new File(folderPath);

        // Создаем папку если не существует
        if (!folder.exists() && !folder.mkdirs()) {
            return false;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(folder);
                return true;
            }
        } catch (Exception e) {
            // Логируем но не показываем пользователю
            System.err.println("Desktop.open failed: " + e.getMessage());
        }

        // Fallback
        return openFolderWithRuntime(folderPath);
    }

    private static boolean openFolderWithRuntime(String folderPath) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                pb = new ProcessBuilder("explorer", folderPath);
            } else if (os.contains("mac")) {
                pb = new ProcessBuilder("open", folderPath);
            } else {
                pb = new ProcessBuilder("xdg-open", folderPath);
            }

            pb.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

