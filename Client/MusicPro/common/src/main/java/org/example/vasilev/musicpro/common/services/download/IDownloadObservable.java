package org.example.vasilev.musicpro.common.services.download;

import org.example.vasilev.musicpro.common.services.IObservable;

import java.util.function.Consumer;

/**
 * Интерфейс наблюдаемого сервиса скачивания
 * Специализированная версия IObservable для DownloadService
 */
public interface IDownloadObservable extends IObservable<Consumer<DownloadEvent>>
{

    /**
     * Подписаться на события скачивания
     * @param subscriber подписчик (обработчик событий DownloadEvent)
     */
    @Override
    void subscribe(Consumer<DownloadEvent> subscriber);

    /**
     * Отписаться от событий скачивания
     * @param subscriber подписчик (обработчик событий DownloadEvent)
     */
    @Override
    void unsubscribe(Consumer<DownloadEvent> subscriber);

    /**
     * private void notifySubscribers(DownloadEvent event)
     * Оповестить подписчиков о событии скачивания
     * @param event событие скачивания
     */
}