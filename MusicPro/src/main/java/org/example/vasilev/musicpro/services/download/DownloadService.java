package org.example.vasilev.musicpro.services.download;

import org.example.vasilev.musicpro.services.APIClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class DownloadService implements IDownloadService
{
    private final APIClient apiClient;
    private final ExecutorService notificationExecutor;
    private final String defaultDownloadPath;

    private final String endpointDownloadMusic;
    private final String endpointDownloadExtras;

    // Список подписчиков на события загрузки
    private final List<Consumer<DownloadEvent>> subscribers = new ArrayList<>();

    public DownloadService(APIClient apiClient, String defaultDownloadPath)
    {
        this.apiClient = apiClient;
        this.defaultDownloadPath = defaultDownloadPath;
        this.notificationExecutor = Executors.newSingleThreadExecutor();

        endpointDownloadMusic = "api/Music/download/id";      // например id1
        endpointDownloadExtras = "api/ExtraFiles/download/id";// например id1
    }

    @Override
    public CompletableFuture<File> downloadMusicFile(long musicFileId, String localFilePath)
    {
        CompletableFuture<File> downloadFuture = new CompletableFuture<>();
        if(localFilePath != null && !localFilePath.trim().isEmpty())
        {
            notifySubscribersAsync(DownloadEvent.started(musicFileId, "music", "Начало загрузки"));

            File downloadFile = new File(localFilePath);
            CompletableFuture<File> apiFuture = apiClient.downloadAsync(
                    endpointDownloadMusic+musicFileId,
                    downloadFile,
                    progress -> {
                        notifySubscribersAsync(DownloadEvent.progress(
                                musicFileId, "music", localFilePath, progress));
                    });

            apiFuture.thenAccept(file -> {
                notifySubscribersAsync(DownloadEvent.completed(
                        musicFileId, "music", file.getName(), file.getAbsolutePath()
                ));
                downloadFuture.complete(file);

            }).exceptionally(throwable -> {
                notifySubscribersAsync(DownloadEvent.error(
                        musicFileId, "music", "Ошибка: " + throwable.getMessage()
                ));
                downloadFuture.completeExceptionally(throwable);
                return null;
            });
        }

        return downloadFuture;
    }

    @Override
    public CompletableFuture<File> downloadMusicFile(long musicFileId)
    {
        return downloadMusicFile(musicFileId, generateFilePath(musicFileId));
    }


    private String generateFilePath(long musicFileId)
    {
        return defaultDownloadPath + "\\music_" + musicFileId+".mp3";
    }



    /// TODO
    @Override
    public CompletableFuture<File> downloadExtraFile(long extraFileId, String localFilePath)
    {
        return null;
    }

    @Override
    public CompletableFuture<File> downloadExtraFile(long extraFileId)
    {
        return null;
    }



    /// работа с подписчиками Observable
    private void notifySubscribersAsync(DownloadEvent event)
    {
    }
    /// TODO добавить методы для подписки на события
}
