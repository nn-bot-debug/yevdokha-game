package ukma.fourgirls.application.service;

public interface AudioEngine {
    void playBackgroundMusic(String resourcePath);
    void fadeOutBackgroundMusic(double durationSeconds);

    void playEyeLoopSound(String resourcePath);
    void stopEyeLoopSound(double fadeDurationSeconds);

    void buttonSound(String resourcePath);
    void vfxSound(String resourcePath);

    boolean toggleMusic();
    boolean toggleSFX();
    boolean toggleVFX();

    boolean isMusicMuted();
    double getVolume();
    void setVolume(double volume);

    boolean isSFXMuted();
    double getSFXVolume();
    void setSFXVolume(double volume);

    boolean isVFXMuted();
    double getVFXVolume();
    void setVFXVolume(double volume);
}
