package org.example.vasilev.musicpro.services.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

/**
 * Базовая реализация плеера на JavaFX MediaPlayer
 */
public class BasicPlayerService implements IPlayerService

{
    private MediaPlayer mediaPlayer;
    private Media currentMedia;
    private File currentFile;

    private PlayerState state = PlayerState.STOPPED;
    private double volume = 0.5;

    /// Списки подписчиков на события сервиса (например чтобы ползунки UI ползли по ходу воспроизведения)
    //private final List<Runnable> stateChangeListeners = new ArrayList<>();
    //private final List<Runnable> timeChangeListeners = new ArrayList<>();
    //private final List<Runnable> volumeChangeListeners = new ArrayList<>();
    /// Пока примитивно без них

    @Override
    public boolean loadFile(File audioFile)
    {
        stop(); // Остановить текущее воспроизведение

        try
        {
            currentFile = audioFile;
            String fileUri = audioFile.toURI().toString();
            currentMedia = new Media(fileUri);
            mediaPlayer = new MediaPlayer(currentMedia);

            // Настройка громкости
            mediaPlayer.setVolume(volume);

            // Обработчики событий
            mediaPlayer.setOnError(() -> {
                System.err.println("Ошибка воспроизведения: " + mediaPlayer.getError());
                setState(PlayerState.STOPPED);
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                setState(PlayerState.STOPPED);
                mediaPlayer.stop();
            });

            // Слушатель изменения времени ///TODO
            //mediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
            //    notifyTimeChangeListeners();
            //});
        }
        catch (Exception e)
        {
            System.err.println("Ошибка загрузки файла: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void stop()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            setState(PlayerState.STOPPED);
        }
    }

    private void setState(PlayerState state)
    {
        this.state = state;
    }


    @Override
    public void play()
    {
        if (mediaPlayer == null)
        {
            System.err.println("Файл не загружен");
            return;
        }

        if (state == PlayerState.PAUSED || state == PlayerState.STOPPED)
        {
            mediaPlayer.play();
            setState(PlayerState.PLAYING);
        }
    }

    @Override
    public void pause()
    {
        if (mediaPlayer != null && state == PlayerState.PLAYING)
        {
            mediaPlayer.pause();
            setState(PlayerState.PAUSED);
        }
    }


    @Override
    public void togglePlayPause()
    {
        if (state == PlayerState.PLAYING)
          pause();
        else
          play();
    }

    @Override
    public void setVolume(double volume)
    {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        if (mediaPlayer != null)
        {
            mediaPlayer.setVolume(this.volume);
        }
        //notifyVolumeChangeListeners(); TODO
    }

    @Override
    public double getVolume()
    {
        return volume;
    }

    @Override
    public void seek(double seconds)
    {
        seek(Duration.seconds(seconds));
    }

    @Override
    public void seek(Duration duration)
    {
        if (mediaPlayer != null)
            mediaPlayer.seek(duration);
    }

    @Override
    public double getCurrentTime()
    {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentTime().toSeconds();

        return 0.0;
    }

    @Override
    public Duration getCurrentDuration()
    {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentTime();

        return Duration.ZERO;
    }

    @Override
    public double getDuration()
    {
        if (mediaPlayer != null && currentMedia != null)
            return currentMedia.getDuration().toSeconds();

        return 0.0;
    }

    @Override
    public Duration getTotalDuration()
    {
        if (currentMedia != null)
            return currentMedia.getDuration();

        return Duration.ZERO;
    }

    @Override
    public PlayerState getState()
    {
        return state;
    }

    @Override
    public File getCurrentFile()
    {
        return currentFile;
    }

    @Override
    public boolean isFileLoaded()
    {
        return mediaPlayer != null;
    }

    @Override
    public boolean isPlaying()
    {
        return state == PlayerState.PLAYING;
    }

    @Override
    public boolean isPaused()
    {
        return state == PlayerState.PAUSED;
    }

    @Override
    public boolean isStopped()
    {
        return state == PlayerState.STOPPED;
    }

    /// Добавление подписчиков на события
    @Override
    public void addStateChangeListener(Runnable listener)
    {
        //TODO
    }

    @Override
    public void removeStateChangeListener(Runnable listener)
    {
       //TODO
    }

    @Override
    public void addTimeChangeListener(Runnable listener)
    {
        //TODO
    }

    @Override
    public void removeTimeChangeListener(Runnable listener)
    {
        //TODO
    }

    @Override
    public void addVolumeChangeListener(Runnable listener)
    {
        //TODO
    }

    @Override
    public void removeVolumeChangeListener(Runnable listener)
    {
        //TODO
    }

    /// По сути RAII деструктор
    @Override
    public void dispose()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        currentMedia = null;
        currentFile = null;
        setState(PlayerState.STOPPED);

        // Очищаем слушателей
        //stateChangeListeners.clear();
        //timeChangeListeners.clear();
        //volumeChangeListeners.clear();
    }
}
