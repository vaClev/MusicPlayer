package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.player.MusicMetadataExtractService;
import org.example.vasilev.musicpro.services.player.BasicPlayerService;
import org.example.vasilev.musicpro.services.player.IPlayerService;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

public class PlayerController implements IPlaylistOwner
{
    /// Плейлист
    @FXML
    private VBox playlistContainer;
    @FXML
    private Label playlistCountLabel;

    @FXML
    private Button selectFileButton;
    @FXML
    private Button clearPlaylistButton;

    /// Список элементов плейлиста
    private final ObservableList<MusicFile> playlistItems = FXCollections.observableArrayList();
    private int currentPlaylistIndex = -1;

    /// ////////////////////////////////////
    /// Кнопки воспроизведения и звука
    @FXML
    private Button playPauseButton;

    @FXML private Slider volumeSlider;
    @FXML private Label volumeLabel;
    @FXML private Slider progressSlider;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;

    /// Текущий трек
    @FXML private Label trackTitleLabel;
    @FXML private Label trackArtistLabel;
    /// Статус бар
    @FXML
    private Label playerStatusLabel;

    /// ////////////////////////////////////////////////
    ///Скрыть показать нижнюю панель
    @FXML
    private HBox bottomPanel;
    @FXML
    private Button toggleBottomPanelButton;
    private boolean isBottomPanelVisible = false;
    @FXML
    private SplitPane splitPane; // ссылка на SplitPane из главного окна
    // Метод для установки splitPane из MainController
    public void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }
    /// ////////////////////////////////////////////////

    /// ////////////////////////////////////////////////
    /// Сервис воспроизведения музыки
    private IPlayerService playerService;
    /// Сервис извлечением метаданных из локального файла
    private MusicMetadataExtractService extractService;
    /// ////////////////////////////////////////////////


    public PlayerController()
    {
    }

    public void setServices(IPlayerService playerService, MusicMetadataExtractService extractService)
    {
        this.playerService = playerService;
        this.extractService = extractService;

        // Подписка UI элементов на события сервиса
        setupPlayerBindings();
    }

    @FXML
    public void initialize()
    {
        // Настройка плейлиста
        setupPlaylist();
    }

    /// ////////////////////////////////////////////////
    /// То что касается плейлиста
    private void setupPlaylist()
    {
        // Обновляем счетчик кол-во песен при изменении плейлиста
        playlistItems.addListener((ListChangeListener<MusicFile>) change -> {
            updatePlaylistCount();
        });

        // Инициализация счетчика
        updatePlaylistCount();
    }

    ///Обновить счетчик плейлиста
    private void updatePlaylistCount()
    {
        Platform.runLater(() -> {
            playlistCountLabel.setText("(" + playlistItems.size() + ")");
        });
    }


    @Override
    ///Добавить файл в плейлист
    public void addMusicFileToPlaylist(MusicFile musicFile)
    {
        playlistItems.add(musicFile);

        if(currentPlaylistIndex==-1)
            currentPlaylistIndex = 0;
        updatePlaylistUI();
    }

    @Override
    public void removeMusicFile(long targetId)
    {
        int index = IntStream.range(0, playlistItems.size())
                .filter(i -> playlistItems.get(i).getId() == targetId)
                .findFirst()
                .orElse(-1);

        if(index!=-1)
          removeFromPlaylist(index);
    }

    /// TODO Задать для воспроизведения новый текущий
    @Override
    public void setAsCurrent(long targetId)
    {
        int index = IntStream.range(0, playlistItems.size())
                .filter(i -> playlistItems.get(i).getId() == targetId)
                .findFirst()
                .orElse(-1);

        if(index!=-1 && currentPlaylistIndex!=index)
        {
            currentPlaylistIndex = index;
            if(playerService.isPlaying())
            {
                playFromPlaylist(index);
            }
            updatePlaylistUI();
        }
    }

    private void updatePlaylistUI()
    {
        Platform.runLater(() -> {
           playlistContainer.getChildren().clear();
           for (int i = 0; i < playlistItems.size(); i++)
           {
               MusicFile item = playlistItems.get(i);
               try
               {
                   FXMLLoader loader = new FXMLLoader(
                           getClass().getResource("/org/example/vasilev/musicpro/views/playlistItem.fxml")
                   );
                   HBox playlistItemNode = loader.load();
                   PlaylistItemController controller = loader.getController();
                   controller.setPlaylistItem(item);
                   //controller.setOnPlayListener(this::playFromPlaylist);
                   // Передаем callback с ID
                   controller.setOnRemoveListener(() -> removeMusicFile(item.getId()));

                   // Помечаем текущий трек
                   if (i == currentPlaylistIndex)
                   {
                       controller.setAsCurrent(true);
                       trackArtistLabel.setText(item.getArtist());
                       trackTitleLabel.setText(item.getTitle());
                   }

                   playlistContainer.getChildren().add(playlistItemNode);
               }
               catch (Exception e)
               {
                   System.err.println("Ошибка создания элемента плейлиста: " + e.getMessage());
               }
           }
        });
    }

    ///Удалить трек из плейлиста
    private void removeFromPlaylist(int index)
    {
        if (index >= 0 && index < playlistItems.size())
        {
            playlistItems.remove(index);
            if (currentPlaylistIndex == index)
            {
                currentPlaylistIndex = -1;
            }
            else if (currentPlaylistIndex > index)
            {
                currentPlaylistIndex--;
            }
            updatePlaylistUI();
        }
    }

    /// Очистить плейлист
    private void clearPlaylist()
    {
        playlistItems.clear();
        currentPlaylistIndex = -1;
        updatePlaylistUI();
        playerStatusLabel.setText("Плейлист очищен");

        stopPlaying();
    }
    ///
    /// ////////////////////////////////////////////////

    /// ///////////////////////////////////////////////
    ///Воспроизвести трек из плейлиста
    /// ///////////////////////////////////////////////
    private void playFromPlaylist(int index)
    {
        if (index >= 0 && index < playlistItems.size())
        {
            MusicFile item = playlistItems.get(index);
            currentPlaylistIndex = index;
            totalTimeLabel.setText(item.getFormattedDuration());
            loadAndPlayFile(item.getLocalFile());
            updatePlaylistUI();
        }
    }
    private void loadAndPlayFile(File selectedFile)
    {
        new Thread(() -> {
            boolean success = playerService.loadFile(selectedFile);

            Platform.runLater(() -> {
                if (success)
                {
                    playerService.play();
                    playerStatusLabel.setText("Воспроизведение: " + selectedFile.getName());
                }
                else
                {
                    playerStatusLabel.setText("Ошибка загрузки файла");
                }
            });
        }).start();
    }

    private void stopPlaying()
    {
        // остановка воспроизведения
        playerService.stop();
        trackArtistLabel.setText("выберите трек");
        trackTitleLabel.setText("Не выбрано");
    }



    /// //////////////////////////////////////////////////
    /// Подключение UI элементов к событиям плеера
    /// //////////////////////////////////////////////////
    private void setupPlayerBindings()
    {
        // 1. Громкость
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            playerService.setVolume(newVal.doubleValue() / 100.0);
        });
        playerService.addVolumeChangeListener(volumeLevel ->
                {
                    Platform.runLater(() ->
                    {
                        volumeLabel.setText(String.format("%d%%", (int) Math.ceil(volumeLevel * 100)));
                    });
                });

        // 2.1 Прогресс воспроизведения (пользователь перетаскивает)
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging)
            { // Когда отпустил ползунок
                double percent = progressSlider.getValue() / 100.0;
                playerService.seek(playerService.getTotalDuration().multiply(percent));
            }
        });
        // 2.2 Прогресс воспроизведения (Плеер информирует UI)
        playerService.addTimeChangeListener(duration ->
        {
            Platform.runLater(() -> {
                if (!progressSlider.isValueChanging()) {
                    double percent = duration.toSeconds() / playerService.getDuration();
                    progressSlider.setValue(percent * 100);
                    currentTimeLabel.setText(formatTime(duration));
                }
            });
        });

        // 4. Подписка на изменение состояния
        playerService.addStateChangeListener(state -> {
            Platform.runLater(() -> updatePlayPauseButton(state));
        });

        playerService.addStateChangeListener(state ->{
            if(state.equals(IPlayerService.PlayerState.END_OF_TRACK))
            {
                Platform.runLater(this::playNextSong);
            }
        });

    }

    private void updatePlayPauseButton(IPlayerService.PlayerState state)
    {
        playPauseButton.setText(state == IPlayerService.PlayerState.PLAYING ? "⏸" : "▶");
    }

    private String formatTime(Duration duration)
    {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /// //////////////////////////////////////////////////
    ///  Обработка нажатия кнопок
    /// //////////////////////////////////////////////////
    public void handlePlayPause(ActionEvent actionEvent)
    {
        if(playerService.isFileLoaded())
        {
            playerService.togglePlayPause();
            return;
        }
        else if(!playlistItems.isEmpty())
        {
            playFromPlaylist(currentPlaylistIndex==-1? 0 : currentPlaylistIndex);
            return;
        }

        playerStatusLabel.setText("Добавьте файлы в плейлист. Или выберите файл с диска");
    }


    public void handlePrev(ActionEvent actionEvent)
    {
        if (playlistItems.isEmpty()) return;

        if (currentPlaylistIndex > 0)
        {
            playFromPlaylist(currentPlaylistIndex - 1);
        }
        else
        {
            // Зацикливание: перейти к последнему
            playFromPlaylist(playlistItems.size() - 1);
        }
    }

    public void handleNext(ActionEvent actionEvent)
    {
        playNextSong();
    }
    private void playNextSong()
    {
        if (playlistItems.isEmpty()) return;

        if (currentPlaylistIndex < playlistItems.size() - 1)
        {
            playFromPlaylist(currentPlaylistIndex + 1);
        }
        else
        {
            // Зацикливание: перейти к первому
            playFromPlaylist(0);
        }
    }

    public void handlePlaylist(ActionEvent actionEvent)
    {

    }

    public void handleClearPlaylist(ActionEvent actionEvent)
    {
        clearPlaylist();
    }

    /// //////////////////////////////////////////////////
    /// Добавление файлов в плейлист локально с компьютера
    public void handleAddFilesToPlaylist(ActionEvent actionEvent)
    {
        List<File> selectedFiles = handleSelectFile(actionEvent);
        if (selectedFiles != null && !selectedFiles.isEmpty())
            addFilesToPlaylist(selectedFiles);
    }

    private List<File> handleSelectFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите аудиофайл");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Аудио файлы", "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg", "*.m4a"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Все файлы", "*.*"));

        Stage stage = (Stage) selectFileButton.getScene().getWindow();

        return fileChooser.showOpenMultipleDialog(stage);
    }

    private void addFilesToPlaylist(List<File> selectedFiles)
    {
        for (File file : selectedFiles)
        {
            MusicFile musicFile = extractService.createMusicFileFromLocalFile(file);
            playlistItems.add(musicFile);
        }

        if(currentPlaylistIndex==-1)
            currentPlaylistIndex = 0;

        // Обновляем UI плейлиста
        updatePlaylistUI();

        // Показываем статус
        playerStatusLabel.setText("Добавлено " + selectedFiles.size() + " треков в плейлист");
    }

    /// UI сворачивание разворачивание нижней панели
    public void handleToggleBottomPanel(ActionEvent actionEvent)
    {
        if (bottomPanel == null) return;

        isBottomPanelVisible = !isBottomPanelVisible;

        // Скрываем/показываем панель
        bottomPanel.setVisible(isBottomPanelVisible);
        bottomPanel.setManaged(isBottomPanelVisible);

        // Меняем символ на кнопке
        if (isBottomPanelVisible) {
            toggleBottomPanelButton.setText("−");
            toggleBottomPanelButton.getStyleClass().remove("collapsed");

            // Если панель показывается, увеличиваем высоту playerContainer
            if (splitPane != null) {
                // Устанавливаем разделитель на 50% для списка и 50% для плеера
                splitPane.setDividerPositions(0.5);
            }
        } else {
            toggleBottomPanelButton.setText("+");
            toggleBottomPanelButton.getStyleClass().add("collapsed");

            // Если панель скрывается, сворачиваем плеер
            if (splitPane != null) {
                splitPane.setDividerPositions(1.0);
            }
        }

        // Обновляем статус
        if (playerStatusLabel != null) {
            playerStatusLabel.setText("Плейлист " + (isBottomPanelVisible ? "показан" : "скрыт"));
        }
    }
}
