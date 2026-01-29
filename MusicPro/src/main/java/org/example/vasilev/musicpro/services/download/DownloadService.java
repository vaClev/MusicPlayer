package org.example.vasilev.musicpro.services.download;

import javafx.application.Platform;
import org.example.vasilev.musicpro.services.APIClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class DownloadService implements IDownloadService, AutoCloseable
{
    private final APIClient apiClient;
    private final ExecutorService notificationExecutor;
    private final String defaultDownloadPath;

    private final String endpointDownloadMusic;
    private final String endpointDownloadExtras;

    // Флаг завершения работы
    private volatile boolean isShutdown = false;

    // Список подписчиков на события загрузки
    private final List<Consumer<DownloadEvent>> subscribers = Collections.synchronizedList(new ArrayList<>());;

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

    /// //////////////////////////////////////////////////////////////
    /// Реализация IObservable
    /// /////////////////////////////////////////////////////////////

    @Override
    public void subscribe(Consumer<DownloadEvent> subscriber)
    {
        if (subscriber != null && !subscribers.contains(subscriber))
        {
            subscribers.add(subscriber);
            System.out.println("Новый подписчик добавлен. Всего подписчиков: " + subscribers.size());
        }
    }

    @Override
    public void unsubscribe(Consumer<DownloadEvent> subscriber)
    {
        if (subscriber != null)
        {
            subscribers.remove(subscriber);
            System.out.println("Подписчик удален. Осталось подписчиков: " + subscribers.size());
        }
    }

    @Override
    public List<Consumer<DownloadEvent>> getSubscribers()
    {
        return new ArrayList<>(subscribers); // Возвращаем копию для безопасности
    }

    @Override
    public void clearSubscribers()
    {
        subscribers.clear();
        System.out.println("Все подписчики очищены");
    }

    /// Уведомление подписчиков о событиях
    private void notifySubscribersAsync(DownloadEvent event)
    {
        // Создаем копию списка для безопасной итерации
        List<Consumer<DownloadEvent>> currentSubscribers = getSubscribers();

        if (!currentSubscribers.isEmpty()) {
            notificationExecutor.submit(() -> {
                for (Consumer<DownloadEvent> subscriber : currentSubscribers) {
                    try
                    {
                        subscriber.accept(event);
                    }
                    catch (IllegalStateException e)
                    {
                        // Если возникла ошибка "Not on FX application thread". Это скоре всего UI - подписчик
                        // пробуем через Platform.runLater.
                        Platform.runLater(() -> {
                            try
                            {
                                subscriber.accept(event);
                            }
                            catch (Exception ex)
                            {
                                handleSubscriberError(subscriber, ex);
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        handleSubscriberError(subscriber, e);
                    }
                }
            });
        }
    }

    private void handleSubscriberError(Consumer<DownloadEvent> subscriber, Exception ex)
    {
        System.err.println("Ошибка в подписчике DownloadService: " + subscriber + ex.getMessage());
    }


    /// //////////////////////////////////////////////////////////////
    /// Реализация AutoCloseable
    /// /////////////////////////////////////////////////////////////
    @Override
    public void close()
    {
        shutdown();
    }

    public void shutdown()
    {
        if (isShutdown)
            return;

        System.out.println("DownloadService: начинаем завершение работы...");

        // 1. Останавливаем notificationExecutor
        if (notificationExecutor != null && !notificationExecutor.isShutdown()) {
            try {
                System.out.println("Завершаем notificationExecutor...");
                notificationExecutor.shutdown(); // Плавное завершение

                // Ждем завершения текущих задач (но не более 5 секунд)
                if (!notificationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Принудительное завершение notificationExecutor...");
                    notificationExecutor.shutdownNow(); // Принудительное завершение

                    // Еще раз ждем
                    if (!notificationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("notificationExecutor не завершился корректно");
                    }
                }
            } catch (InterruptedException e) {
                // Восстанавливаем статус прерывания
                Thread.currentThread().interrupt();
                notificationExecutor.shutdownNow();
            }
        }

        // 2. Закрываем APIClient
        apiClient.shutdown();

        // 3. Очищаем подписчиков
        clearSubscribers();

        isShutdown = true;
        System.out.println("DownloadService: работа завершена");
    }

    /// TODO проверки состояния при работе?
    private boolean isShutdown()
    {
        return isShutdown;
    }
}
