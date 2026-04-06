package org.example.vasilev.musicpro.desktop.utils;

import java.awt.Desktop;
import java.io.File;

public class FolderOpener {

    public static boolean openFolder(String folderPath) {
        File folder = new File(folderPath);

        // Создаем папку если не существует
        if (!folder.exists() && !folder.mkdirs()) {
            return false;
        }
        String os = System.getProperty("os.name").toLowerCase();

        /// Windows or MacOS
        if (os.contains("win") || os.contains("mac")) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(folder); // don't work on Ubuntu (Linux)
                    return true;
                }
            } catch (Exception e) {
                // Логируем но не показываем пользователю
                System.err.println("Desktop.open failed: " + e.getMessage());
            }
        }

        /// Linux, and some situation
        return openFolderWithRuntime(folderPath, os);
    }

    private static boolean openFolderWithRuntime(String folderPath, String os)
    {
        ProcessBuilder pb;
        if (os.contains("win"))
            pb = new ProcessBuilder("explorer", folderPath);

        else if (os.contains("mac"))
            pb = new ProcessBuilder("open", folderPath);

        else
            pb = new ProcessBuilder("xdg-open", folderPath);

        try
        {
            pb.start();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}

