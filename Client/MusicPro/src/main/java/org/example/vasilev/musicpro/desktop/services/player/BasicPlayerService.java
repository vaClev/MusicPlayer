package org.example.vasilev.musicpro.desktop.services.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.vasilev.musicpro.common.services.player.IPlayerService;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

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
    private final List<Consumer<PlayerState>> stateListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<Duration>> timeListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<Double>> volumeListeners = new CopyOnWriteArrayList<>();

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
                setState(PlayerState.END_OF_TRACK);
                mediaPlayer.stop();
            });

            // Слушатель изменения времени
            mediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
                if(state == PlayerState.PLAYING)
                {
                    timeListeners.forEach(listener -> listener.accept(newVal));
                }
            });

            setState(PlayerState.STOPPED);
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
        notifyStateListeners();
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
            mediaPlayer.setVolume(this.volume);

        notifyVolumeChangeListeners();
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
    public void addStateChangeListener(Consumer<PlayerState> listener)
    {
        stateListeners.add(listener);
    }

    @Override
    public void removeStateChangeListener(Consumer<PlayerState> listener)
    {
       stateListeners.remove(listener);
    }

    private void notifyStateListeners()
    {
        stateListeners.forEach(l -> l.accept(state));
    }

    @Override
    public void addTimeChangeListener(Consumer<Duration> listener)
    {
        timeListeners.add(listener);
    }

    @Override
    public void removeTimeChangeListener(Consumer<Duration> listener)
    {
        timeListeners.remove(listener);
    }

    @Override
    public void addVolumeChangeListener(Consumer<Double> listener)
    {
        volumeListeners.add(listener);
    }

    @Override
    public void removeVolumeChangeListener(Consumer<Double> listener)
    {
        volumeListeners.remove(listener);
    }

    private void notifyVolumeChangeListeners()
    {
        volumeListeners.forEach(l -> l.accept(volume));
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
        stateListeners.clear();
        timeListeners.clear();
        volumeListeners.clear();
    }
}
