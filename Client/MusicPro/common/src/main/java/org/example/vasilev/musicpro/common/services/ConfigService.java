package org.example.vasilev.musicpro.common.services;

import java.io.File;

public class ConfigService
{
    private IAppConfig config;

    public ConfigService(IAppConfig config)
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

    public IAppConfig getConfig()
    {
        return config;
    }
}
