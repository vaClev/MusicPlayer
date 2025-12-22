package org.example.vasilev.musicpro.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>,
        JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_DATE_TIME; // Формат: "2025-12-17T18:26:47.668934Z"

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type,
                                     JsonDeserializationContext context) {
        try {
            String dateStr = json.getAsString();
            // Удаляем 'Z' если есть и парсим
            if (dateStr.endsWith("Z")) {
                dateStr = dateStr.substring(0, dateStr.length() - 1);
            }
            return LocalDateTime.parse(dateStr, FORMATTER);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга даты: " + json.getAsString());
            return null; // Или LocalDateTime.now() для теста
        }
    }

    @Override
    public JsonElement serialize(LocalDateTime date, Type type,
                                 JsonSerializationContext context) {
        return new JsonPrimitive(date.format(FORMATTER));
    }
}