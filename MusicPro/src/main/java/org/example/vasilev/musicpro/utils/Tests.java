package org.example.vasilev.musicpro.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.vasilev.musicpro.dto.MusicFileDTO;
import org.example.vasilev.musicpro.models.MusicFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class Tests
{
    /// Тестовое получение карточек. Из JSON файла. Отладка UI без сервера
    public List<MusicFile> loadMusicFilesFromJson()
    {
        // Путь к файлу в ресурсах
        String jsonPath = "/org/example/vasilev/musicpro/test.json";
        // Создаем Gson
        Gson gson = new Gson();

        try (InputStream inputStream = getClass().getResourceAsStream(jsonPath);
             InputStreamReader reader = new InputStreamReader(inputStream))
        {
            // Проверка, что файл найден
            if (inputStream == null) {
                throw new RuntimeException("Файл не найден: " + jsonPath);
            }

            // Читаем весь JSON в строку для отладки
            String json = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
            System.out.println("JSON содержимое:\n" + json);

            // Десериализация
            List<MusicFileDTO> dtos = gson.fromJson(json,
                    new TypeToken<List<MusicFileDTO>>(){}.getType());

            System.out.println("Успешно прочитано DTO: " + dtos.size());

            return dtos.stream()
                    .map(MusicFileDTO::toDomainModel)
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Ошибка при чтении JSON: " + e.getMessage(), e);
        }
    }
}
