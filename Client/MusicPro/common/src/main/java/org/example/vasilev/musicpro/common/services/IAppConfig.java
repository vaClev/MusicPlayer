package org.example.vasilev.musicpro.common.services;

/**
 * Интерфейс для работы с конфигурацией приложения.
 * Абстрагирует способ хранения настроек (файлы, SharedPreferences и т.д.)
 */
public interface IAppConfig {

    /// Геттеры основных параметров
    String getDownloadDir();

    String getCacheDir();

    String getTempDir();

    String getServerUrl();

    int getMaxConcurrentDownloads();

    boolean shouldAutoCreateDirs();


    /// Действия
    void saveConfig();

    void ensureDirectoriesExist();
}
