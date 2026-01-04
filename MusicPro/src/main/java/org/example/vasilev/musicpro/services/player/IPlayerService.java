package org.example.vasilev.musicpro.services.player;
import javafx.util.Duration;
import java.io.File;

/**
 * Интерфейс сервиса воспроизведения аудио
 * Базовый контракт для всех реализаций плеера
 */
public interface IPlayerService
{

    /**
     * Состояния плеера
     */
    enum PlayerState
    {
        PLAYING,
        PAUSED,
        STOPPED
    }

    /**
     * Загрузить аудиофайл для воспроизведения
     * @param audioFile файл для загрузки
     * @return true если файл успешно загружен
     */
    boolean loadFile(File audioFile);

    /**
     * Начать воспроизведение загруженного файла
     */
    void play();

    /**
     * Приостановить воспроизведение
     */
    void pause();

    /**
     * Остановить воспроизведение
     */
    void stop();

    /**
     * Переключить между воспроизведением и паузой
     */
    void togglePlayPause();

    /**
     * Установить громкость
     * @param volume громкость от 0.0 до 1.0
     */
    void setVolume(double volume);

    /**
     * Получить текущую громкость
     * @return текущая громкость (0.0 - 1.0)
     */
    double getVolume();

    /**
     * Перемотать на указанную позицию
     * @param seconds позиция в секундах
     */
    void seek(double seconds);

    /**
     * Перемотать на указанную длительность
     * @param duration позиция как Duration
     */
    void seek(Duration duration);

    /**
     * Получить текущую позицию воспроизведения
     * @return позиция в секундах
     */
    double getCurrentTime();

    /**
     * Получить текущую позицию как Duration
     * @return текущая позиция
     */
    Duration getCurrentDuration();

    /**
     * Получить длительность загруженного трека
     * @return длительность в секундах
     */
    double getDuration();

    /**
     * Получить длительность как Duration
     * @return длительность трека
     */
    Duration getTotalDuration();

    /**
     * Получить текущее состояние плеера
     * @return состояние плеера
     */
    PlayerState getState();

    /**
     * Получить текущий загруженный файл
     * @return текущий файл или null
     */
    File getCurrentFile();

    /**
     * Проверить, загружен ли файл
     * @return true если файл загружен
     */
    boolean isFileLoaded();

    /**
     * Проверить, играет ли музыка
     * @return true если воспроизведение активно
     */
    boolean isPlaying();

    /**
     * Проверить, находится ли плеер на паузе
     * @return true если на паузе
     */
    boolean isPaused();

    /**
     * Проверить, остановлен ли плеер
     * @return true если остановлен
     */
    boolean isStopped();

    /**
     * Добавить слушателя изменения состояния
     * @param listener слушатель
     */
    void addStateChangeListener(Runnable listener);

    /**
     * Удалить слушателя изменения состояния
     * @param listener слушатель
     */
    void removeStateChangeListener(Runnable listener);

    /**
     * Добавить слушателя изменения времени
     * @param listener слушатель
     */
    void addTimeChangeListener(Runnable listener);

    /**
     * Удалить слушателя изменения времени
     * @param listener слушатель
     */
    void removeTimeChangeListener(Runnable listener);

    /**
     * Добавить слушателя изменения громкости
     * @param listener слушатель
     */
    void addVolumeChangeListener(Runnable listener);

    /**
     * Удалить слушателя изменения громкости
     * @param listener слушатель
     */
    void removeVolumeChangeListener(Runnable listener);

    /**
     * Освободить ресурсы плеера
     */
    void dispose();


    /// TODO обдумать возможно потребуется
    /**
     * Получить метаданные трека (если доступны)
     * @return метаданные или null
     */
    /*default AudioMetadata getMetadata()
    {
        return null;
    }*/

    /**
     * Класс для хранения метаданных аудио
     */
    /*class AudioMetadata
    {
        private String title;
        private String artist;
        private String album;
        private String genre;
        private Integer year;
        private Integer trackNumber;

        // Геттеры и сеттеры
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getArtist() { return artist; }
        public void setArtist(String artist) { this.artist = artist; }

        public String getAlbum() { return album; }
        public void setAlbum(String album) { this.album = album; }

        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }

        public Integer getTrackNumber() { return trackNumber; }
        public void setTrackNumber(Integer trackNumber) { this.trackNumber = trackNumber; }
    }*/
}