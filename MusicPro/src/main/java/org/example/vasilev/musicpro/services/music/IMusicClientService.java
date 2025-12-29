package org.example.vasilev.musicpro.services.music;

import org.example.vasilev.musicpro.models.MusicFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IMusicClientService {

    /**
     * Получить порцию музыкальных файлов с сервера
     * @param page номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список музыкальных файлов
     */
    CompletableFuture<List<MusicFile>> getMusicFiles(int page, int pageSize);

    /**
     * Получить следующую порцию музыкальных файлов
     * @param currentPage текущая страница
     * @param pageSize размер страницы
     * @return список музыкальных файлов
     */
    //CompletableFuture<List<MusicFile>> getNextPage(int currentPage, int pageSize);

    /**
     * Получить детальную информацию о музыкальном файле по ID
     * @param musicFileId ID музыкального файла
     * @return детальная информация о музыкальном файле
     */
    CompletableFuture<MusicFile> getMusicFileDetails(long musicFileId);

    /**
     * Скачать музыкальный файл с сервера
     * @param musicFileId ID музыкального файла
     * @param localFilePath путь для сохранения файла локально
     * @return файл, сохраненный локально
     */
    //CompletableFuture<File> downloadMusicFile(long musicFileId, String localFilePath);

    /**
     * Скачать музыкальный файл с сервера в папку по умолчанию
     * @param musicFileId ID музыкального файла
     * @return файл, сохраненный локально
     */
    //CompletableFuture<File> downloadMusicFile(long musicFileId);

    /**
     * Поиск музыкальных файлов по запросу
     * @param query поисковый запрос
     * @param page номер страницы
     * @param pageSize размер страницы
     * @return список найденных музыкальных файлов
     */
    /// TODO подумать о развитии функциональности
    //CompletableFuture<List<MusicFile>> searchMusicFiles(String query, int page, int pageSize);
}
