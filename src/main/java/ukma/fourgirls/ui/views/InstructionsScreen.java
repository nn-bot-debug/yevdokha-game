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
    private Font font;

    public InstructionsScreen() {
        this.root = new StackPane();

        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm());
        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/instruction.css")).toExternalForm());

        try {
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 22);
        }
        catch (Exception e) {
            font = Font.font("Arial", 24);
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

        HBox textContainer = new HBox(210);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.setPadding(new Insets(260, 210, 260, 210));

        VBox leftPage = new VBox(20);
        leftPage.setAlignment(Pos.TOP_LEFT);
        leftPage.setPrefWidth(600);

        Label leftTitle = new Label("Керування та інтерфейс");
        leftTitle.setFont(Font.font(font.getFamily(), 34));
        leftTitle.setAlignment(Pos.CENTER);
        leftTitle.getStyleClass().add("instruction-title-left");

        Label leftText = new Label(
                     "• Озирання (Панорама):" +
                        "Просто рухайте курсор миші вліво або вправо, щоб оглянути поточну кімнату" +
                        "• Взаємодія з предметами:" +
                        "Наводьте мишу на об'єкти. Якщо курсор змінюється на вказівний палець, ви можете натиснути на предмет, щоб підібрати його" +
                        "• Меню навігації:" +
                        "У верхньому правому кутку екрана знаходиться панель переміщення. Перемикайтеся між відкритими кімнатами будинку одним кліком."
        );

        leftText.setFont(Font.font(font.getFamily(), 24));
        leftText.getStyleClass().add("instruction-text-left");
        leftText.setWrapText(true);

        leftPage.getChildren().addAll(leftTitle, leftText);

        VBox rightPage = new VBox(20);
        rightPage.setAlignment(Pos.TOP_LEFT);
        rightPage.setPrefWidth(600);

        Label rightTitle = new Label("Інвентар та поради");
        rightTitle.setFont(Font.font(font.getFamily(), 34));
        rightTitle.setAlignment(Pos.CENTER);
        rightTitle.getStyleClass().add("instruction-title-right");

        Label rightText = new Label(
                "• Сховище речей (Інвентар):" +
                        "У нижній частині екрана розташована прихована панель. Наведіть курсор миші на самий низ екрана, щоб висунути інвентар та переглянути ваші речі" +
                        "• Підказки:" +
                        "Звуки мають значення. Слідкуйте за атмосферою та змінами навколо." +
                        "Будьте готові до несподіваних і лякаючих поворотів..."
        );

        rightText.setFont(Font.font(font.getFamily(), 24));
        rightText.getStyleClass().add("instruction-text-left");
        rightText.setWrapText(true);

        rightPage.getChildren().addAll(rightTitle, rightText);

        textContainer.getChildren().addAll(leftPage, rightPage);

        uiLayer.getChildren().add(textContainer);

        VBox bottomContainer = new VBox();
        bottomContainer.setAlignment(Pos.TOP_LEFT);
        bottomContainer.setPadding(new Insets(14, 0, 0, 14));

        Button backButton = new Button("Back to Menu");
        backButton.setFont(font);
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