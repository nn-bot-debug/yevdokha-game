package ukma.fourgirls.core;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class AudioManager {

    private MediaPlayer backgroundMusicPlayer;

    //прапорці стану
    private boolean isMusicMuted = false;
    private boolean isSFXMuted = false;
    private boolean isVFXMuted = false;

    //рівні гучності
    private double musicVolume = 0.4;
    private double sfxVolume = 0.4;
    private double vfxVolume = 0.4;

    private AudioManager() {}

    public static AudioManager getInstance() {
        return InstanceHolder.INSTANCE;
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
            backgroundMusicPlayer.setMute(isMusicMuted);

            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to start background music: " + e.getMessage());
        }
    }

    // --- Управління музикою (Music) ---

    public boolean toggleMusic() {
        isMusicMuted = !isMusicMuted;
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setMute(isMusicMuted);
        }
        return isMusicMuted;
    }

    public boolean isMusicMuted() { return isMusicMuted; }

    public void setVolume(double volume) {
        this.musicVolume = volume;
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(volume);
        }
    }

    public double getVolume() { return musicVolume; }

    // --- Управління ефектами інтерфейсу (SFX) ---

    public boolean toggleSFX() {
        isSFXMuted = !isSFXMuted;
        return isSFXMuted;
    }

    public boolean isSFXMuted() { return isSFXMuted; }

    public void setSFXVolume(double volume) { this.sfxVolume = volume; }

    public double getSFXVolume() { return sfxVolume; }

    // --- Управління візуальними ефектами (VFX) ---

    public boolean toggleVFX() {
        isVFXMuted = !isVFXMuted;
        return isVFXMuted;
    }

    public boolean isVFXMuted() { return isVFXMuted; }

    public void setVFXVolume(double volume) { this.vfxVolume = volume; }

    public double getVFXVolume() { return vfxVolume; }

    // --- Відтворення коротких звуків ---

    public void buttonSound(String resourcePath) {
        playSoundEffect(resourcePath, isSFXMuted, sfxVolume);
    }

    public void vfxSound(String resourcePath) {
        playSoundEffect(resourcePath, isVFXMuted, vfxVolume);
    }

    private void playSoundEffect(String resourcePath, boolean isMuted, double volume) {
        if (isMuted) return;

        try {
            var resource = Objects.requireNonNull(getClass().getResource(resourcePath));
            AudioClip audioClip = new AudioClip(resource.toExternalForm());
            audioClip.setVolume(volume);
            audioClip.play();
        } catch (Exception e) {
            System.err.println("Failed to play audio effect (" + resourcePath + "): " + e.getMessage());
        }
    }
}