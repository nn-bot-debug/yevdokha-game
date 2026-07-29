package ukma.fourgirls.application.service;

public interface Settings {
    // music
    boolean isMusicMuted();
    boolean toggleMusic();
    double getMusicVolume();
    void setMusicVolume(double volume);

    // SFX
    boolean isSfxMuted();
    boolean toggleSfx();
    double getSfxVolume();
    void setSfxVolume(double volume);

    // VFX
    boolean isVfxMuted();
    boolean toggleVfx();
    double getVfxVolume();
    void setVfxVolume(double volume);

    // Language
    String getCurrentLanguageCode();
    void toggleLanguage();
}