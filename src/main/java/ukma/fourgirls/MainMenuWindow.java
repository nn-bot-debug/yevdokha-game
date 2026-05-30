package ukma.fourgirls;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Objects;

public class MainMenuWindow extends Application {

    private Font font;

    @Override
    public void start(Stage primaryStage){
        try{
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 24);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        primaryStage.setTitle("YEVDOKHA-GAME");
        primaryStage.setWidth(2048);
        primaryStage.setHeight(1152);
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

        VBox button = new VBox(20);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0,0,0,290));

        String [] buttonNames={
                "New Game",
                "Continue Game",
                "Instruction",
                "Settings",
                "Quit"
        };

        for (String name : buttonNames){
            Button buttonN = new Button(name);
            styleButton(buttonN);
            button.getChildren().add(buttonN);

        }
        root.getChildren().add(button);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
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
                "-fx-effect: dropshadow(three-pass-box, rgba(60, 80, 80, 0.8), 15, 0.0, 0,4);";

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