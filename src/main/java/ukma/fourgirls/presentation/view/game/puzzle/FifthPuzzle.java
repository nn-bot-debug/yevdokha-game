package ukma.fourgirls.presentation.view.game.puzzle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ukma.fourgirls.GameContext;
import ukma.fourgirls.presentation.component.NotificationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FifthPuzzle extends Puzzle {

    private final java.util.function.Consumer<Integer> onFinishCallback;
    private final boolean isTimed;

    private int currentNumber = 0;
    private final int targetNumber = 17; // ціль
    private int secondsLeft = 50;

    private Timeline timeline;
    private final Text timerText;
    private final Text counterText;
    private final Text hintText;

    private final AnchorPane gamePane;

    private final Button btnLeft = new Button("Наліво");
    private final Button btnCenter = new Button("Прямо");
    private final Button btnRight = new Button("Направо");

    private final List<Button> pathButtons = new ArrayList<>();
    private boolean isGameActive = true;
    private final VBox mainLayout;

    private static class PathOperation {
        int value;
        boolean isMultiplication;

        PathOperation(int value, boolean isMultiplication) {
            this.value = value;
            this.isMultiplication = isMultiplication;
        }
    }

    public FifthPuzzle(GameContext context, boolean isTimed, java.util.function.Consumer<Integer> onFinishCallback) {
        super(context);
        this.onFinishCallback = onFinishCallback;
        this.isTimed = isTimed;

        this.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/puzzle.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/settings.css")).toExternalForm()
        );

        setupBackground("/images/canvas/mazes.png",4);

        Pane dimOverlay = new Pane();
        dimOverlay.setStyle("-fx-background-color: rgba(10, 12, 15, 0.45);");
        this.getChildren().add(dimOverlay);

        mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setVisible(false);

        VBox topLayout = new VBox(10);
        topLayout.setAlignment(Pos.TOP_CENTER);
        topLayout.setPadding(new Insets(25, 0, 0, 0));

        hintText = new Text("Блуд водить вас манівцями... Знайдіть шлях до Озера (Потрібно отримати: " + targetNumber + ")");
        hintText.setFill(Color.web("#e2e8f0"));
        try {
            Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 24);
            hintText.setFont(font != null ? font : Font.font("Verdana", 22));
        } catch (Exception e) {
            hintText.setFont(Font.font("Verdana", 22));
        }

        timerText = new Text();
        timerText.setFill(Color.web("#f87171"));
        try {
            Font timerFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Creepster-Regular.ttf"), 46);
            timerText.setFont(timerFont != null ? timerFont : Font.font("Verdana", 32));
        } catch (Exception e) {
            timerText.setFont(Font.font("Verdana", 32));
        }

        if (isTimed) {
            timerText.setText("00:50");
        }

        topLayout.getChildren().addAll(hintText, timerText);
        mainLayout.getChildren().add(topLayout);

        // Ігрове поле стежок
        gamePane = new AnchorPane();
        gamePane.setPrefSize(1400, 600);
        gamePane.setMaxSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);

        VBox counterBox = new VBox(2);
        counterBox.getStyleClass().add("maze-counter-box");
        counterBox.setAlignment(Pos.CENTER);

        Text label = new Text("Орієнтир:");
        label.setFill(Color.web("#94a3b8"));
        label.setFont(Font.font("Verdana", 12));

        counterText = new Text("0");
        counterText.setFill(Color.web("#10b981"));
        counterText.setFont(Font.font("Verdana", 32));
        counterText.setEffect(new DropShadow(8, Color.web("#10b981")));

        counterBox.getChildren().addAll(label, counterText);

        AnchorPane.setLeftAnchor(counterBox, 60.0);
        AnchorPane.setTopAnchor(counterBox, 0.0);
        gamePane.getChildren().add(counterBox);

        pathButtons.add(btnLeft);
        pathButtons.add(btnCenter);
        pathButtons.add(btnRight);

        for (Button b : pathButtons) {
            b.getStyleClass().add("maze-path-button");
            gamePane.getChildren().add(b);
        }

        double buttonsY = 530.0;
        AnchorPane.setLeftAnchor(btnLeft, 170.0);    AnchorPane.setTopAnchor(btnLeft, buttonsY);
        AnchorPane.setLeftAnchor(btnCenter, 600.0);  AnchorPane.setTopAnchor(btnCenter, buttonsY);
        AnchorPane.setRightAnchor(btnRight, 170.0);  AnchorPane.setTopAnchor(btnRight, buttonsY);

        mainLayout.getChildren().add(gamePane);

        generateRandomProceduralGraph();

        mainLayout.getChildren().add(createResultBox());
        this.getChildren().add(mainLayout);

        String title = "Головоломка: Стежки Блуда";
        String description =
                "Ви потрапили в містичні тенета лісового духа.\n\n" +
                        "Перед тобою три стежки. Кожна з них приховано змінює твій магічний орієнтир. " +
                        "Блуд перетасовує математичні правила після кожного твого кроку!\n\n" +
                        "Слідкуй за спливаючими плашками змін, прорахуй алгоритм і дійди до числа " + targetNumber + ", щоб вийти до Озера.";
        showInstructionOverlay(title, description, this::onStart);
    }

    private void onStart() {
        mainLayout.setVisible(true);
        if (isTimed) {
            startTimer();
        }
    }

    /**
     * Створюємо випадкові правила для кожної кнопки з пулу складності
     */
    private void generateRandomProceduralGraph() {
        List<PathOperation> pool = new ArrayList<>();
        pool.add(new PathOperation(4, false));   // +4
        pool.add(new PathOperation(5, false));   // +5
        pool.add(new PathOperation(7, false));   // +7
        pool.add(new PathOperation(2, true));    // x2
        pool.add(new PathOperation(-2, false));  // -2
        pool.add(new PathOperation(-3, false));  // -3
        pool.add(new PathOperation(-4, false));  // -4

        Collections.shuffle(pool);

        bindRoute(btnLeft, pool.get(0));
        bindRoute(btnCenter, pool.get(1));
        bindRoute(btnRight, pool.get(2));
    }

    private void bindRoute(Button btn, PathOperation op) {
        btn.setOnAction(e -> {
            if (!isGameActive) return;

            context.getAudio().buttonSound("/music/button-click-sound.wav");
            int oldNumber = currentNumber;

            this.getChildren().removeIf(node ->
                    node.getStyleClass().contains("notification-pane")
            );

            String operationText;
            if (op.isMultiplication) {
                operationText = "Зміна в " + op.value + " рази!";
            } else {
                String sign = (op.value > 0) ? "+" : "";
                operationText = "Зміна на " + sign + op.value + "!";
            }

            if (op.isMultiplication) {
                currentNumber *= op.value;
            } else {
                currentNumber += op.value;
            }

            if (currentNumber < 0 || currentNumber > 25) {
                currentNumber = 0;
                counterText.setText("0");
                counterText.setFill(Color.web("#ef4444"));
                hintText.setText("Морок згустився, і Блуд вивів вас на початок лабіринту!");
                hintText.setFill(Color.web("#f87171"));

                NotificationManager.showNotification(this, "Ви вилетіли за межі лісу! Повернення на 0.");

                if (!this.getChildren().isEmpty()) {
                    var lastNode = this.getChildren().get(this.getChildren().size() - 1);
                    if (lastNode.getStyleClass().contains("notification-pane")) {
                        StackPane.setMargin(lastNode, new Insets(0, 0, 140, 0));
                    }
                }

                generateRandomProceduralGraph();
                return;
            }

            counterText.setText(String.valueOf(currentNumber));
            NotificationManager.showNotification(this, operationText);

            if (!this.getChildren().isEmpty()) {
                var lastNode = this.getChildren().get(this.getChildren().size() - 1);
                if (lastNode.getStyleClass().contains("notification-pane")) {
                    StackPane.setMargin(lastNode, new Insets(0, 0, 140, 0));
                }
            }

            if (currentNumber > oldNumber) {
                counterText.setFill(Color.web("#34d399"));
                hintText.setText("Шукайте стежку далі... (Ціль: " + targetNumber + ")");
                hintText.setFill(Color.web("#a7f3d0"));
            } else {
                counterText.setFill(Color.web("#fbbf24"));
                hintText.setText("Здається, ви повертаєте не туди... (Ціль: " + targetNumber + ")");
                hintText.setFill(Color.web("#fde68a"));
            }

            if (currentNumber == targetNumber) {
                handleWin();
            } else {
                generateRandomProceduralGraph();
            }
        });
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            int minutes = secondsLeft / 60;
            int seconds = secondsLeft % 60;
            timerText.setText(String.format("%02d:%02d", minutes, seconds));
            if (secondsLeft <= 0)
                handleLose();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleWin() {
        isGameActive = false;
        if (timeline != null) {
            timeline.stop();
        }

        timerText.setFill(Color.LIGHTGREEN);
        counterText.setFill(Color.LIGHTGREEN);

        for (Button b : pathButtons) b.setDisable(true);

        session.changeKarma(1);

        showResultOverlay("Магічний лабіринт розступився! Попереду заблищало Озеро.", true, "Вийти до озера", () -> onFinishCallback.accept(1));
    }

    private void handleLose() {
        isGameActive = false;
        if (timeline != null) {
            timeline.stop();
        }

        timerText.setText("00:00");
        timerText.setFill(Color.RED);

        for (Button b : pathButtons) b.setDisable(true);

        session.changeKarma(-1);

        showResultOverlay("Блуд повністю заплутав ваші думки. Ви втомилися блукати...", false,"Прийняти долю", () -> onFinishCallback.accept(0));
    }
}