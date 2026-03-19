package org.example.vasilev.musicpro.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.example.vasilev.musicpro.services.music.IPageOwner;
import org.example.vasilev.musicpro.services.music.PageChangeEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Контроллер для панели пагинации
 */
public class PaginationController implements Initializable
{
    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageInfoLabel;

    @FXML
    private ComboBox<String> pageSizeComboBox;  // Оставляем String для совместимости с FXML

    private IPageOwner pageOwner;
    private final Consumer<PageChangeEvent> paginationSubscriber = this::onPageChange;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Наполняем ComboBox значениями
        pageSizeComboBox.getItems().addAll("1", "3", "5", "10", "20");

        // Устанавливаем значение по умолчанию
        pageSizeComboBox.setValue("10");

        // Слушаем изменения комбобокса
        pageSizeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    int currentPageSize = Integer.parseInt(newVal);
                    if (pageOwner != null) {
                        pageOwner.setPageSize(currentPageSize);
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем
                }
            }
        });
    }

    /**
     * Установить владельца страницы
     */
    public void setPageOwner(IPageOwner pageOwner)
    {
        this.pageOwner = pageOwner;

        // Подписываемся на события изменения страницы
        if (pageOwner != null)
            pageOwner.subscribe(paginationSubscriber);
    }

    /**
     * Обработчик изменения страницы
     */
    private void onPageChange(PageChangeEvent event)
    {
        javafx.application.Platform.runLater(() ->
        {
            int pageNum = 0;
            int totalPages = 0;
            int currentSize = 10;
            var page = event.getData();
            if (page != null && !page.getItems().isEmpty())
            {
                pageNum = page.getPageNumber();
                totalPages = page.getTotalPages();
                currentSize = page.getPageSize();
            }

            // Обновляем информацию о странице
            pageInfoLabel.setText("Страница " + pageNum + " из " + totalPages);

            // Обновляем состояние кнопок
            prevPageButton.setDisable(pageNum <= 1);
            nextPageButton.setDisable(pageNum >= totalPages);

            // Обновляем размер страницы в комбобоксе, если он изменился
            String sizeStr = String.valueOf(currentSize);
            if (!sizeStr.equals(pageSizeComboBox.getValue())) {
                pageSizeComboBox.setValue(sizeStr);
            }
        });
    }

    /**
     * Обработчик кнопки "Предыдущая страница"
     */
    @FXML
    private void handlePrevPage(ActionEvent event)
    {
        if (pageOwner != null)
        {
            pageOwner.prevPage();
        }
    }

    /**
     * Обработчик кнопки "Следующая страница"
     */
    @FXML
    private void handleNextPage(ActionEvent event)
    {
        if (pageOwner != null)
        {
            pageOwner.nextPage();
        }
    }
}