package org.example.vasilev.musicpro.desktop.services;

import org.example.vasilev.musicpro.common.services.IAppConfig;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig implements IAppConfig
{
    private static final String CONFIG_FILE = "musicplayer.properties";
    private static AppConfig instance;
    private Properties properties;

    // Используем Paths.get() для кросс-платформенных путей
    private static final String DEFAULT_DOWNLOAD_DIR =
            Paths.get(System.getProperty("user.home"), "MusicPlayer", "downloads").toString();

    private static final String DEFAULT_CACHE_DIR =
            Paths.get(System.getProperty("user.home"), "MusicPlayer", "cache").toString();

    private static final String DEFAULT_TEMP_DIR =
            Paths.get(System.getProperty("java.io.tmpdir"), "MusicPlayer").toString();
    private static final String DEFAULT_SERVER_URL = "http://127.0.0.1:5098/";

    private AppConfig()
    {
        properties = new Properties();
        loadConfig();
    }

    /// Получение силглтона
    public static AppConfig getInstance()
    {
        if (instance == null)
        {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadConfig()
    {
        File configFile = findConfigFile();// Ищем конфиг в нескольких местах

        if (configFile.exists())
        {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            }
            catch (IOException e)
            {
                System.err.println("Ошибка загрузки конфига: " + e.getMessage());
                setDefaults();
            }
        }
        else
        {
            // Файл не существует, создаем с настройками по умолчанию
            setDefaults();
            saveConfig();
        }

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

    private File findConfigFile()
    {
        // 1. Текущая директория - Windows
        File currentDir = new File(CONFIG_FILE);
        if (currentDir.exists()) return currentDir;

        // 2. Домашняя директория + MusicPlayer - Linux variant   "~/MusicPlayer/"
        File appDirLinux = new File(Paths.get(System.getProperty("user.home"), "MusicPlayer", CONFIG_FILE).toString());
        if (appDirLinux.exists()) return appDirLinux;

        // 2.1 Домашняя директория variant
        File homeDir = new File(System.getProperty("user.home"), CONFIG_FILE);
        if (homeDir.exists()) return homeDir;

        // 3. Директория приложения (для macOS .app)
        String appDirMac = System.getProperty("app.dir", ".");
        File appConfig = new File(appDirMac, CONFIG_FILE);
        if (appConfig.exists()) return appConfig;

        return currentDir; // вернем текущую для создания нового
    }

    private void setDefaults()
    {
        properties.setProperty("download.dir", DEFAULT_DOWNLOAD_DIR);
        properties.setProperty("cache.dir", DEFAULT_CACHE_DIR);
        properties.setProperty("temp.dir", DEFAULT_TEMP_DIR);
        properties.setProperty("server.url", DEFAULT_SERVER_URL);
        properties.setProperty("max.concurrent.downloads", "3");
        properties.setProperty("auto.create.dirs", "true");
    }

    @Override
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


    /// Геттеры
    @Override
    public String getDownloadDir()
    {
        return properties.getProperty("download.dir", DEFAULT_DOWNLOAD_DIR);
    }

    @Override
    public String getCacheDir()
    {
        return properties.getProperty("cache.dir", DEFAULT_CACHE_DIR);
    }

    @Override
    public String getTempDir()
    {
        return properties.getProperty("temp.dir", DEFAULT_TEMP_DIR);
    }

    @Override
    public String getServerUrl()
    {
        return properties.getProperty("server.url", DEFAULT_SERVER_URL);
    }

    @Override
    public int getMaxConcurrentDownloads()
    {
        return Integer.parseInt(properties.getProperty("max.concurrent.downloads", "3"));
    }

    @Override
    public boolean shouldAutoCreateDirs()
    {
        return Boolean.parseBoolean(properties.getProperty("auto.create.dirs", "true"));
    }

    /// Проверка и создание директорий
    @Override
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
