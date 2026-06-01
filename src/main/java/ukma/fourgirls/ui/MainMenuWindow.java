package ukma.fourgirls.ui;

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
import ukma.fourgirls.ui.animation.AnimationCanvas;
import ukma.fourgirls.SceneManager;
import ukma.fourgirls.ui.roots.ChildRoom;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuWindow extends Application {

    private Font font;

    @Override
    public void start(Stage primaryStage){
        SceneManager.getInstance().init(primaryStage);

        try{
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 24);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        primaryStage.setTitle("YEVDOKHA-GAME");
        primaryStage.setFullScreen(true);
        //primaryStage.setResizable(false);
        StackPane root = new StackPane();

        try{
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/MainMenuBackground.jpg")));
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

        AnimationCanvas animationCanvas = new AnimationCanvas();
        root.getChildren().add(animationCanvas);

        VBox button = new VBox(20);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0,0,0,290));

        Scene mainScene = new Scene(root);
        SceneManager.getInstance().setMainMenuRoot(root);

        Map<String, Runnable> buttonActions = new LinkedHashMap<>();
        buttonActions.put("New Game", () -> SceneManager.getInstance().switchToRoot(new ChildRoom().getRoot()));
        //buttonActions.put("Continue Game", this::continueGame);
        buttonActions.put("Instruction", () -> SceneManager.getInstance().switchToRoot(new InstructionsScreen().getRoot()));
        buttonActions.put("Settings", () -> {
            SettingsScreen settings = new SettingsScreen(root);
            root.getChildren().add(settings.getRoot());
        });
        buttonActions.put("Quit", Platform::exit);

        for (Map.Entry<String, Runnable> entry : buttonActions.entrySet()) {
            Button buttonN = new Button(entry.getKey());
            styleButton(buttonN);
            buttonN.setOnAction(e -> entry.getValue().run());
            button.getChildren().add(buttonN);
        }

        root.getChildren().add(button);
        primaryStage.setScene(mainScene);
        // Запускаємо фонову хорор-музику
        AudioManager.getInstance().playBackgroundMusic("/music/background.mp3");

        primaryStage.show();
    }

    private void styleButton(Button button){
        button.setPrefWidth(180);
        button.setPrefHeight(34);

        String buttonStyle =
                "-fx-background-color: rgba(35, 50, 50, 0.85);" +
                "-fx-text-fill: #9ba89e;" +
                "-fx-border-color: #3c5050;" +
                "-fx-border-width: 1px;" +
                "-fx-background-radius: 2px;" +
                "-fx-border-radius: 2px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(60, 80, 80, 0.8), 15, 0.0, 0,4);" +
                "-fx-cursor: hand;";

        button.setStyle(buttonStyle);

        button.setFont(font);

        button.setOnMouseEntered(e-> {
            button.setStyle(
               buttonStyle +
               "-fx-background-color: rgba(50, 70, 70, 0.9);" +
               "-fx-text-fill: #9ba89e;" +
               "-fx-border-color: #3c5050;" +
               "-fx-border-width: 4px;"
            );
            button.setFont(font);
        });

        button.setOnMouseExited(e-> {
            button.setStyle(buttonStyle);
            button.setFont(font);
        });
    }
}