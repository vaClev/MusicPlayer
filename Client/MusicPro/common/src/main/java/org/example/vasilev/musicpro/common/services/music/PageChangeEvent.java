package org.example.vasilev.musicpro.common.services.music;

import org.example.vasilev.musicpro.common.dto.MusicPageDTO;

import java.util.Map;

/**
 * Событие изменения страницы
 */
public class PageChangeEvent
{
    public enum Type { NORMAL, SEARCH, ERROR }

    private final Type type;
    private final Map<String, String> searchParams;
    private final MusicPageDTO data;
    private final Throwable error;

    private PageChangeEvent(Builder builder)
    {
        this.type = builder.type;
        this.searchParams = builder.searchParams;
        this.data = builder.data;
        this.error = builder.error;
    }

    public Type getType() { return type; }
    public Map<String, String> getSearchParams() { return searchParams; }
    public MusicPageDTO getData() { return data; }
    public Throwable getError() { return error; }

    public boolean isSearch() { return type == Type.SEARCH; }
    public boolean isNormal() { return type == Type.NORMAL; }
    public boolean isError() { return type == Type.ERROR; }

    public static class Builder
    {
        private Type type;
        private Map<String, String> searchParams;
        private MusicPageDTO data;
        private Throwable error;

        public Builder type(Type type) { this.type = type; return this; }
        public Builder searchParams(Map<String, String> searchParams) { this.searchParams = searchParams; return this; }
        public Builder data(MusicPageDTO data) { this.data = data; return this; }
        public Builder error(Throwable error) { this.error = error; return this; }

        public PageChangeEvent build()
        {
            return new PageChangeEvent(this);
        }
    }
}