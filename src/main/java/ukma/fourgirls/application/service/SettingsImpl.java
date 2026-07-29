package ukma.fourgirls.application.service;

import ukma.fourgirls.infrastructure.localization.LanguageManager;

public class SettingsImpl implements Settings {
    private final AudioEngine audioEngine;

    public SettingsImpl(AudioEngine audioEngine) {
        this.audioEngine = audioEngine;
    }

    @Override
    public boolean isMusicMuted() {
        return audioEngine.isMusicMuted();
    }

    @Override
    public boolean toggleMusic() {
        return audioEngine.toggleMusic();
    }

    @Override
    public double getMusicVolume() {
        return audioEngine.getVolume();
    }

    @Override
    public void setMusicVolume(double volume) {
        audioEngine.setVolume(volume);
    }

    @Override
    public boolean isSfxMuted() {
        return audioEngine.isSFXMuted();
    }

    @Override
    public boolean toggleSfx() {
        return audioEngine.toggleSFX();
    }

    @Override
    public double getSfxVolume() {
        return audioEngine.getSFXVolume();
    }

    @Override
    public void setSfxVolume(double volume) {
        audioEngine.setSFXVolume(volume);
    }

    @Override
    public boolean isVfxMuted() {
        return audioEngine.isVFXMuted();
    }

    @Override
    public boolean toggleVfx() {
        return audioEngine.toggleVFX();
    }

    @Override
    public double getVfxVolume() {
        return audioEngine.getVFXVolume();
    }

    @Override
    public void setVfxVolume(double volume) {
        audioEngine.setVFXVolume(volume);
    }

    @Override
    public String getCurrentLanguageCode() {
        return LanguageManager.getString("settings.title").equals("SETTINGS") ? "EN" : "UA";
    }

    @Override
    public void toggleLanguage() {
        if (getCurrentLanguageCode().equals("UA")) {
            LanguageManager.setLanguage(java.util.Locale.of("en"));
        } else {
            LanguageManager.setLanguage(java.util.Locale.of("uk"));
        }
    }
}
