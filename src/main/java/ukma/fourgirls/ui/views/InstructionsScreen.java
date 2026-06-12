package ukma.fourgirls.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.ui.animation.AnimationCanvas;

import java.util.Objects;

public class InstructionsScreen {

    private final StackPane root;
    private Font btnFont;
    private Font textFont;

    public InstructionsScreen() {
        this.root = new StackPane();

        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm());
        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/instruction.css")).toExternalForm());

        try {
            btnFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Creepster-Regular.ttf"), 22);
        }
        catch (Exception e) {
            btnFont = Font.font("Arial", 24);
        }
        try {
            textFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 22);
        }
        catch (Exception e) {
            textFont = Font.font("Arial", 24);
        }

        try {
            Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/notebook.png")));
            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            root.setBackground(new Background(backgroundImage));
        }
        catch (Exception e) {
            System.err.println("Не вдалося завантажити фон інструкції: " + e.getMessage());
        }

        AnimationCanvas animationCanvas = new AnimationCanvas();
        root.getChildren().add(animationCanvas);

        StackPane uiLayer = new StackPane();

        HBox textContainer = new HBox(140);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.setPadding(new Insets(250, 0, 250, 140));

        VBox leftPage = new VBox(20);
        leftPage.setAlignment(Pos.TOP_LEFT);
        leftPage.setPrefWidth(616);

        Label leftTitle = new Label("          Керування та інтерфейс");
        leftTitle.setFont(Font.font(textFont.getFamily(), 35));
        leftTitle.setAlignment(Pos.CENTER);
        leftTitle.getStyleClass().add("instruction-title-left");

        Label leftText = new Label(
                     "• Озирання (Панорама):\n" +
                        "Просто рухайте курсор миші вліво або вправо, щоб оглянути поточну кімнату\n\n" +
                        "• Взаємодія з предметами:\n" +
                        "Наводьте мишу на об'єкти. Якщо курсор змінюється на вказівний палець, ви можете натиснути на предмет, щоб підібрати його\n\n" +
                        "• Меню навігації:\n" +
                        "У верхньому правому кутку екрана знаходиться панель переміщення. Перемикайтеся між відкритими кімнатами одним кліком"
        );

        leftText.setFont(Font.font(textFont.getFamily(), 25));
        leftText.getStyleClass().add("instruction-text-left");
        leftText.setWrapText(true);

        leftPage.getChildren().addAll(leftTitle, leftText);

        VBox rightPage = new VBox(20);
        rightPage.setAlignment(Pos.TOP_LEFT);
        rightPage.setPrefWidth(700);

        Label rightTitle = new Label("\n              Інвентар та поради");
        rightTitle.setFont(Font.font(textFont.getFamily(), 35));
        rightTitle.setAlignment(Pos.CENTER);
        rightTitle.getStyleClass().add("instruction-title-right");

        Label rightText = new Label(
                "• Сховище речей (Інвентар):\n" +
                        "У нижній частині екрана розташована прихована панель. Наведіть курсор миші на низ екрана, щоб висунути інвентар та переглянути ваші речі\n\n" +
                        "• Підказки:\n" +
                        "Звуки мають значення. Слідкуйте за атмосферою та змінами навколо\n\n" +
                        "Будьте готові до несподіваних і лякаючих поворотів..."
        );

        rightText.setFont(Font.font(textFont.getFamily(), 25));
        rightText.getStyleClass().add("instruction-text-left");
        rightText.setWrapText(true);

        rightPage.getChildren().addAll(rightTitle, rightText);

        textContainer.getChildren().addAll(leftPage, rightPage);

        uiLayer.getChildren().add(textContainer);

        VBox bottomContainer = new VBox();
        bottomContainer.setAlignment(Pos.TOP_LEFT);
        bottomContainer.setPadding(new Insets(14, 0, 0, 14));

        Button backButton = new Button("Back to Menu");
        backButton.setFont(btnFont);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            SceneManager.getInstance().switchToMainMenu();
        });


        bottomContainer.getChildren().add(backButton);
        root.getChildren().add(uiLayer);
        root.getChildren().add(bottomContainer);
    }

    public Parent getRoot() {
        return root;
    }
}