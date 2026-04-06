package org.example.vasilev.musicpro.desktop.controllers.details;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.vasilev.musicpro.desktop.models.ExtraFileFX;

import java.awt.*;
import java.net.URI;

///
/// Этот контроллер для одной карточки ExtraFile.
public class ExtraFileCardController
{
    @FXML
    private HBox cardContainer;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label fileTypeLabel;
    @FXML
    private Label fileSizeLabel;
    @FXML
    private Label uploadDateLabel;
    @FXML
    private Button downloadButton;
    @FXML
    private Label fileIconLabel;

    private ExtraFileFX extraFile;
    
    public void setExtraFile(ExtraFileFX extraFile)
    {
        this.extraFile = extraFile;
        updateUI();
    }

    private void updateUI()
    {
        fileNameLabel.setText(extraFile.getDisplayName());
        fileTypeLabel.setText(extraFile.getFileTypeName());
        fileSizeLabel.setText(extraFile.getFormattedFileSize());

        // Форматируем дату загрузки
        if (extraFile.getUploadDate() != null)
        {
            uploadDateLabel.setText(extraFile.getUploadDate()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }
        else
        {
            uploadDateLabel.setText("Неизвестно");
        }

        // Устанавливаем иконку из метода getIcon()
        fileIconLabel.setText(extraFile.getIcon());

        // Делаем кнопку доступной только если файл можно скачать
        downloadButton.setDisable(!extraFile.isDownloadable());

        // Если файл нельзя скачать, меняем текст кнопки
        if (!extraFile.isDownloadable())
        {
            downloadButton.setText("Недоступно");
            downloadButton.setStyle("-fx-background-color: #666666; -fx-text-fill: #aaaaaa;");
        }
    }

    @FXML
    private void handleDownload()
    {
        if (extraFile != null && extraFile.isDownloadable())
        {
            try
            {
                // Открываем в браузере для скачивания
                Desktop.getDesktop().browse(new URI(extraFile.getDownloadExtraUrl()));
                System.out.println("Открытие ссылки для скачивания: " + extraFile.getDisplayName());

                // Можно добавить логику для прямого скачивания через приложение
                // DownloadService.downloadExtraFile(extraFile);
                //TODO
            }
            catch (Exception ex)
            {
                System.err.println("Ошибка открытия ссылки: " + ex.getMessage());
                showCopyLinkDialog();
            }
        }
    }

    private void showCopyLinkDialog()
    {
        // Показываем диалог с ссылкой
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ссылка для скачивания");
        alert.setHeaderText(extraFile.getDisplayName());
        alert.setContentText("Скопируйте ссылку:\n" + extraFile.getDownloadExtraUrl());

        // Добавляем кнопку копирования
        ButtonType copyButton = new ButtonType("Копировать ссылку");
        alert.getButtonTypes().add(copyButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == copyButton) {
                // Копирование в буфер обмена
                javafx.scene.input.Clipboard clipboard =
                        javafx.scene.input.Clipboard.getSystemClipboard();
                javafx.scene.input.ClipboardContent content =
                        new javafx.scene.input.ClipboardContent();
                content.putString(extraFile.getDownloadExtraUrl());
                clipboard.setContent(content);

                showAlert("Успех", "Ссылка скопирована в буфер обмена");
            }
        });
    }

    private void showAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
