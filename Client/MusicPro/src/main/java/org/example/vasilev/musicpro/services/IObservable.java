package org.example.vasilev.musicpro.services;

import java.util.List;

/**
 * Интерфейс наблюдаемого объекта (Observer pattern)
 * @param <T> тип подписчика (обычно Consumer<EventType>)
 */
public interface IObservable<T> {

    /**
     * Подписаться на события
     * @param subscriber подписчик
     */
    void subscribe(T subscriber);

    /**
     * Отписаться от событий
     * @param subscriber подписчик
     */
    void unsubscribe(T subscriber);

    /**
     * Получить список всех подписчиков
     * @return список подписчиков
     */
    List<T> getSubscribers();


    /**
     * Проверить, есть ли подписчики
     * @return true если есть подписчики
     */
    default boolean hasSubscribers() {
        return !getSubscribers().isEmpty();
    }

    /**
     * Очистить всех подписчиков
     */
    void clearSubscribers();
}
