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

import java.util.Objects;

public class SettingsScreen {
    private final StackPane overlayRoot;
    private final StackPane parentContainer;
    private Font font;

    public SettingsScreen(StackPane parentContainer) {
        this.parentContainer = parentContainer;

        this.overlayRoot = new StackPane();
        this.overlayRoot.getStyleClass().add("settings-overlay");

        this.overlayRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/settings.css")).toExternalForm());

        try {
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 24);
        } catch (Exception e) {
            font = Font.font("Arial", 22);
        }

        VBox dialogBox = new VBox(25);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setMaxWidth(450);
        dialogBox.setMaxHeight(350);
        dialogBox.setPadding(new Insets(30, 40, 30, 40));
        dialogBox.getStyleClass().add("settings-dialog");

        // Заголовок вікна
        Label titleLabel = new Label("SETTINGS");
        titleLabel.setFont(font);
        titleLabel.getStyleClass().add("settings-title");
        dialogBox.getChildren().add(titleLabel);

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(40);
        settingsGrid.setVgap(15);
        settingsGrid.setAlignment(Pos.CENTER);

        // --- Рядок 1: Музика ---
        Label musicLabel = new Label("MUSIC");
        musicLabel.setFont(font);
        musicLabel.getStyleClass().add("settings-label");

        boolean mutedAtStart = AudioManager.getInstance().isMusicMuted();
        ToggleButton musicToggle = new ToggleButton(mutedAtStart ? "OFF" : "ON");
        musicToggle.setFont(font);
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
            musicToggle.setText(isMuted ? "OFF" : "ON");
            musicSlider.setDisable(isMuted);
            AudioManager.getInstance().setVolume(musicSlider.getValue());
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });

        settingsGrid.add(musicSlider, 0, 1, 2, 1);
        GridPane.setMargin(musicSlider, new Insets(0, 0, 10, 0));

        // --- Рядок 2: Звуки дій ---
        Label soundLabel = new Label("SOUNDS");
        soundLabel.setFont(font);
        soundLabel.getStyleClass().add("settings-label");

        ToggleButton soundToggle = new ToggleButton("ON");
        soundToggle.setFont(font);
        soundToggle.getStyleClass().add("settings-toggle");

        soundToggle.setOnAction(e -> {
            if (soundToggle.isSelected()) {
                soundToggle.setText("OFF");
                // TODO: Код вимкнення звуків ефектів
            } else {
                soundToggle.setText("ON");
                // TODO: Код ввімкнення звуків
            }
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });
        settingsGrid.add(soundLabel, 0, 2);
        settingsGrid.add(soundToggle, 1, 2);

        // --- Рядок 3: Мова ---
        Label langLabel = new Label("LANGUAGE");
        langLabel.setFont(font);
        langLabel.getStyleClass().add("settings-label");

        Button langButton = new Button("UA");
        langButton.setFont(font);
        langButton.getStyleClass().add("settings-button");

        langButton.setOnAction(e -> {
            if (langButton.getText().equals("UA")) {
                langButton.setText("EN");
            } else {
                langButton.setText("UA");
            }
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
        });
        settingsGrid.add(langLabel, 0, 3);
        settingsGrid.add(langButton, 1, 3);

        dialogBox.getChildren().add(settingsGrid);

        Button closeButton = new Button("CLOSE");
        closeButton.setFont(font);
        closeButton.getStyleClass().add("settings-button");
        closeButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            parentContainer.getChildren().remove(overlayRoot);
        });

        dialogBox.getChildren().add(closeButton);
        overlayRoot.getChildren().add(dialogBox);
    }

    public Parent getRoot() {
        return overlayRoot;
    }
}