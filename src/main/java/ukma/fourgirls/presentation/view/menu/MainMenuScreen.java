package ukma.fourgirls.presentation.view.menu;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import ukma.fourgirls.GameContext;
import ukma.fourgirls.application.dto.SaveData;
import ukma.fourgirls.infrastructure.localization.LanguageManager;
import ukma.fourgirls.infrastructure.persistence.SaveManager;
import ukma.fourgirls.presentation.animation.MenuAnimationCanvas;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuScreen {

    private final StackPane root;
    private final GameContext context;
    private Font uaFont;
    private Font enFont;

    public MainMenuScreen(GameContext context) {
        this.context = context;
        this.root = new StackPane();

        initFonts();
        initBackground();
        initTitle();
        initButtons();
        initAuthors();

        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm());
    }

    // Віддаємо готову верстку
    public StackPane getRoot() {
        return root;
    }

    private void initFonts() {
        try { uaFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 20); }
        catch (Exception e) { uaFont = Font.font("Arial", 24); }

        try { enFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Creepster-Regular.ttf"), 20); }
        catch (Exception e) { enFont = Font.font("Arial", 24); }
    }

    private void initBackground() {
        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/canvas/MainMenuBackground.jpg")));
            BackgroundImage backgroundImageB = new BackgroundImage(
                    backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            root.setBackground(new Background(backgroundImageB));
            root.getChildren().add(new MenuAnimationCanvas());
        } catch (Exception e) {
            System.err.println("Помилка завантаження фону: " + e.getMessage());
        }
    }

    private void initTitle() {
        Label gameTitle = new Label("Побачиш мої чари?");
        try {
            Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 50);
            gameTitle.setFont(titleFont);
        } catch (Exception e) {
            gameTitle.setFont(Font.font("Arial", 20));
        }

        gameTitle.setTextFill(Color.web("#828f86"));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web("#404d42"));
        dropShadow.setRadius(0.3);
        dropShadow.setSpread(0.5);
        gameTitle.setEffect(dropShadow);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.1), new KeyValue(dropShadow.radiusProperty(), 15)));
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        StackPane.setAlignment(gameTitle, Pos.TOP_LEFT);
        StackPane.setMargin(gameTitle, new Insets(180, 0, 0, 150));
        root.getChildren().add(gameTitle);
    }

    private void initButtons() {
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(0, 0, 0, 290));

        Map<String, Runnable> buttonActions = new LinkedHashMap<>();
        buttonActions.put("menu.new", () -> {
            context.getScene().resetSession();
            context.getLocations().switchTo("ChildRoom");
        });
        buttonActions.put("menu.continue", this::continueGame);
        buttonActions.put("menu.instruction", () -> context.getScene().switchToRoot(new InstructionsScreen(context).getRoot()));
        buttonActions.put("menu.settings", () -> {
            SettingsScreen settings = new SettingsScreen(context, root);
            root.getChildren().add(settings.getRoot());
        });
        buttonActions.put("menu.quit", Platform::exit);

        Map<String, Button> menuButtons = new LinkedHashMap<>();

        for (Map.Entry<String, Runnable> entry : buttonActions.entrySet()) {
            String langKey = entry.getKey();
            Button button = new Button(LanguageManager.getString(langKey));
            button.setFont(isCurrentLanguageEnglish() ? enFont : uaFont);
            button.getStyleClass().add("main-menu-button");
            button.setOnAction(e -> {
                context.getAudio().buttonSound("/music/button-click-sound.wav");
                entry.getValue().run();
            });

            buttonBox.getChildren().add(button);
            menuButtons.put(langKey, button);
        }

        LanguageManager.addLanguageChangeListener(() -> {
            for (Map.Entry<String, Button> entry : menuButtons.entrySet()) {
                Button btn = entry.getValue();
                btn.setText(LanguageManager.getString(entry.getKey()));
                btn.setFont(isCurrentLanguageEnglish() ? enFont : uaFont);
            }
        });

        root.getChildren().add(buttonBox);
    }

    private void initAuthors() {
        Label authorsLabel = new Label("Бурмецька \nКазмірчук \nКорж \nСолтис");
        try {
            Font authorsFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 18);
            authorsLabel.setFont(authorsFont);
        } catch (Exception e) {
            authorsLabel.setFont(Font.font("Arial", 20));
        }
        authorsLabel.setTextFill(Color.web("#828f86"));
        StackPane.setAlignment(authorsLabel, Pos.BOTTOM_LEFT);
        StackPane.setMargin(authorsLabel, new Insets(0, 0, 30, 30));
        root.getChildren().add(authorsLabel);
    }

    private boolean isCurrentLanguageEnglish() {
        try {
            String newGameText = LanguageManager.getString("menu.new").toLowerCase();
            return newGameText.contains("new") || newGameText.contains("game");
        } catch (Exception e) {
            return false;
        }
    }

    private void continueGame() {
        SaveData data = SaveManager.loadGame();
        if (data == null) {
            return;
        }
        context.getScene().loadSession(data);
        if (!context.getLocations().switchTo(data.currentRoomId)) {
            System.err.println("Error: Unknown room saved - " + data.currentRoomId);
        }
    }
}