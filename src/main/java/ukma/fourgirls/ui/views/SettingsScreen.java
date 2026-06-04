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

public class SettingsScreen {
    private final StackPane overlayRoot;
    private final StackPane parentContainer;
    private Font font;

    /**
     * Конструктор налаштувань
     * @param parentContainer Головний StackPane меню, куди ми "накладемо" це вікно зверху
     */
    public SettingsScreen(StackPane parentContainer) {
        this.parentContainer = parentContainer;

        // Затемнення всього екрану
        this.overlayRoot = new StackPane();
        this.overlayRoot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.65);"); // Напівпрозорий чорний фон

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

        dialogBox.setStyle(
                "-fx-background-color: rgba(25, 35, 35, 0.95);" +
                        "-fx-border-color: #3c5050;" +
                        "-fx-border-width: 3px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 30, 0.0, 0, 0);"
        );

        // Заголовок вікна
        Label titleLabel = new Label("SETTINGS");
        titleLabel.setFont(font);
        titleLabel.setStyle("-fx-text-fill: #9ba89e; -fx-underline: true;");
        dialogBox.getChildren().add(titleLabel);

        // 3. Сітка з налаштуваннями (Опція -> Кнопка)
        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(40);
        settingsGrid.setVgap(15);
        settingsGrid.setAlignment(Pos.CENTER);

        // --- Рядок 1: Музика ---
        Label musicLabel = new Label("MUSIC");
        styleLabel(musicLabel);

        boolean mutedAtStart = AudioManager.getInstance().isMusicMuted();
        ToggleButton musicToggle = new ToggleButton(mutedAtStart ? "OFF" : "ON");
        musicToggle.setSelected(mutedAtStart);
        styleToggleButton(musicToggle);

        settingsGrid.add(musicLabel, 0, 0);
        settingsGrid.add(musicToggle, 1, 0);

        // Параметри Slider(min, max, value)
        double currentVolume = AudioManager.getInstance().getVolume();
        Slider musicSlider = new Slider(0.0, 1.0, currentVolume);
        styleSlider(musicSlider);
        musicSlider.setDisable(mutedAtStart);

        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.getInstance().setVolume(newValue.doubleValue());
        });

        musicToggle.setOnAction(e -> {
            boolean isMuted = AudioManager.getInstance().toggleMusic();
            musicToggle.setText(isMuted ? "OFF" : "ON");
            musicSlider.setDisable(isMuted); // Блокуємо повзунок, якщо музику вимкнено повністю
        });

        settingsGrid.add(musicSlider, 0, 1, 2, 1);
        GridPane.setMargin(musicSlider, new Insets(0, 0, 10, 0));


        // --- Рядок 2: Звуки дій ---
        Label soundLabel = new Label("SOUNDS");
        styleLabel(soundLabel);
        ToggleButton soundToggle = new ToggleButton("ON");
        styleToggleButton(soundToggle);
        soundToggle.setOnAction(e -> {
            if (soundToggle.isSelected()) {
                soundToggle.setText("OFF");
                // Код вимкнення звуків ефектів
            } else {
                soundToggle.setText("ON");
                // Код ввімкнення звуків
            }
        });
        settingsGrid.add(soundLabel, 0, 2);
        settingsGrid.add(soundToggle, 1, 2);

        // --- Рядок 3: Мова ---
        Label langLabel = new Label("LANGUAGE");
        styleLabel(langLabel);
        Button langButton = new Button("UA");
        styleInterfaceButton(langButton);
        langButton.setOnAction(e -> {
            if (langButton.getText().equals("UA")) {
                langButton.setText("EN");
                // Логіка перемикання інтерфейсу на англійську
            } else {
                langButton.setText("UA");
                // Назад на українську
            }
        });
        settingsGrid.add(langLabel, 0, 3);
        settingsGrid.add(langButton, 1, 3);

        dialogBox.getChildren().add(settingsGrid);

        // 4. Кнопка закриття вікна (Back / Close)
        Button closeButton = new Button("CLOSE");
        styleInterfaceButton(closeButton);
        // При натисканні ми просто видаляємо це вікно з нашого головного меню!
        closeButton.setOnAction(e -> parentContainer.getChildren().remove(overlayRoot));

        dialogBox.getChildren().add(closeButton);

        // Додаємо коробку по центру напівпрозорого екрану
        overlayRoot.getChildren().add(dialogBox);
    }

    public Parent getRoot() {
        return overlayRoot;
    }

    // --- ДОПОМІЖНІ МЕТОДИ СТИЛІЗАЦІЇ ---
    private void styleLabel(Label label) {
        label.setFont(font);
        label.setStyle("-fx-text-fill: #9ba89e;");
    }

    private void styleInterfaceButton(Button button) {
        button.setPrefWidth(100);
        button.setPrefHeight(34);
        button.setFont(font);
        button.setStyle(
                "-fx-background-color: rgba(35, 50, 50, 0.85);" +
                        "-fx-text-fill: #9ba89e;" +
                        "-fx-border-color: #3c5050;" +
                        "-fx-background-radius: 2px;" +
                        "-fx-border-radius: 2px;"
        );
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: rgba(50, 70, 70, 0.9); -fx-text-fill: #9ba89e; -fx-border-color: #3c5050;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: rgba(35, 50, 50, 0.85); -fx-text-fill: #9ba89e; -fx-border-color: #3c5050;"));
    }

    private void styleToggleButton(ToggleButton toggle) {
        toggle.setPrefWidth(100);
        toggle.setPrefHeight(34);
        toggle.setFont(font);

        String baseActive = "-fx-background-color: rgba(45, 65, 65, 0.9); -fx-text-fill: #9ba89e; -fx-border-color: #4a6363; -fx-border-width: 2px;";
        String baseMuted = "-fx-background-color: rgba(20, 25, 25, 0.6); -fx-text-fill: #5a665e; -fx-border-color: #253333; -fx-border-width: 1px;";

        toggle.setStyle(baseActive); // По замовчуванню ON

        toggle.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                toggle.setStyle(baseMuted); // Стан OFF
            } else {
                toggle.setStyle(baseActive); // Стан ON
            }
        });
    }


    private void styleSlider(Slider slider) {
        slider.setPrefWidth(240);

        // За допомогою CSS міняємо колір доріжки (track) та бігунка (thumb)
        slider.setStyle(
                ".slider .track {" +
                        "    -fx-background-color: #233232;" +
                        "    -fx-background-insets: 0;" +
                        "    -fx-background-radius: 2px;" +
                        "    -fx-pref-height: 6px;" +
                        "}" +
                        ".slider .thumb {" +
                        "    -fx-background-color: #9ba89e;" +
                        "    -fx-background-radius: 2px;" +
                        "    -fx-padding: 8px 5px;" +
                        "    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);" +
                        "}" +
                        ".slider:hover .thumb {" +
                        "    -fx-background-color: #b28d94;" +
                        "}"
        );
    }
}