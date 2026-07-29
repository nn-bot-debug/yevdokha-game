package ukma.fourgirls.presentation.view.menu;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import ukma.fourgirls.application.service.Settings;
import ukma.fourgirls.infrastructure.localization.LanguageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SettingsView {
    private final Settings settings;
    private final Runnable onPlayClickSound;
    private final StackPane parentContainer;

    private StackPane overlayRoot;
    private final List<Runnable> languageUpdaters = new ArrayList<>();

    public SettingsView(Settings settings, Runnable onPlayClickSound, StackPane parentContainer) {
        this.settings = settings;
        this.onPlayClickSound = onPlayClickSound;
        this.parentContainer = parentContainer;
        buildUI();
        setupLocalizationListener();
    }

    private void buildUI() {
        createOverlayRoot();

        VBox dialogBox = new VBox(25);
        dialogBox.getStyleClass().add("settings-dialog");

        dialogBox.getChildren().addAll(
                createTitleLabel(),
                createSettingsGrid(),
                createCloseButton()
        );

        overlayRoot.getChildren().add(dialogBox);
    }

    private void createOverlayRoot() {
        overlayRoot = new StackPane();
        overlayRoot.getStyleClass().add("settings-overlay");
        overlayRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/menu/settings.css")).toExternalForm());
    }

    private Label createTitleLabel() {
        Label titleLabel = new Label(LanguageManager.getString("settings.title"));
        titleLabel.getStyleClass().add("settings-title");
        registerLanguageUpdater(() -> titleLabel.setText(LanguageManager.getString("settings.title")));
        return titleLabel;
    }

    private GridPane createSettingsGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("settings-grid");

        createSettingRow(grid, 0, "settings.music", settings.isMusicMuted(), settings.getMusicVolume(),
                settings::toggleMusic, settings::setMusicVolume);
        createSettingRow(grid, 2, "settings.sounds", settings.isSfxMuted(), settings.getSfxVolume(),
                settings::toggleSfx, settings::setSfxVolume);

        Slider vfxSlider = createSettingRow(grid, 4, "settings.vfx", settings.isVfxMuted(),
                settings.getVfxVolume(), settings::toggleVfx, settings::setVfxVolume);
        GridPane.setMargin(vfxSlider, new Insets(0, 0, 10, 0));

        buildLanguageRow(grid);

        return grid;
    }

    private void buildLanguageRow(GridPane grid) {
        Label langLabel = new Label(LanguageManager.getString("settings.language"));
        langLabel.getStyleClass().add("settings-label");
        registerLanguageUpdater(() -> langLabel.setText(LanguageManager.getString("settings.language")));

        Button langButton = new Button(settings.getCurrentLanguageCode());
        langButton.getStyleClass().add("settings-button");

        langButton.setOnAction(e -> {
            settings.toggleLanguage();
            langButton.setText(settings.getCurrentLanguageCode());
            playClickSound();
        });

        grid.add(langLabel, 0, 6);
        grid.add(langButton, 1, 6);
    }

    private Button createCloseButton() {
        Button closeButton = new Button(LanguageManager.getString("settings.close"));
        closeButton.getStyleClass().add("settings-button");
        registerLanguageUpdater(() -> closeButton.setText(LanguageManager.getString("settings.close")));

        closeButton.setOnAction(e -> {
            playClickSound();
            parentContainer.getChildren().remove(overlayRoot);
        });

        return closeButton;
    }

    private Slider createSettingRow(GridPane grid, int rowIndex, String labelKey,
                                    boolean isMuted, double currentVolume,
                                    Supplier<Boolean> onToggle, Consumer<Double> onVolumeChanged) {

        Label label = new Label(LanguageManager.getString(labelKey));
        label.getStyleClass().add("settings-label");

        ToggleButton toggleButton = new ToggleButton(LanguageManager.getString(isMuted ? "settings.off" : "settings.on"));
        toggleButton.getStyleClass().add("settings-toggle");
        toggleButton.setSelected(isMuted);

        grid.add(label, 0, rowIndex);
        grid.add(toggleButton, 1, rowIndex);

        Slider slider = new Slider(0.0, 1.0, currentVolume);
        slider.getStyleClass().add("settings-slider");
        slider.setDisable(isMuted);

        slider.valueProperty().addListener((obs, oldVal, newVal)
                -> onVolumeChanged.accept(newVal.doubleValue()));

        toggleButton.setOnAction(e -> {
            boolean newMutedState = onToggle.get();
            toggleButton.setText(LanguageManager.getString(newMutedState ? "settings.off" : "settings.on"));
            slider.setDisable(newMutedState);
            playClickSound();
        });

        grid.add(slider, 0, rowIndex + 1, 2, 1);

        registerLanguageUpdater(() -> {
            label.setText(LanguageManager.getString(labelKey));
            toggleButton.setText(LanguageManager.getString(toggleButton.isSelected() ? "settings.off" : "settings.on"));
        });

        return slider;
    }

    private void registerLanguageUpdater(Runnable updater) {
        languageUpdaters.add(updater);
    }

    private void setupLocalizationListener() {
        LanguageManager.addLanguageChangeListener(() -> languageUpdaters.forEach(Runnable::run));
    }

    private void playClickSound() {
        if (onPlayClickSound != null) {
            onPlayClickSound.run();
        }
    }

    public Parent getRoot() {
        return overlayRoot;
    }
}