package org.example.vasilev.musicpro.common.dto;

import com.google.gson.annotations.SerializedName;
import org.example.vasilev.musicpro.common.models.MusicFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO для ответа сервера со страницей музыкальных файлов.
 * Соответствует JSON структуре нового API с пагинацией.
 */
public class MusicPageDTO
{
    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("hasPreviousPage")
    private boolean hasPreviousPage;

    @SerializedName("hasNextPage")
    private boolean hasNextPage;

    @SerializedName("items")
    private List<MusicFileDTO> items;

    // --- Конструкторы ---
    public MusicPageDTO() {} // Обязательно для Gson


    ///  Геттеры и сеттеры для всех полей DTO
    /// /////////////////////////////////////

    // pageNumber
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    // pageSize
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    // totalPages
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    // totalCount
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    // hasPreviousPage
    public boolean isHasPreviousPage() { return hasPreviousPage; }
    public void setHasPreviousPage(boolean hasPreviousPage) { this.hasPreviousPage = hasPreviousPage; }

    // hasNextPage
    public boolean isHasNextPage() { return hasNextPage; }
    public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }

    // items
    public List<MusicFileDTO> getItems() { return items; }
    public void setItems(List<MusicFileDTO> items) { this.items = items; }

    // Метод для преобразования в список карточек (как было раньше)
    public List<MusicFile> toCardsList()
    {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        return items.stream()
                .map(MusicFileDTO::toDomainModel)
                .collect(Collectors.toList());
    }
}
