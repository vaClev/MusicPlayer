package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.vasilev.musicpro.services.music.IPageOwner;
import org.example.vasilev.musicpro.services.music.PageChangeEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
    private Button clearSearchButton;

    @FXML
    private Button searchButton;

    /// Ссылка на метод для обратного вызова поиска
    private IPageOwner pageOwner;
    private final Consumer<PageChangeEvent> searchSubscriber = this::onPageChange;

    private final int startPage = 1;

    /// //////////////////////////////////////
    /// Установка сервиса загрузки страниц
    /// ////////////////////////////////////////
    public void setPageOwner(IPageOwner pageOwner)
    {
        this.pageOwner = pageOwner;
        if (pageOwner != null)
            pageOwner.subscribe(searchSubscriber);
    }

    private void onPageChange(PageChangeEvent event)
    {
        Platform.runLater(() ->
        {
            // Если это обычный режим (не поиск) - очищаем поля
            // TODO можно написать что сейчас поиск неактивен. Или загреить? или свернуть его
            if (event.isNormal())
                clearFields(true);
        });
    }
    /// ////////////////////////////////////////

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
    }

    private void setupEventHandlers()
    {
        // Кнопка очистки
        clearSearchButton.setOnAction(event -> clearFields(false));

        // Кнопка поиска
        searchButton.setOnAction(event ->
        {
            notifySearch();
        });

        // Обработка Enter в полях ввода
        setupEnterKeyHandlers();
    }

    private void setupEnterKeyHandlers()
    {
        searchQueryField.setOnAction(event ->
        {
            notifySearch();
        });

        titleField.setOnAction(event ->
        {
            notifySearch();
        });

        artistField.setOnAction(event ->
        {
            notifySearch();
        });

        genreField.setOnAction(event ->
        {
            notifySearch();
        });

        yearField.setOnAction(event ->
        {
            notifySearch();
        });

    }

    /// Очистка всех полей поиска
    private void clearFields(boolean isFromNormalLoading)
    {
        searchQueryField.clear();
        titleField.clear();
        artistField.clear();
        genreField.clear();
        yearField.clear();
        exactMatchCheckBox.setSelected(false);
        sortByComboBox.setValue("title");
        sortDescCheckBox.setSelected(false);

        if(!isFromNormalLoading)
          notifySearch();
    }

    /// Уведомление о необходимости поиска
    private void notifySearch()
    {
        if (pageOwner != null)
        {
            pageOwner.search(buildSearchParams(), startPage);
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
}