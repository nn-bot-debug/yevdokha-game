package ukma.fourgirls.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ukma.fourgirls.infrastructure.localization.LanguageManager;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsImplTest {

    @Mock
    private AudioEngine audioEngine;

    private SettingsImpl settings;

    @BeforeEach
    void setUp() {
        settings = new SettingsImpl(audioEngine);
    }

    @Test
    void isMusicMuted_ReturnsAudioEngineState() {
        when(audioEngine.isMusicMuted()).thenReturn(true);
        assertTrue(settings.isMusicMuted());
        verify(audioEngine).isMusicMuted();
    }

    @Test
    void toggleMusic_TogglesAndReturnsState() {
        when(audioEngine.toggleMusic()).thenReturn(false);
        assertFalse(settings.toggleMusic());
        verify(audioEngine).toggleMusic();
    }

    @Test
    void getMusicVolume_ReturnsCorrectVolume() {
        when(audioEngine.getVolume()).thenReturn(0.75);
        assertEquals(0.75, settings.getMusicVolume());
        verify(audioEngine).getVolume();
    }

    @Test
    void setMusicVolume_DelegatesToAudioEngine() {
        settings.setMusicVolume(0.5);
        verify(audioEngine).setVolume(0.5);
    }

    @Test
    void isSfxMuted_ReturnsAudioEngineState() {
        when(audioEngine.isSFXMuted()).thenReturn(false);
        assertFalse(settings.isSfxMuted());
        verify(audioEngine).isSFXMuted();
    }

    @Test
    void toggleSfx_TogglesAndReturnsState() {
        when(audioEngine.toggleSFX()).thenReturn(true);
        assertTrue(settings.toggleSfx());
        verify(audioEngine).toggleSFX();
    }

    @Test
    void getSfxVolume_ReturnsCorrectVolume() {
        when(audioEngine.getSFXVolume()).thenReturn(0.8);
        assertEquals(0.8, settings.getSfxVolume());
        verify(audioEngine).getSFXVolume();
    }

    @Test
    void setSfxVolume_DelegatesToAudioEngine() {
        settings.setSfxVolume(0.3);
        verify(audioEngine).setSFXVolume(0.3);
    }


    @Test
    void isVfxMuted_ReturnsAudioEngineState() {
        when(audioEngine.isVFXMuted()).thenReturn(true);
        assertTrue(settings.isVfxMuted());
        verify(audioEngine).isVFXMuted();
    }

    @Test
    void toggleVfx_TogglesAndReturnsState() {
        when(audioEngine.toggleVFX()).thenReturn(false);
        assertFalse(settings.toggleVfx());
        verify(audioEngine).toggleVFX();
    }

    @Test
    void getVfxVolume_ReturnsCorrectVolume() {
        when(audioEngine.getVFXVolume()).thenReturn(0.9);
        assertEquals(0.9, settings.getVfxVolume());
        verify(audioEngine).getVFXVolume();
    }

    @Test
    void setVfxVolume_DelegatesToAudioEngine() {
        settings.setVfxVolume(0.4);
        verify(audioEngine).setVFXVolume(0.4);
    }

    @Test
    void getCurrentLanguageCode_WhenTitleIsSettings_ReturnsEN() {
        try (MockedStatic<LanguageManager> mockedLanguageManager = mockStatic(LanguageManager.class)) {
            mockedLanguageManager.when(() -> LanguageManager.getString("settings.title"))
                    .thenReturn("SETTINGS");

            assertEquals("EN", settings.getCurrentLanguageCode());
        }
    }

    @Test
    void getCurrentLanguageCode_WhenTitleIsNotSettings_ReturnsUA() {
        try (MockedStatic<LanguageManager> mockedLanguageManager = mockStatic(LanguageManager.class)) {
            mockedLanguageManager.when(() -> LanguageManager.getString("settings.title"))
                    .thenReturn("НАЛАШТУВАННЯ");

            assertEquals("UA", settings.getCurrentLanguageCode());
        }
    }

    @Test
    void toggleLanguage_FromEnToUa() {
        try (MockedStatic<LanguageManager> mockedLanguageManager = mockStatic(LanguageManager.class)) {
            mockedLanguageManager.when(() -> LanguageManager.getString("settings.title"))
                    .thenReturn("SETTINGS");

            settings.toggleLanguage();

            mockedLanguageManager.verify(() -> LanguageManager.setLanguage(Locale.of("uk")));
        }
    }

    @Test
    void toggleLanguage_FromUaToEn() {
        try (MockedStatic<LanguageManager> mockedLanguageManager = mockStatic(LanguageManager.class)) {
            mockedLanguageManager.when(() -> LanguageManager.getString("settings.title"))
                    .thenReturn("НАЛАШТУВАННЯ");

            settings.toggleLanguage();

            mockedLanguageManager.verify(() -> LanguageManager.setLanguage(Locale.of("en")));
        }
    }
}