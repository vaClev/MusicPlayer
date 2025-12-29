package org.example.vasilev.musicpro.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class GsonFactory
{
    /**
     * Создает настроенный экземпляр Gson с поддержкой LocalDateTime
     */
    //public static Gson createDefaultGson()
    //{
    //    return new GsonBuilder()
    //            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
    //            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'")
    //            .create();
    //}

    /**
     * Создает Gson с красивым форматированием (для отладки)
     */
    //public static Gson createPrettyGson() {
    //    return new GsonBuilder()
    //            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
    //            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'")
    //            .setPrettyPrinting()
    //            .create();
    //}

    /**
     * Создает минималистичный Gson без дополнительных настроек
     */
    public static Gson createSimpleGson()
    {
        return new Gson();
    }
}
