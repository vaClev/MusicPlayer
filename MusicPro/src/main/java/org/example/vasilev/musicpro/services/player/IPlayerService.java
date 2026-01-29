package org.example.vasilev.musicpro.services.player;
import javafx.util.Duration;
import java.io.File;
import java.util.function.Consumer;

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
        STOPPED,
        END_OF_TRACK
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
    void addStateChangeListener(Consumer<PlayerState> listener);

    /**
     * Удалить слушателя изменения состояния
     * @param listener слушатель
     */
    void removeStateChangeListener(Consumer<PlayerState> listener);

    /**
     * Добавить слушателя изменения времени
     * @param listener слушатель
     */
    void addTimeChangeListener(Consumer<Duration> listener);

    /**
     * Удалить слушателя изменения времени
     * @param listener слушатель
     */
    void removeTimeChangeListener(Consumer<Duration> listener);

    /**
     * Добавить слушателя изменения громкости
     * @param listener слушатель
     */
    void addVolumeChangeListener(Consumer<Double> listener);

    /**
     * Удалить слушателя изменения громкости
     * @param listener слушатель
     */
    void removeVolumeChangeListener(Consumer<Double> listener);

    /**
     * Освободить ресурсы плеера
     */
    void dispose();
}