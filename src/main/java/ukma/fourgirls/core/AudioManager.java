package ukma.fourgirls.core;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class AudioManager {

    private static AudioManager instance;

    private MediaPlayer backgroundMusicPlayer;
    private boolean isMusicMuted = false;
    private boolean isSFXMuted = false;

    private double musicVolume = 0.4;
    private double sfxVolume = 0.4;

    private boolean isVFXMuted = false;
    private double vfxVolume = 0.4;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = InstanceHolder.INSTANCE;
        }
        return instance;
    }

    private static class InstanceHolder {
        private static final AudioManager INSTANCE = new AudioManager();
    }

    /**
     * Запускає фонову музику по колу
     * @param resourcePath Шлях до файлу в ресурсах
     */
    public void playBackgroundMusic(String resourcePath) {
        try {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
            }

            var resource = Objects.requireNonNull(getClass().getResource(resourcePath));
            Media media = new Media(resource.toExternalForm());
            backgroundMusicPlayer = new MediaPlayer(media);

            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusicPlayer.setVolume(musicVolume);

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
     * Перемикач стану ефектів
     */
    public boolean toggleSFX() {
        isSFXMuted = !isSFXMuted;
        return isSFXMuted;
    }

    public boolean isSFXMuted() {
        return isSFXMuted;
    }

    /**
     * Динамічно змінює гучність музики
     */
    public void setVolume(double volume) {
        this.musicVolume = volume;
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(volume);
        }
    }

    /**
     * Повертає поточну гучність музики для повзунка
     */
    public double getVolume() {
        return musicVolume;
    }

    /**
     * Метод для зміни гучності ефектів (якщо знадобиться окремий повзунок)
     */
    public void setSFXVolume(double volume) {
        this.sfxVolume = volume;
    }

    public double getSFXVolume() {
        return sfxVolume;
    }

    public boolean toggleVFX() {
        isVFXMuted = !isVFXMuted;
        return isVFXMuted;
    }

    public boolean isVFXMuted() {
        return isVFXMuted;
    }

    public void setVFXVolume(double volume) {
        this.vfxVolume = volume;
    }

    public double getVFXVolume() {
        return vfxVolume;
    }
    /**
     * Відтворює звук кнопки/дії
     */
    public void buttonSound(String resourcePath) {
        if (isSFXMuted) {
            return;
        }

        try {
            var resource = Objects.requireNonNull(getClass().getResource(resourcePath));
            AudioClip audioClip = new AudioClip(resource.toExternalForm());

            audioClip.setVolume(sfxVolume); // Використовуємо гучність для SFX
            audioClip.play();
        } catch (Exception e) {
            System.err.println("Не вдалося запустити аудіо ефект: " + e.getMessage());
        }
    }

    public void vfxSound(String resourcePath) {
        if (isVFXMuted) {
            return;
        }

        try {
            var resource = Objects.requireNonNull(getClass().getResource(resourcePath));
            AudioClip audioClip = new AudioClip(resource.toExternalForm());

            audioClip.setVolume(vfxVolume);
            audioClip.play();
        } catch (Exception e) {
            System.err.println("Не вдалося запустити аудіо VFX: " + e.getMessage());
        }
    }
}