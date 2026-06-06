package ukma.fourgirls.core;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class AudioManager {

    private static AudioManager instance;

    private MediaPlayer backgroundMusicPlayer;
    private boolean isMusicMuted = false;
    private double currentVolume = 0.4;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new InstanceHolder().INSTANCE; // Безпечний Singleton
        }
        return instance;
    }

    private static class InstanceHolder {
        private static final AudioManager INSTANCE = new AudioManager();
    }


    /**
     * Запускає фонову музику по колу
     * @param resourcePath Шлях до файлу в ресурсах (наприклад, "/music/ambient.mp3")
     */
    public void playBackgroundMusic(String resourcePath) {
        try {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
            }

            // Завантажуємо файл
            var resource = Objects.requireNonNull(getClass().getResource(resourcePath));
            Media media = new Media(resource.toExternalForm());
            backgroundMusicPlayer = new MediaPlayer(media);

            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            backgroundMusicPlayer.setVolume(currentVolume);

            if (isMusicMuted) {
                backgroundMusicPlayer.setMute(true);
            }

            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Не вдалося запустити аудіо: " + e.getMessage());
        }
    }

    /**
     * Перемикач стану музики (Mute / Unmute)
     * @return поточний стан (true = звуку немає, false = звук є)
     */
    public boolean toggleMusic() {
        isMusicMuted = !isMusicMuted;

        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setMute(isMusicMuted);
        }

        return isMusicMuted;
    }

    public boolean isMusicMuted() {
        return isMusicMuted;
    }

    /**
     * Динамічно змінює гучність плеєра прямо під час перетягування повзунка
     * @param volume значення від 0.0 до 1.0
     */
    public void setVolume(double volume) {
        this.currentVolume = volume;
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(volume);
        }
    }

    /**
     * Повертає поточну гучність, щоб повзунок у налаштуваннях знав, де саме стати при відкритті
     */
    public double getVolume() {
        return currentVolume;
    }

    public void buttonSound(String resourcePath) {
        try {
            var resource = Objects.requireNonNull(getClass().getResource(resourcePath));
            AudioClip audioClip = new AudioClip(resource.toExternalForm());

            audioClip.setVolume(currentVolume);

            audioClip.play();
        }
        catch (Exception e) {
            System.err.println("Не вдалося запустити аудіо: " + e.getMessage());
        }
    }
}