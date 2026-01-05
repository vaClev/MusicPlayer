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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.player.BasicPlayerService;
import org.example.vasilev.musicpro.services.player.IPlayerService;

import java.io.File;
import java.io.IOException;
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

    /// Кнопки воспроизведения и звука
    @FXML
    private Button playPauseButton;

    /// Статус бар
    @FXML
    private Label playerStatusLabel;


    /// Сервис воспроизведения музыки
    private IPlayerService playerService;

    /// Список элементов плейлиста //TODO выделить в отдельный класс?
    private final ObservableList<MusicFile> playlistItems = FXCollections.observableArrayList();
    private int currentPlaylistIndex = -1;


    public PlayerController()
    {
        // Используем базовую реализацию
        this.playerService = new BasicPlayerService();

        // Добавляем слушателей событий сервиса для реактивного обновления UI ///TODO
        //setupServiceListeners();
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

    ///Добавить локальный файл в плейлист
    private void addLocalFileToPlaylist(File file)
    {
        /// TODO добавить возможность выбора файла с диска
    }

    @Override
    ///Добавить файл в плейлист
    public void addMusicFileToPlaylist(MusicFile musicFile)
    {
        playlistItems.add(musicFile);
        updatePlaylistUI();
    }

    @Override
    public void removeMusicFile(long targetId)
    {
        int index = IntStream.range(0, playlistItems.size())
                .filter(i -> playlistItems.get(i).getId() == targetId)
                .findFirst()
                .orElse(-1);

        removeFromPlaylist(index);
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
                   //controller.setOnRemoveListener(this::removeFromPlaylist);

                   // Помечаем текущий трек
                   if (i == currentPlaylistIndex)
                   {
                       controller.setAsCurrent(true);
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

    ///Воспроизвести трек из плейлиста
    private void playFromPlaylist(int index)
    {
        if (index >= 0 && index < playlistItems.size())
        {
            MusicFile item = playlistItems.get(index);
            currentPlaylistIndex = index;
            loadAndPlayFile(item.getLocalFile());
            updatePlaylistUI();
        }
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
    }
    ///
    /// ////////////////////////////////////////////////


    ///  TODO переделать так чтобы создавался MusicFile и он добалялся в локальный playlist
    public void handleSelectFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите аудиофайл");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Аудио файлы", "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg", "*.m4a"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Все файлы", "*.*"));

        Stage stage = (Stage) selectFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null)
        {
            loadAndPlayFile(selectedFile);
        }
    }

    private void loadAndPlayFile(File selectedFile)
    {
        new Thread(() -> {
            boolean success = playerService.loadFile(selectedFile);

            Platform.runLater(() -> {
                //showLoading(false);//можно отобразить загрузку но кажется это очень быстро, так что не требуется

                if (success)
                {
                    //updateTrackInfo(selectedFile);
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



    /// TODO начать с подключения к событиям прогресса и кнопок UI
    public void handlePlayPause(ActionEvent actionEvent)
    {
        if(playerService.isFileLoaded())
        {
            playerService.togglePlayPause();
            return;
        }
        else if(!playlistItems.isEmpty())
        {
            playerService.loadFile(playlistItems.getFirst().getLocalFile());
            playerService.togglePlayPause();
            return;
        }

        if(playlistItems.isEmpty() || !playerService.isFileLoaded())
        {
            playerStatusLabel.setText("Добавьте файлы в плейлист. Или выберите файл с диска");
        }
    }


    public void handlePrev(ActionEvent actionEvent)
    {
    }

    public void handleNext(ActionEvent actionEvent)
    {
    }

    public void handlePlaylist(ActionEvent actionEvent)
    {
    }

    public void handleClearPlaylist(ActionEvent actionEvent)
    {
        clearPlaylist();
    }

    public void handleAddFilesToPlaylist(ActionEvent actionEvent)
    {
    }
}
