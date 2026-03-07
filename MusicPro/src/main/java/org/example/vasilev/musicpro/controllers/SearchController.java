package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Контроллер для панели поиска и пагинации
 */
public class SearchController implements Initializable
{

    @FXML
    private TextField searchQueryField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField artistField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField yearField;

    @FXML
    private CheckBox exactMatchCheckBox;

    @FXML
    private ComboBox<String> sortByComboBox;

    @FXML
    private CheckBox sortDescCheckBox;

    @FXML
    private TextField pageSizeField;

    // Кнопки пагинации
    @FXML
    private Label pageInfoLabel;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Button clearSearchButton;

    @FXML
    private Button searchButton;

    /// Ссылка на метод для обратного вызова поиска
    private SearchCallback callback;

    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        initializeControls();
        setupEventHandlers();
    }

    private void initializeControls()
    {
        // Настраиваем ComboBox для сортировки
        sortByComboBox.getItems().addAll(
                "title", "artist", "album", "genre", "year", "duration", "uploadDate"
        );
        sortByComboBox.setValue("title");

        // Устанавливаем размер страницы по умолчанию
        pageSizeField.setText("10");
    }

    private void setupEventHandlers()
    {
        // Кнопки пагинации
        prevPageButton.setOnAction(event ->
        {
            if (currentPage > 1)
            {
                currentPage--;
                notifySearch();
            }
        });

        nextPageButton.setOnAction(event ->
        {
            if (currentPage < totalPages)
            {
                currentPage++;
                notifySearch();
            }
        });

        // Кнопка очистки
        clearSearchButton.setOnAction(event -> clearFields());

        // Кнопка поиска
        searchButton.setOnAction(event ->
        {
            currentPage = 1; // Сбрасываем на первую страницу при новом поиске
            notifySearch();
        });

        // Обработка Enter в полях ввода
        setupEnterKeyHandlers();
    }

    private void setupEnterKeyHandlers()
    {
        searchQueryField.setOnAction(event ->
        {
            currentPage = 1;
            notifySearch();
        });

        titleField.setOnAction(event ->
        {
            currentPage = 1;
            notifySearch();
        });

        artistField.setOnAction(event ->
        {
            currentPage = 1;
            notifySearch();
        });

        genreField.setOnAction(event ->
        {
            currentPage = 1;
            notifySearch();
        });

        yearField.setOnAction(event ->
        {
            currentPage = 1;
            notifySearch();
        });

    }

    /// Очистка всех полей поиска
    private void clearFields()
    {
        searchQueryField.clear();
        titleField.clear();
        artistField.clear();
        genreField.clear();
        yearField.clear();
        exactMatchCheckBox.setSelected(false);
        sortByComboBox.setValue("title");
        sortDescCheckBox.setSelected(false);
        pageSizeField.setText("10");

        currentPage = 1;
        notifySearch();
    }

    /// Уведомление главного контроллера о необходимости поиска
    private void notifySearch()
    {
        if (callback != null)
        {
            callback.onSearch(buildSearchParams(), currentPage, getPageSize());
        }
    }

    /// Сбор параметров поиска из UI
    public Map<String, String> buildSearchParams()
    {
        Map<String, String> params = new HashMap<>();

        addIfNotEmpty(params, "query", searchQueryField.getText());
        addIfNotEmpty(params, "title", titleField.getText());
        addIfNotEmpty(params, "artist", artistField.getText());
        addIfNotEmpty(params, "genre", genreField.getText());
        addIfNotEmpty(params, "year", yearField.getText());


        if (!params.isEmpty())
        {
            if (exactMatchCheckBox.isSelected())
                params.put("exactMatch", "true");

            if (sortByComboBox.getValue() != null && !sortByComboBox.getValue().isEmpty())
                params.put("sortBy", sortByComboBox.getValue());

            if (sortDescCheckBox.isSelected())
                params.put("sortDesc", "true");
        }

        return params;
    }

    private void addIfNotEmpty(Map<String, String> map, String key, String value)
    {
        if (value != null && !value.trim().isEmpty())
        {
            map.put(key, value.trim());
        }
    }

    /// Получение размера страницы из поля ввода
    public int getPageSize()
    {
        try
        {
            return Integer.parseInt(pageSizeField.getText());
        } catch (NumberFormatException e)
        {
            return 10;
        }
    }

    /// Обновление информации о пагинации
    public void updatePagination(int currentPage, int totalPages)
    {
        this.currentPage = currentPage;
        this.totalPages = totalPages;

        Platform.runLater(() ->
        {
            pageInfoLabel.setText("Страница " + currentPage + " из " + totalPages);
            prevPageButton.setDisable(currentPage <= 1);
            nextPageButton.setDisable(currentPage >= totalPages);
        });
    }

    /// Установка колбэка для связи с главным контроллером
    public void setCallback(SearchCallback callback)
    {
        this.callback = callback;
    }

    /// Сброс на первую страницу
    public void resetPage()
    {
        this.currentPage = 1;
    }

    /// Интерфейс для обратного вызова
    public interface SearchCallback
    {
        void onSearch(Map<String, String> searchParams, int page, int pageSize);
    }
}