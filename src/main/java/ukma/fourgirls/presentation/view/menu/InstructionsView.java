package ukma.fourgirls.presentation.view.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ukma.fourgirls.infrastructure.localization.LanguageManager;
import ukma.fourgirls.presentation.animation.AnimationCanvas;
import ukma.fourgirls.presentation.controller.GameFlowController;

import java.util.Objects;

public class InstructionsView {

    private final Runnable onPlayClickSound;
    private final GameFlowController controller;
    private final StackPane root;

    private Font btnFont;
    private Font textFont;
    private Button backButton;

    public InstructionsView(Runnable onPlayClickSound, GameFlowController controller) {
        this.onPlayClickSound = onPlayClickSound;
        this.controller = controller;
        this.root = new StackPane();

        loadStylesheets();
        loadFonts();
        buildUI();
    }

    private void loadStylesheets() {
        root.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/menu/instruction.css")).toExternalForm()
        );
    }

    private void loadFonts() {
        btnFont = loadFontOrDefault("/fonts/Creepster-Regular.ttf");
        textFont = loadFontOrDefault("/fonts/Epoch_YP_Demo.ttf");
    }

    private Font loadFontOrDefault(String path) {
        try {
            return Font.loadFont(getClass().getResourceAsStream(path), 22);
        } catch (Exception e) {
            return Font.font("Arial", 24);
        }
    }

    private void buildUI() {
        setupBackground();

        root.getChildren().addAll(
                new AnimationCanvas(),
                createNotebookContentLayer(),
                createTopBarLayer()
        );
    }

    private void setupBackground() {
        try {
            Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/canvas/notebook.png")));
            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            root.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити фон інструкції: " + e.getMessage());
        }
    }

    private StackPane createNotebookContentLayer() {
        HBox textContainer = new HBox(80);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.paddingProperty().bind(
                javafx.beans.binding.Bindings.createObjectBinding(() -> {
                    double height = root.getHeight();
                    double width = root.getWidth();
                    return new Insets(height * 0.22, width * 0.08, height * 0.15, width * 0.08);
                }, root.heightProperty(), root.widthProperty())
        );

        String leftText = """
                • Озирання (Панорама):
                Просто рухайте курсор миші вліво або вправо, щоб оглянути поточну кімнату
                
                • Взаємодія з предметами:
                Наводьте мишу на об'єкти. Якщо курсор змінюється на вказівний палець, натисніть, щоб підібрати
                
                • Меню навігації:
                У верхньому правому кутку знаходиться панель переміщення між кімнатами""";

        String rightText = """
                • Сховище речей (Інвентар):
                У нижній частині екрана розташована прихована панель. Наведіть курсор на низ екрана, щоб висунути її
                
                • Підказки:
                Звуки мають значення. Слідкуйте за атмосферою та змінами навколо
                
                Будьте готові до несподіваних і лякаючих поворотів...""";

        textContainer.getChildren().addAll(
                createPage("Керування та інтерфейс", "instruction-title-left", leftText),
                createPage("Інвентар та поради", "instruction-title-right", rightText)
        );

        return new StackPane(textContainer);
    }

    private VBox createPage(String titleText, String titleStyleClass, String bodyText) {
        VBox page = new VBox(10);
        page.setAlignment(Pos.TOP_LEFT);
        page.maxWidthProperty().bind(root.widthProperty().multiply(0.35));

        Label title = new Label(titleText);
        title.setAlignment(Pos.CENTER);
        title.getStyleClass().add(titleStyleClass);
        title.setWrapText(true);

        Label text = new Label(bodyText);
        text.getStyleClass().add("instruction-text-left");
        text.setWrapText(true);

        page.getChildren().addAll(title, text);
        return page;
    }

    private VBox createTopBarLayer() {
        VBox bottomContainer = new VBox();
        bottomContainer.setAlignment(Pos.TOP_LEFT);
        bottomContainer.setPadding(new Insets(14, 0, 0, 14));

        backButton = new Button();
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> {
            onPlayClickSound.run();
            controller.switchToMainMenu();
        });

        setupLocalization();

        bottomContainer.getChildren().add(backButton);
        return bottomContainer;
    }

    private void setupLocalization() {
        LanguageManager.addLanguageChangeListener(this.updateTextsListener);
        updateTexts();
    }

    private final Runnable updateTextsListener = this::updateTexts;

    private void updateTexts() {
        String translated = LanguageManager.getString("button.back");
        backButton.setText(translated);

        if ("Назад до меню".equals(translated)) {
            backButton.setFont(Font.font(textFont.getFamily(), 18));
        } else {
            backButton.setFont(Font.font(btnFont.getFamily(), 20));
        }
    }

    public Parent getRoot() {
        return root;
    }
}