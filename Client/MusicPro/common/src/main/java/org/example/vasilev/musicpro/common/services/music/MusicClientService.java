package org.example.vasilev.musicpro.common.services.music;

import com.google.gson.reflect.TypeToken;
import org.example.vasilev.musicpro.common.dto.MusicFileDTO;
import org.example.vasilev.musicpro.common.dto.MusicFileDetailDTO;
import org.example.vasilev.musicpro.common.dto.MusicPageDTO;
import org.example.vasilev.musicpro.common.models.MusicFile;
import org.example.vasilev.musicpro.common.services.APIClient;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MusicClientService implements IMusicClientService
{
    private final APIClient apiClient;

    /// Конструктор для внедрения зависимости сверху
    public MusicClientService(APIClient apiClient)
    {
        this.apiClient = apiClient;
    }

    /// Получение с сервера списка музыкальных файлов OLD
    @Override
    public CompletableFuture<List<MusicFile>> getMusicFiles()
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                /// игнорируем пагинацию. Отправим простой запрос All.
                String url = "api/music/all";

                Type listType = new TypeToken<List<MusicFileDTO>>()
                {
                }.getType();
                List<MusicFileDTO> dtoList = (List<MusicFileDTO>) apiClient.getAsync(url, listType).join();

                return dtoList.stream()
                        .map(MusicFileDTO::toDomainModel)
                        .collect(Collectors.toList());
            } catch (Exception e)
            {
                throw new RuntimeException("Failed to get music files", e);
            }
        });
    }

    /// Получение с сервера списка музыкальных файлов NEW API
    @Override
    public CompletableFuture<MusicPageDTO> getMusicFiles(int pageNumber, int pageSize)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                // Формируем параметры запроса
                Map<String, String> params = new HashMap<>();
                params.put("pageNumber", String.valueOf(pageNumber));
                params.put("pageSize", String.valueOf(pageSize));

                // Строим URL с параметрами -- например api/music?pageNumber=2&pageSize=5
                String url = APIClient.buildUrlWithParams("api/music", params);

                MusicPageDTO page = apiClient.getAsync(url, MusicPageDTO.class).join();

                return page;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to get music files", e);
            }
        });
    }

    /// Получение с сервера детальную информацию о музыкальном файле по его ID
    @Override
    public CompletableFuture<MusicFile> getMusicFileDetails(long musicFileId)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                String url = String.format("api/music/id%d", musicFileId);
                MusicFileDetailDTO dto = apiClient.getAsync(url, MusicFileDetailDTO.class).join();

                return dto.toDomainModel();
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to get music files", e);
            }
        });
    }

    //TODO реализовать
    @Override
    public CompletableFuture<MusicPageDTO> searchMusicFiles(Map<String, String> searchParams, int page, int pageSize)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                // Добавляем параметры пагинации
                searchParams.put("pageNumber", String.valueOf(page));
                searchParams.put("pageSize", String.valueOf(pageSize));

                // Строим URL с параметрами
                String url = APIClient.buildUrlWithParams("api/search", searchParams);

                // Выполняем запрос
                return apiClient.getAsync(url, MusicPageDTO.class).join();
            } catch (Exception e)
            {
                throw new RuntimeException("Failed to search music files", e);
            }
        });
    }
}
