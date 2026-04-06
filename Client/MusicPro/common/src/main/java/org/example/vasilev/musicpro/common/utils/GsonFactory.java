package org.example.vasilev.musicpro.common.utils;

import com.google.gson.Gson;

public class GsonFactory
{
    ///Создает минималистичный Gson без дополнительных настроек
    public static Gson createSimpleGson()
    {
        return new Gson();
    }
}
