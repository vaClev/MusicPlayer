package org.example.vasilev.musicpro.controllers.details;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.example.vasilev.musicpro.models.ExtraFile;
import org.example.vasilev.musicpro.models.MusicFile;

import java.io.IOException;
import java.util.List;

///
///Этот контроллер для одной карточки полной информации MusicFile.
/// При наличии списка ExtraFiles создает каждому карточку и внедряет в предусмотренный формой контейнер.
public class MusicDetailsController
{
    @FXML
    private Button downloadButton;
    // Главная секция
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label durationLabel;

    // Дополнительная информация
    @FXML
    private Label albumLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label fileSizeLabel;
    @FXML
    private Label uploadDateLabel;

    // Статус загрузки
    @FXML private Label downloadStatusLabel;
    @FXML private ProgressIndicator downloadProgress;

    // Extra Files секции с группировкой по типам
    @FXML private VBox sheetMusicContainer;
    @FXML private VBox tabsContainer;
    @FXML private VBox lyricsContainer;
    @FXML private VBox chordsContainer;
    @FXML private VBox imagesContainer;
    @FXML private VBox otherFilesContainer;

    // Панели для группировки
    @FXML private TitledPane sheetMusicPane;
    @FXML private TitledPane tabsPane;
    @FXML private TitledPane lyricsPane;
    @FXML private TitledPane chordsPane;
    @FXML private TitledPane imagesPane;
    @FXML private TitledPane otherFilesPane;
    /// Модельный объект отображаемый этой формой
    private MusicFile currentMusicFile;

    public void setMusicFile(MusicFile musicFile) 
    {
        this.currentMusicFile = musicFile;
        updateUI();
        loadExtraFiles();
    }

    private void updateUI()
    {
        // Главная секция
        titleLabel.setText(currentMusicFile.getTitle());
        artistLabel.setText(currentMusicFile.getArtist());
        durationLabel.setText(currentMusicFile.getFormattedDuration());

        // Дополнительная информация
        albumLabel.setText(currentMusicFile.getAlbum());
        genreLabel.setText(currentMusicFile.getGenre());

        Integer year = currentMusicFile.getYear();
        yearLabel.setText(year != null && year > 0 ? year.toString() : "Не указан");

        fileSizeLabel.setText(currentMusicFile.getFormattedFileSize());
        uploadDateLabel.setText(currentMusicFile.getFormattedUploadDate());

        // Статус загрузки
        updateDownloadStatus();
    }

    private void updateDownloadStatus()
    {
        boolean isDownloaded = currentMusicFile.isDownloaded();
        if (isDownloaded)
        {
            downloadStatusLabel.setText("✓ Загружено локально");
            downloadStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
        }
        else
        {
            downloadStatusLabel.setText("Доступно для загрузки");
            downloadStatusLabel.setStyle("-fx-text-fill: #FF9800;");
        }
        // Загреивание кнопки скачивания, если файл уже скачан
        downloadButton.setDisable(isDownloaded);
        downloadProgress.setVisible(false);
    }


    private void loadExtraFiles() {
        clearAllContainers();

        // Загружаем файлы по типам
        loadExtraFilesByType(currentMusicFile.getSheetMusic(), sheetMusicContainer);
        loadExtraFilesByType(currentMusicFile.getTabs(), tabsContainer);
        loadExtraFilesByType(currentMusicFile.getLyrics(), lyricsContainer);
        loadExtraFilesByType(currentMusicFile.getChords(), chordsContainer);
        loadExtraFilesByType(currentMusicFile.getImages(), imagesContainer);
        loadExtraFilesByType(currentMusicFile.getOtherFiles(), otherFilesContainer);

        // Скрываем пустые секции
        hideEmptySections();
    }

    private void clearAllContainers()
    {
        sheetMusicContainer.getChildren().clear();
        tabsContainer.getChildren().clear();
        lyricsContainer.getChildren().clear();
        chordsContainer.getChildren().clear();
        imagesContainer.getChildren().clear();
        otherFilesContainer.getChildren().clear();
    }

    /// Создае м карточки ExtraFiles и добавлям их в указанную панель
    private void loadExtraFilesByType(List<ExtraFile> files, VBox container) {
        if (files.isEmpty()) return;

        for (ExtraFile extraFile : files)
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/vasilev/musicpro/views/details/extra-file-card.fxml")
                );
                Node card = loader.load();
                ExtraFileCardController controller = loader.getController();
                controller.setExtraFile(extraFile);

                container.getChildren().add(card);
            }
            catch (IOException e)
            {
                System.err.println("Ошибка загрузки карточки файла: " + e.getMessage());
            }
        }
    }

    private void hideEmptySections()
    {
        sheetMusicPane.setVisible(!sheetMusicContainer.getChildren().isEmpty());
        tabsPane.setVisible(!tabsContainer.getChildren().isEmpty());
        lyricsPane.setVisible(!lyricsContainer.getChildren().isEmpty());
        chordsPane.setVisible(!chordsContainer.getChildren().isEmpty());
        imagesPane.setVisible(!imagesContainer.getChildren().isEmpty());
        otherFilesPane.setVisible(!otherFilesContainer.getChildren().isEmpty());
    }

    @FXML
    public void handleDownloadMusic(ActionEvent actionEvent)
    {
    }

    @FXML
    public void handleAddToPlaylist(ActionEvent actionEvent)
    {
    }

}
