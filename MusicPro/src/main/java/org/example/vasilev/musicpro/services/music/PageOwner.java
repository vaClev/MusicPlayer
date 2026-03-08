package org.example.vasilev.musicpro.services.music;

import org.example.vasilev.musicpro.dto.MusicPageDTO;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

public class PageOwner implements IPageOwner
{
    private final IMusicClientService musicClientService;

    // Состояние
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private boolean isSearchMode = false;
    private Map<String, String> currentSearchParams = null;

    // Подписчики со слабыми ссылками
    private final List<WeakReference<Consumer<PageChangeEvent>>> subscribers =
            Collections.synchronizedList(new ArrayList<>());

    public PageOwner(IMusicClientService musicClientService)
    {
        this.musicClientService = musicClientService;
    }

    @Override
    /// Задать размер страницы (сколько карточек песен на странице)
    public void setPageSize(int pageSize)
    {
        if (pageSize > 0 && this.pageSize != pageSize)
        {
            this.pageSize = pageSize;

            // Сбрасываем на первую страницу с новым размером
            if (isSearchMode)
                search(currentSearchParams, 1);
            else
                loadPage(1);
        }
    }

    @Override
    public void loadPage(int page)
    {
        if (page < 1) return;

        this.currentPage = page;
        this.isSearchMode = false;
        this.currentSearchParams = null;

        // Отправляем запрос
        musicClientService.getMusicFiles(currentPage, pageSize)
                .thenAccept(this::handleSuccess)
                .exceptionally(this::handleError);
    }

    private void handleSuccess(MusicPageDTO pageData)
    {
        this.currentPage = pageData.getPageNumber();
        this.totalPages = pageData.getTotalPages();

        // Создаем событие
        PageChangeEvent event = new PageChangeEvent.Builder()
                .type(isSearchMode ? PageChangeEvent.Type.SEARCH : PageChangeEvent.Type.NORMAL)
                .searchParams(currentSearchParams)
                .data(pageData)
                .build();

        notifySubscribers(event);
    }

    private Void handleError(Throwable error)
    {
        PageChangeEvent event = new PageChangeEvent.Builder()
                .type(PageChangeEvent.Type.ERROR)
                .searchParams(currentSearchParams)
                .error(error)
                .build();

        notifySubscribers(event);
        return null;
    }

    //поиск
    @Override
    public void search(Map<String, String> searchParams, int page)
    {
        if (page < 1) return;

        this.currentPage = page;
        this.isSearchMode = true;
        this.currentSearchParams = searchParams != null ? new HashMap<>(searchParams) : new HashMap<>();

        if(searchParams == null || searchParams.isEmpty())
        {
            // Не стучимся зря на сервер. Создаем событие "пустой поисковой запрос"
            PageChangeEvent event = new PageChangeEvent.Builder()
                    .type(PageChangeEvent.Type.SEARCH)
                    .searchParams(currentSearchParams)
                    .build();

            notifySubscribers(event);
        }
        else
        {
            // Отправляем запрос
            musicClientService.searchMusicFiles(currentSearchParams, currentPage, pageSize)
                    .thenAccept(this::handleSuccess)
                    .exceptionally(this::handleError);
        }
    }

    // следующая страница
    @Override
    public void nextPage()
    {
        if (hasNextPage())
        {
            if (isSearchMode)
            {
                search(currentSearchParams, currentPage + 1);
            } else
            {
                loadPage(currentPage + 1);
            }
        }
    }

    // предыдущая страница
    @Override
    public void prevPage()
    {
        if (hasPreviousPage())
        {
            if (isSearchMode)
            {
                search(currentSearchParams, currentPage - 1);
            } else
            {
                loadPage(currentPage - 1);
            }
        }
    }

    /// ////////////////////////////////////////////////
    // IObservable
    /// ///////////////////////////////////////////////
    private void notifySubscribers(PageChangeEvent event)
    {
        cleanupStaleReferences();

        List<WeakReference<Consumer<PageChangeEvent>>> copy;
        synchronized (subscribers)
        {
            copy = new ArrayList<>(subscribers);
        }

        for (WeakReference<Consumer<PageChangeEvent>> ref : copy)
        {
            Consumer<PageChangeEvent> subscriber = ref.get();
            if (subscriber != null)
            {
                try
                {
                    subscriber.accept(event);
                }
                catch (Exception e)
                {
                    System.err.println("Ошибка при уведомлении подписчика: " + e.getMessage());
                }
            }
        }
    }

    private void cleanupStaleReferences()
    {
        synchronized (subscribers)
        {
            subscribers.removeIf(ref -> ref.get() == null);
        }
    }

    // Реализация IObservable
    @Override
    public void subscribe(Consumer<PageChangeEvent> subscriber)
    {
        if (subscriber == null) return;

        cleanupStaleReferences();

        synchronized (subscribers)
        {
            subscribers.add(new WeakReference<>(subscriber));
        }
    }

    @Override
    public void unsubscribe(Consumer<PageChangeEvent> subscriber)
    {
        if (subscriber == null) return;

        synchronized (subscribers)
        {
            subscribers.removeIf(ref ->
            {
                Consumer<PageChangeEvent> existing = ref.get();
                return existing == null || existing == subscriber;
            });
        }
    }

    @Override
    public List<Consumer<PageChangeEvent>> getSubscribers()
    {
        List<Consumer<PageChangeEvent>> active = new ArrayList<>();

        synchronized (subscribers)
        {
            Iterator<WeakReference<Consumer<PageChangeEvent>>> iterator = subscribers.iterator();
            while (iterator.hasNext())
            {
                Consumer<PageChangeEvent> subscriber = iterator.next().get();
                if (subscriber != null)
                {
                    active.add(subscriber);
                } else
                {
                    iterator.remove();
                }
            }
        }

        return active;
    }

    @Override
    public void clearSubscribers()
    {
        synchronized (subscribers)
        {
            subscribers.clear();
        }
    }

    // Геттеры
    @Override
    public int getCurrentPage()
    {
        return currentPage;
    }

    @Override
    public int getPageSize()
    {
        return pageSize;
    }

    @Override
    public int getTotalPages()
    {
        return totalPages;
    }

    @Override
    public boolean isSearchMode()
    {
        return isSearchMode;
    }

    @Override
    public Map<String, String> getCurrentSearchParams()
    {
        return currentSearchParams != null ?
                Collections.unmodifiableMap(currentSearchParams) : null;
    }
}
