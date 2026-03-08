package org.example.vasilev.musicpro.services.music;

import org.example.vasilev.musicpro.services.IObservable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Интерфейс владельца страницы с пагинацией
 */
public interface IPageOwner extends IObservable<Consumer<PageChangeEvent>>
{

    /// Установить размер страницы
    void setPageSize(int pageSize);

    /**
     * Загрузить обычную страницу
     * @param page номер страницы
     */
    void loadPage(int page);

    /**
     * Выполнить поиск
     * @param searchParams параметры поиска
     * @param page номер страницы
     */
    void search(Map<String, String> searchParams, int page);

    /// Перейти на следующую страницу
    void nextPage();

    /// Перейти на предыдущую страницу
    void prevPage();

    /// Получить текущий номер страницы
    int getCurrentPage();

    /// Получить размер страницы
    int getPageSize();

    ///Получить общее количество страниц
    int getTotalPages();

    ///Проверить, в режиме ли поиска ?
    boolean isSearchMode();

    /// ///////////////////////////////
    /// с default реализацией
    /// //////////////////////////////
    /// Есть ли следующая страница ?
    default boolean hasNextPage()
    {
        return getCurrentPage() < getTotalPages();
    }

    ///Есть ли предыдущая страница
    default boolean hasPreviousPage()
    {
        return getCurrentPage() > 1;
    }
}
