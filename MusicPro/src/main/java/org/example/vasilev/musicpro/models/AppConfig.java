package org.example.vasilev.musicpro.models;

import java.io.*;
import java.util.Properties;

public class AppConfig
{
    private static final String CONFIG_FILE = "musicplayer.properties";
    private static AppConfig instance;
    private Properties properties;

    private static final String DEFAULT_DOWNLOAD_DIR = System.getProperty("user.home") + "\\MusicPlayer\\downloads";
    private static final String DEFAULT_CACHE_DIR = System.getProperty("user.home") + "\\MusicPlayer\\cache";
    private static final String DEFAULT_TEMP_DIR = System.getProperty("java.io.tmpdir") + "MusicPlayer";

    private AppConfig()
    {
        properties = new Properties();
        loadConfig();
    }

    // Получение силглтона //TODO перенести его spring
    public static AppConfig getInstance()
    {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadConfig()
    {
        try (InputStream input = new FileInputStream(CONFIG_FILE))
        {
            properties.load(input);
        }
        catch (IOException e)
        {
            // Файл не существует, создаем с настройками по умолчанию
            setDefaults();
            saveConfig();
        }
    }

    private void setDefaults()
    {
        properties.setProperty("download.dir", DEFAULT_DOWNLOAD_DIR);
        properties.setProperty("cache.dir", DEFAULT_CACHE_DIR);
        properties.setProperty("temp.dir", DEFAULT_TEMP_DIR);
        properties.setProperty("max.concurrent.downloads", "3");
        properties.setProperty("auto.create.dirs", "true");
    }

    public void saveConfig()
    {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE))
        {
            properties.store(output, "Music Player Configuration");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    ///Геттеры
    public String getDownloadDir()
    {
        return properties.getProperty("download.dir", DEFAULT_DOWNLOAD_DIR);
    }

    public String getCacheDir()
    {
        return properties.getProperty("cache.dir", DEFAULT_CACHE_DIR);
    }

    public String getTempDir()
    {
        return properties.getProperty("temp.dir", DEFAULT_TEMP_DIR);
    }

    public int getMaxConcurrentDownloads()
    {
        return Integer.parseInt(properties.getProperty("max.concurrent.downloads", "3"));
    }

    public boolean shouldAutoCreateDirs()
    {
        return Boolean.parseBoolean(properties.getProperty("auto.create.dirs", "true"));
    }


    /// Сеттер
    public void setDownloadDir(String path)
    {
        properties.setProperty("download.dir", path);
        saveConfig();
    }


    // Проверка и создание директорий
    public void ensureDirectoriesExist()
    {
        createDirectoryIfNotExists(getDownloadDir());
        createDirectoryIfNotExists(getCacheDir());
        createDirectoryIfNotExists(getTempDir());
    }


    private void createDirectoryIfNotExists(String path)
    {
        File dir = new File(path);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
    }
}
