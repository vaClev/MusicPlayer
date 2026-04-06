package org.example.vasilev.musicpro.common.services;

import org.example.vasilev.musicpro.desktop.services.AppConfig;

import java.io.File;

public class ConfigService
{
    private AppConfig config;

    public ConfigService(AppConfig config)
    {
        this.config = config;
        initializeDirectories();
    }

    private void initializeDirectories()
    {
        if (config.shouldAutoCreateDirs())
            config.ensureDirectoriesExist();
    }

    //Сконструирует и вернет полный путь до скаченного файла
    public String getDownloadPath(String filename)
    {
        return config.getDownloadDir() + File.separator + filename;
    }

    public AppConfig getConfig()
    {
        return config;
    }
}
