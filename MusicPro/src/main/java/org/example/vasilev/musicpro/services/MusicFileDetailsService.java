package org.example.vasilev.musicpro.services;

import com.google.gson.Gson;
import org.example.vasilev.musicpro.dto.MusicFileDetailDTO;
import org.example.vasilev.musicpro.models.MusicFile;

public class MusicFileDetailsService
{
    private final Gson gson = new Gson();

    public MusicFileDetailsService(/*APIClientService, LocalDBService*/)
    {
    }

    public MusicFile getMusicFileDetails(long musicFileId)
    {
        //IsExistInLocalData?
        try {
            // 1. Получаем JSON от сервера //APIClientService
            String json = makeApiRequest("/api/Music/id" + musicFileId);

            // 2. Парсим в DTO
            MusicFileDetailDTO dto = gson.fromJson(json, MusicFileDetailDTO.class);

            // 3. Конвертируем DTO в модель с ExtraFiles
            MusicFile musicFile = dto.toDomainModel();

            return musicFile;

        }
        catch (Exception e)
        {
            throw new RuntimeException("Ошибка при получении деталей трека", e);
        }
    }

    private String makeApiRequest(String endpoint) {
        // Для тестирования возвращаем фиксированный JSON ответ
        return "{\n" +
                "  \"id\": 1,\n" +
                "  \"title\": \"Coffee And TV\",\n" +
                "  \"artist\": \"Blur\",\n" +
                "  \"album\": \"Blur: The Best Of\",\n" +
                "  \"genre\": null,\n" +
                "  \"year\": 2000,\n" +
                "  \"duration\": \"00:05:18.6810000\",\n" +
                "  \"uploadDate\": \"2025-12-13T23:10:43.647829Z\",\n" +
                "  \"downloadMusicUrl\": \"http://127.0.0.1:5098/api/Music/download/id1\",\n" +
                "  \"extraFiles\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"originalFileName\": \"myFile.txt\",\n" +
                "      \"description\": \"Проверка\",\n" +
                "      \"fileType\": 0,\n" +
                "      \"fileTypeName\": \"SheetMusic\",\n" +
                "      \"fileSize\": 4000000,\n" +
                "      \"uploadDate\": \"2025-12-13T23:58:14.040992Z\",\n" +
                "      \"downloadExtraUrl\": \"http://127.0.0.1:5098/api/ExtraFiles/download/id1\",\n" +
                "      \"musicFileId\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"originalFileName\": \"splin-orbit_bez_sahara_3.gpx\",\n" +
                "      \"description\": \"Ноты\",\n" +
                "      \"fileType\": 1,\n" +
                "      \"fileTypeName\": \"Tabs\",\n" +
                "      \"fileSize\": 44948,\n" +
                "      \"uploadDate\": \"2025-12-17T19:38:48.52778Z\",\n" +
                "      \"downloadExtraUrl\": \"http://127.0.0.1:5098/api/ExtraFiles/download/id2\",\n" +
                "      \"musicFileId\": 1\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
