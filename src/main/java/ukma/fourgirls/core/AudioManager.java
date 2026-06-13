package ukma.fourgirls.core;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.Objects;

public class AudioManager {

    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer activeEyePlayer;

    //прапорці стану
    private boolean isMusicMuted = false;
    private boolean isSFXMuted = false;
    private boolean isVFXMuted = false;

    //рівні гучності
    private double musicVolume = 0.4;
    private double sfxVolume = 0.7;
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

    public MediaPlayer playEyeLoopSound(String resourcePath) {
        if (isSFXMuted) return null;
        try {
            if (activeEyePlayer != null) {
                activeEyePlayer.stop();
            }

            var resource = Objects.requireNonNull(getClass().getResource(resourcePath));
            Media media = new Media(resource.toExternalForm());
            activeEyePlayer = new MediaPlayer(media);

            activeEyePlayer.setCycleCount(MediaPlayer.INDEFINITE);
            activeEyePlayer.setVolume(sfxVolume);
            activeEyePlayer.play();

            return activeEyePlayer;
        } catch (Exception e) {
            System.err.println("Failed to play eye loop sound: " + e.getMessage());
            return null;
        }
    }

    public void fadeOutAndStop(MediaPlayer player, double durationSeconds) {
        if (player == null) return;

        double startVolume = player.getVolume();
        int steps = 25;
        double volumeStep = startVolume / steps;
        double timeStep = durationSeconds / steps;

        Timeline fadeTimeline = new Timeline();

        for (int i = 0; i <= steps; i++) {
            final double targetVolume = Math.max(0, startVolume - (i * volumeStep));
            KeyFrame frame = new KeyFrame(
                    Duration.seconds(i * timeStep),
                    e -> player.setVolume(targetVolume)
            );
            fadeTimeline.getKeyFrames().add(frame);
        }

        fadeTimeline.setOnFinished(e -> {
            player.stop();
            player.dispose();
            if (player == activeEyePlayer) {
                activeEyePlayer = null;
            }
        });

        fadeTimeline.play();
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

    public void setSFXVolume(double volume) {
        this.sfxVolume = volume;
        if (activeEyePlayer != null) {
            activeEyePlayer.setVolume(volume);
        }
    }

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