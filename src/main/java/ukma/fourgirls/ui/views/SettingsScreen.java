package ukma.fourgirls.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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
        dialogBox.setMaxHeight(350);
        dialogBox.setPadding(new Insets(30, 40, 30, 40));
        dialogBox.getStyleClass().add("settings-dialog");

        // Заголовок вікна
        Label titleLabel = new Label(LanguageManager.getString("settings.title"));
        //titleLabel.setFont(font);
        titleLabel.getStyleClass().add("settings-title");
        dialogBox.getChildren().add(titleLabel);

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(40);
        settingsGrid.setVgap(15);
        settingsGrid.setAlignment(Pos.CENTER);

        // --- Рядок 1: Музика ---
        Label musicLabel = new Label(LanguageManager.getString("settings.music"));
        //musicLabel.setFont(font);
        musicLabel.getStyleClass().add("settings-label");

        boolean mutedAtStart = AudioManager.getInstance().isMusicMuted();
        ToggleButton musicToggle = new ToggleButton(LanguageManager.getString(mutedAtStart ? "settings.off" : "settings.on"));
        //musicToggle.setFont(font);
        musicToggle.getStyleClass().add("settings-toggle");
        musicToggle.setSelected(mutedAtStart);

        settingsGrid.add(musicLabel, 0, 0);
        settingsGrid.add(musicToggle, 1, 0);

        double currentVolume = AudioManager.getInstance().getVolume();
        Slider musicSlider = new Slider(0.0, 1.0, currentVolume);
        musicSlider.getStyleClass().add("settings-slider");
        musicSlider.setDisable(mutedAtStart);

        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.getInstance().setVolume(newValue.doubleValue());
        });

        musicToggle.setOnAction(e -> {
            boolean isMuted = AudioManager.getInstance().toggleMusic();
            musicToggle.setText(LanguageManager.getString(isMuted ? "settings.off" : "settings.on"));
            musicSlider.setDisable(isMuted);
            AudioManager.getInstance().setVolume(musicSlider.getValue());
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });

        settingsGrid.add(musicSlider, 0, 1, 2, 1);
        GridPane.setMargin(musicSlider, new Insets(0, 0, 10, 0));

        // --- Рядок 2: Звуки дій ---
        Label soundLabel = new Label(LanguageManager.getString("settings.sounds"));
        //soundLabel.setFont(font);
        soundLabel.getStyleClass().add("settings-label");

        ToggleButton soundToggle = new ToggleButton(LanguageManager.getString("settings.on"));
        //soundToggle.setFont(font);
        soundToggle.getStyleClass().add("settings-toggle");

        soundToggle.setOnAction(e -> {
            if (soundToggle.isSelected()) {
                soundToggle.setText(LanguageManager.getString("settings.off"));
                // TODO: Код вимкнення звуків ефектів
            } else {
                soundToggle.setText(LanguageManager.getString("settings.on"));
                // TODO: Код ввімкнення звуків
            }
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });
        settingsGrid.add(soundLabel, 0, 2);
        settingsGrid.add(soundToggle, 1, 2);

        // --- Рядок 3: Мова ---
        Label langLabel = new Label(LanguageManager.getString("settings.language"));
        //langLabel.setFont(font);
        langLabel.getStyleClass().add("settings-label");

        String currentLangCode = LanguageManager.getString("settings.title").equals("SETTINGS") ? "EN" : "UA";
        Button langButton = new Button(currentLangCode);
        //langButton.setFont(font);
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
        settingsGrid.add(langLabel, 0, 3);
        settingsGrid.add(langButton, 1, 3);

        dialogBox.getChildren().add(settingsGrid);

        Button closeButton = new Button(LanguageManager.getString("settings.close"));
        //closeButton.setFont(font);
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
            langLabel.setText(LanguageManager.getString("settings.language"));
            closeButton.setText(LanguageManager.getString("settings.close"));

            musicToggle.setText(LanguageManager.getString(musicToggle.isSelected() ? "settings.off" : "settings.on"));
            soundToggle.setText(LanguageManager.getString(soundToggle.isSelected() ? "settings.off" : "settings.on"));
        });
    }

    public Parent getRoot() {
        return overlayRoot;
    }
}