package ukma.fourgirls.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.LanguageManager;

import java.util.Objects;

public class SettingsScreen {
    private final StackPane overlayRoot;
    private final StackPane parentContainer;

    public SettingsScreen(StackPane parentContainer) {
        this.parentContainer = parentContainer;

        this.overlayRoot = new StackPane();
        this.overlayRoot.getStyleClass().add("settings-overlay");
        this.overlayRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/settings.css")).toExternalForm());

        VBox dialogBox = new VBox(25);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setMaxWidth(450);
        dialogBox.setMaxHeight(520); // Трохи збільшили під 3 повзунки
        dialogBox.setPadding(new Insets(30, 40, 30, 40));
        dialogBox.getStyleClass().add("settings-dialog");

        Label titleLabel = new Label(LanguageManager.getString("settings.title"));
        titleLabel.getStyleClass().add("settings-title");
        dialogBox.getChildren().add(titleLabel);

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(40);
        settingsGrid.setVgap(12); // Трохи зменшили вертикальний зазор, щоб усе компактно сіло
        settingsGrid.setAlignment(Pos.CENTER);

        // --- Рядок 1 & 2: Музика ---
        Label musicLabel = new Label(LanguageManager.getString("settings.music"));
        musicLabel.getStyleClass().add("settings-label");

        boolean musicMuted = AudioManager.getInstance().isMusicMuted();
        ToggleButton musicToggle = new ToggleButton(LanguageManager.getString(musicMuted ? "settings.off" : "settings.on"));
        musicToggle.getStyleClass().add("settings-toggle");
        musicToggle.setSelected(musicMuted);

        settingsGrid.add(musicLabel, 0, 0);
        settingsGrid.add(musicToggle, 1, 0);

        double currentMusicVolume = AudioManager.getInstance().getVolume();
        Slider musicSlider = new Slider(0.0, 1.0, currentMusicVolume);
        musicSlider.getStyleClass().add("settings-slider");
        musicSlider.setDisable(musicMuted);

        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.getInstance().setVolume(newValue.doubleValue());
        });

        musicToggle.setOnAction(e -> {
            boolean isMuted = AudioManager.getInstance().toggleMusic();
            musicToggle.setText(LanguageManager.getString(isMuted ? "settings.off" : "settings.on"));
            musicSlider.setDisable(isMuted);
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });

        settingsGrid.add(musicSlider, 0, 1, 2, 1);

        // --- Рядок 3 & 4: Звуки дій (SFX) ---
        Label soundLabel = new Label(LanguageManager.getString("settings.sounds"));
        soundLabel.getStyleClass().add("settings-label");

        boolean sfxMuted = AudioManager.getInstance().isSFXMuted();
        ToggleButton soundToggle = new ToggleButton(LanguageManager.getString(sfxMuted ? "settings.off" : "settings.on"));
        soundToggle.getStyleClass().add("settings-toggle");
        soundToggle.setSelected(sfxMuted);

        settingsGrid.add(soundLabel, 0, 2);
        settingsGrid.add(soundToggle, 1, 2);

        double currentSfxVolume = AudioManager.getInstance().getSFXVolume();
        Slider soundSlider = new Slider(0.0, 1.0, currentSfxVolume);
        soundSlider.getStyleClass().add("settings-slider");
        soundSlider.setDisable(sfxMuted);

        soundSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.getInstance().setSFXVolume(newValue.doubleValue());
        });

        soundToggle.setOnAction(e -> {
            boolean isMuted = AudioManager.getInstance().toggleSFX();
            soundToggle.setText(LanguageManager.getString(isMuted ? "settings.off" : "settings.on"));
            soundSlider.setDisable(isMuted);
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });

        settingsGrid.add(soundSlider, 0, 3, 2, 1);

        // --- Рядок 5 & 6: Візуальні Ефекти (VFX Звуки) ---
        // Якщо у LanguageManager ще немає ключа "settings.vfx", він поверне цей рядок або додай його у файли локалізації (messages_uk.properties тощо)
        Label vfxLabel = new Label(LanguageManager.getString("settings.vfx"));
        vfxLabel.getStyleClass().add("settings-label");

        boolean vfxMuted = AudioManager.getInstance().isVFXMuted();
        ToggleButton vfxToggle = new ToggleButton(LanguageManager.getString(vfxMuted ? "settings.off" : "settings.on"));
        vfxToggle.getStyleClass().add("settings-toggle");
        vfxToggle.setSelected(vfxMuted);

        settingsGrid.add(vfxLabel, 0, 4);
        settingsGrid.add(vfxToggle, 1, 4);

        double currentVfxVolume = AudioManager.getInstance().getVFXVolume();
        Slider vfxSlider = new Slider(0.0, 1.0, currentVfxVolume);
        vfxSlider.getStyleClass().add("settings-slider");
        vfxSlider.setDisable(vfxMuted);

        vfxSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.getInstance().setVFXVolume(newValue.doubleValue());
        });

        vfxToggle.setOnAction(e -> {
            boolean isMuted = AudioManager.getInstance().toggleVFX();
            vfxToggle.setText(LanguageManager.getString(isMuted ? "settings.off" : "settings.on"));
            vfxSlider.setDisable(isMuted);
            // Програємо тестовий звук VFX (наприклад, якийсь спалах чи іскру), якщо увімкнули назад
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });

        settingsGrid.add(vfxSlider, 0, 5, 2, 1);
        GridPane.setMargin(vfxSlider, new Insets(0, 0, 10, 0));

        // --- Рядок 7: Мова ---
        Label langLabel = new Label(LanguageManager.getString("settings.language"));
        langLabel.getStyleClass().add("settings-label");

        String currentLangCode = LanguageManager.getString("settings.title").equals("SETTINGS") ? "EN" : "UA";
        Button langButton = new Button(currentLangCode);
        langButton.getStyleClass().add("settings-button");

        langButton.setOnAction(e -> {
            if (langButton.getText().equals("UA")) {
                langButton.setText("EN");
                LanguageManager.setLanguage(java.util.Locale.of("en"));
            } else {
                langButton.setText("UA");
                LanguageManager.setLanguage(java.util.Locale.of("uk"));
            }
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });
        settingsGrid.add(langLabel, 0, 6);
        settingsGrid.add(langButton, 1, 6);

        dialogBox.getChildren().add(settingsGrid);

        // --- Кнопка закриття ---
        Button closeButton = new Button(LanguageManager.getString("settings.close"));
        closeButton.getStyleClass().add("settings-button");
        closeButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            parentContainer.getChildren().remove(overlayRoot);
        });

        dialogBox.getChildren().add(closeButton);
        overlayRoot.getChildren().add(dialogBox);

        LanguageManager.addLanguageChangeListener(() -> {
            titleLabel.setText(LanguageManager.getString("settings.title"));
            musicLabel.setText(LanguageManager.getString("settings.music"));
            soundLabel.setText(LanguageManager.getString("settings.sounds"));
            vfxLabel.setText(LanguageManager.getString("settings.vfx"));
            langLabel.setText(LanguageManager.getString("settings.language"));
            closeButton.setText(LanguageManager.getString("settings.close"));

            musicToggle.setText(LanguageManager.getString(musicToggle.isSelected() ? "settings.off" : "settings.on"));
            soundToggle.setText(LanguageManager.getString(soundToggle.isSelected() ? "settings.off" : "settings.on"));
            vfxToggle.setText(LanguageManager.getString(vfxToggle.isSelected() ? "settings.off" : "settings.on"));
        });
    }

    public Parent getRoot() {
        return overlayRoot;
    }
}