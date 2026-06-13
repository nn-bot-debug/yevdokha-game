package ukma.fourgirls.ui.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.LanguageManager;
import ukma.fourgirls.ui.animation.MenuAnimationCanvas;
import ukma.fourgirls.core.SceneManager;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuWindow extends Application {

    private Font uaFont;
    private Font enFont;

    @Override
    public void start(Stage primaryStage){
        SceneManager.getInstance().init(primaryStage);

        primaryStage.setTitle("YEVDOKHA-GAME");
        primaryStage.setFullScreen(true);
        //primaryStage.setResizable(false);
        StackPane root = new StackPane();

        try{
            uaFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 20);
        }
        catch (Exception e) {
            uaFont = Font.font("Arial", 24);
        }

        try{
            enFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Creepster-Regular.ttf"), 20);
        }
        catch (Exception e) {
            enFont = Font.font("Arial", 24);
        }

        try{
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/MainMenuBackground.jpg")));
            BackgroundImage backgroundImageB = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );

            root.setBackground(new Background(backgroundImageB));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        MenuAnimationCanvas animationCanvas = new MenuAnimationCanvas();
        root.getChildren().add(animationCanvas);

        Label gameTitle = new Label("Побачиш мої чари?");

        try {
            Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 50);
            gameTitle.setFont(titleFont);
        }
        catch (Exception e) {
            gameTitle.setFont(Font.font("Arial", 20));

        }

        gameTitle.setTextFill(Color.web("#828f86"));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web("#404d42"));
        dropShadow.setRadius(0.3);
        dropShadow.setSpread(0.5);

        gameTitle.setEffect(dropShadow);

        Timeline timeline = new Timeline();

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(1.1),
                new KeyValue(dropShadow.radiusProperty(), 15)
        );

        timeline.getKeyFrames().add(keyFrame);
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        StackPane.setAlignment(gameTitle, Pos.TOP_LEFT);
        StackPane.setMargin(gameTitle, new Insets(180,0,0,150));
        root.getChildren().add(gameTitle);

        VBox button = new VBox(20);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0,0,0,290));

        Scene mainScene = new Scene(root);

        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm());

        SceneManager.getInstance().setMainMenuRoot(root);

        Map<String, Runnable> buttonActions = new LinkedHashMap<>();
        buttonActions.put("menu.new", () -> {
            ukma.fourgirls.state.GameState.reset();
            ukma.fourgirls.state.InventoryState.reset();

            ukma.fourgirls.ui.roots.ChildRoom childRoom = new ukma.fourgirls.ui.roots.ChildRoom();
            ukma.fourgirls.state.GameState.unlockLocation("ChildRoom");
            ukma.fourgirls.core.SceneManager.getInstance().switchToRoot((javafx.scene.layout.StackPane) childRoom.getRoot());
        });
        buttonActions.put("menu.continue", this::continueGame);
        buttonActions.put("menu.instruction", () -> SceneManager.getInstance().switchToRoot(new InstructionsScreen().getRoot()));
        buttonActions.put("menu.settings", () -> {
            SettingsScreen settings = new SettingsScreen(root);
            root.getChildren().add(settings.getRoot());
        });
        buttonActions.put("menu.quit", Platform::exit);

        Map<String, Button> menuButtons = new LinkedHashMap<>();

        for (Map.Entry<String, Runnable> entry : buttonActions.entrySet()) {
            String langKey = entry.getKey();
            Button buttonN = new Button(LanguageManager.getString(langKey));
            buttonN.setFont(isCurrentLanguageEnglish() ? enFont : uaFont);
            buttonN.getStyleClass().add("main-menu-button");
            buttonN.setOnAction(e -> {
                AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
                entry.getValue().run();
            });

            button.getChildren().add(buttonN);
            menuButtons.put(langKey, buttonN);
        }

        LanguageManager.addLanguageChangeListener(() -> {
            for (Map.Entry<String, Button> entry : menuButtons.entrySet()) {
                Button btn = entry.getValue();
                btn.setText(LanguageManager.getString(entry.getKey()));
                btn.setFont(isCurrentLanguageEnglish() ? enFont : uaFont);
            }
        });

        root.getChildren().add(button);
        primaryStage.setScene(mainScene);

        AudioManager.getInstance().playBackgroundMusic("/music/background.mp3");

        primaryStage.show();
    }

    private boolean isCurrentLanguageEnglish() {
        try{
            String newGameText = LanguageManager.getString("menu.new").toLowerCase();
            return newGameText.contains("new") || newGameText.contains("game");
        }
        catch (Exception e){
            return false;
        }
    }

    private void continueGame() {
        ukma.fourgirls.state.SaveData data = ukma.fourgirls.core.SaveManager.loadGame();

        if (data == null) {
            // Тут можна додати виклик віконця (Alert), яке скаже гравцеві, що збережень немає
            return;
        }

        ukma.fourgirls.state.GameState.reset();
        ukma.fourgirls.state.GameState.changeKarma(data.karmaBalance);
        ukma.fourgirls.state.GameState.activeSceneId = data.currentDialogNodeId;

        ukma.fourgirls.state.GameState.momRoomVisited = data.momRoomVisited;
        ukma.fourgirls.state.GameState.kitchenStormFinished = data.kitchenStormFinished;
        ukma.fourgirls.state.GameState.setChildRoomIntroPlayed(data.childRoomIntroPlayed);
        ukma.fourgirls.state.GameState.setDrawingPickedUp(data.drawingPickedUp);

        if (data.inventoryUnlocked) {
            ukma.fourgirls.state.GameState.unlockInventory();
        }

        if (data.unlockedLocations != null) {
            for (String loc : data.unlockedLocations) {
                ukma.fourgirls.state.GameState.unlockLocation(loc);
            }
        }

        ukma.fourgirls.state.InventoryState.reset();

        java.util.List<String> savedItems = data.inventoryItemNames;
        if (savedItems != null) {
            for (String itemName : savedItems) {
                ukma.fourgirls.domain.Item restoredItem = ukma.fourgirls.state.ItemRegistry.getItemByName(itemName);
                if (restoredItem != null) {
                    ukma.fourgirls.state.InventoryState.addItem(restoredItem);
                }
            }
        }

        System.out.println("Завантажуємо кімнату: " + data.currentRoomId);

        if ("ChildRoom".equals(data.currentRoomId)) {
            ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("ChildRoom", ukma.fourgirls.ui.roots.ChildRoom::new);
        }
        else if ("MomRoom".equals(data.currentRoomId)) {
            ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("MomRoom", ukma.fourgirls.ui.roots.MomRoom::new);
        }
        else if ("Kitchen".equals(data.currentRoomId)) {
            ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("Kitchen", ukma.fourgirls.ui.roots.Kitchen::new);
        }
        else if ("Corridor".equals(data.currentRoomId)) {
            ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("Corridor", ukma.fourgirls.ui.roots.Corridor::new);
        }
        else if ("Yard".equals(data.currentRoomId)) {
            ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("Yard", ukma.fourgirls.ui.roots.Yard::new);
        }
        else {
            System.err.println("Error: Unknown room saved - " + data.currentRoomId);
        }
    }
}