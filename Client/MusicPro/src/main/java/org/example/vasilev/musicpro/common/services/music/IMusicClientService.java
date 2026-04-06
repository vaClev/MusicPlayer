package org.example.vasilev.musicpro.common.services.music;

import org.example.vasilev.musicpro.common.dto.MusicPageDTO;
import org.example.vasilev.musicpro.common.models.MusicFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IMusicClientService
{
    /**
     * Получить порцию музыкальных файлов с сервера
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список музыкальных файлов
     */
    CompletableFuture<MusicPageDTO> getMusicFiles(int pageNumber, int pageSize);

    //OLD версия для проверки
    CompletableFuture<List<MusicFile>> getMusicFiles();

    /**
     * Получить следующую порцию музыкальных файлов
     * @param currentPage текущая страница
     * @param pageSize размер страницы
     * @return список музыкальных файлов
     */
    /// TODO подумать о развитии функциональности
    //CompletableFuture<List<MusicFile>> getNextPage(int currentPage, int pageSize);

    /**
     * Получить детальную информацию о музыкальном файле по ID
     * @param musicFileId ID музыкального файла
     * @return детальная информация о музыкальном файле
     */
    CompletableFuture<MusicFile> getMusicFileDetails(long musicFileId);

    /**
     * Поиск музыкальных файлов по запросу
     * @param searchParams поисковые параметры
     * @param page номер страницы
     * @param pageSize размер страницы
     * @return список найденных музыкальных файлов
     */
    CompletableFuture<MusicPageDTO> searchMusicFiles(Map<String, String> searchParams, int page, int pageSize);
}
