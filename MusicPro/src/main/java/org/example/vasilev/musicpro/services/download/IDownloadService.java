package org.example.vasilev.musicpro.services.download;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface IDownloadService extends IDownloadObservable
{
    /**
     * Скачать музыкальный файл с сервера
     * @param musicFileId ID музыкального файла
     * @param localFilePath путь для сохранения файла локально
     * @return файл, сохраненный локально
     */
    CompletableFuture<File> downloadMusicFile(long musicFileId, String localFilePath);


    /**
     * Скачать музыкальный файл с сервера в папку по умолчанию
     * @param musicFileId ID музыкального файла
     * @return файл, сохраненный локально
     */
    CompletableFuture<File> downloadMusicFile(long musicFileId);


    /**
     * Скачать музыкальный файл с сервера
     * @param extraFileId ID музыкального файла
     * @param localFilePath путь для сохранения файла локально
     * @return файл, сохраненный локально
     */
    CompletableFuture<File> downloadExtraFile(long extraFileId, String localFilePath);


    /**
     * Скачать музыкальный файл с сервера в папку по умолчанию
     * @param extraFileId ID музыкального файла
     * @return файл, сохраненный локально
     */
    CompletableFuture<File> downloadExtraFile(long extraFileId);
}
