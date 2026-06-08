package ukma.fourgirls.ui.views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.LanguageManager;
import ukma.fourgirls.logic.StoryController;
import ukma.fourgirls.ui.animation.MenuAnimationCanvas;
import ukma.fourgirls.core.SceneManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuWindow extends Application {

    private Font font;

    @Override
    public void start(Stage primaryStage){
        SceneManager.getInstance().init(primaryStage);

        primaryStage.setTitle("YEVDOKHA-GAME");
        primaryStage.setFullScreen(true);
        //primaryStage.setResizable(false);
        StackPane root = new StackPane();

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

        VBox button = new VBox(20);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0,0,0,290));

        Scene mainScene = new Scene(root);

        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm());

        SceneManager.getInstance().setMainMenuRoot(root);

        Map<String, Runnable> buttonActions = new LinkedHashMap<>();
        buttonActions.put("menu.new", StoryController::startStory);
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
            //buttonN.setFont(font);
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
                entry.getValue().setText(LanguageManager.getString(entry.getKey()));
            }
        });

        root.getChildren().add(button);
        primaryStage.setScene(mainScene);

        AudioManager.getInstance().playBackgroundMusic("/music/background.mp3");

        primaryStage.show();
    }

    private void continueGame() {
    }
}