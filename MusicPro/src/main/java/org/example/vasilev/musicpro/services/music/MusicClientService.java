package org.example.vasilev.musicpro.services.music;

import com.google.gson.reflect.TypeToken;
import org.example.vasilev.musicpro.dto.MusicFileDTO;
import org.example.vasilev.musicpro.models.MusicFile;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MusicClientService implements IMusicClientService
{
    private final APIClient apiClient;
    private int currentPage = 0;

    /// Конструктор для внедрения зависимости сверху
    public MusicClientService(APIClient apiClient)
    {
        this.apiClient = apiClient;
    }

    /// Конструктор для отладки
    public MusicClientService()
    {
        this.apiClient = new APIClient();
    }


    @Override
    public CompletableFuture<List<MusicFile>> getMusicFiles(int page, int pageSize)
    {
        return CompletableFuture.supplyAsync(() -> {
            try
            {
                //Map<String, String> params = new HashMap<>();
                //params.put("page", String.valueOf(page));
                //params.put("pageSize", String.valueOf(pageSize));

                ///пока игнорируем пагинацию. Отправим простой запрос All.
                String url = "api/music";

                Type listType = new TypeToken<List<MusicFileDTO>>(){}.getType();
                List<MusicFileDTO> dtoList = (List<MusicFileDTO>) apiClient.getAsync(url, listType).join();

                currentPage = page;
                return  dtoList.stream()
                        .map(MusicFileDTO::toDomainModel)
                        .collect(Collectors.toList());
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to get music files", e);
            }
        });
    }

    @Override
    public CompletableFuture<MusicFile> getMusicFileDetails(long musicFileId)
    {
        return null;
    }
}
