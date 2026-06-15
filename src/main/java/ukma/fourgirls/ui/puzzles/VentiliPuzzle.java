package ukma.fourgirls.ui.puzzles;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.core.AudioManager;

import java.util.Objects;

public class VentiliPuzzle extends StackPane {

    private final GameSession session;
    private final java.util.function.Consumer<Integer> onFinishCallback;

    // Стан чотирьох вентилів
    private boolean valveA = false;
    private boolean valveB = false;
    private boolean valveC = false;
    private boolean valveD = false;
    private boolean valveE = false;

    private Line pipeA, pipeB, pipeC, pipeD, pipeE;
    private Line pipeAnd1Out, pipeOrOut, pipeAnd2Out, pipeFinal;

    private Circle gateAND1, gateOR, gateAND2, mainReceiver;
    private Text textAND1, textOR, textAND2, textReceiver;

    private Timeline timeline;
    private int secondsLeft;
    private final Text timerText;
    private final Text hintText;

    private final VBox mainLayout;
    private final VBox resultBox;
    private final Text resultTitle;
    private final Button resultButton;

    private boolean isGameActive = true;

    // Налаштування кольорів для потоків смоли
    private static final Color EMPTY_PIPE = Color.rgb(65, 55, 50);
    private static final Color RESIN_FLOW = Color.rgb(245, 158, 11);
    private static final Color RESIN_GLOW = Color.rgb(251, 191, 36);

    public VentiliPuzzle(GameSession session, java.util.function.Consumer<Integer> onFinishCallback) {
        this.session = session;
        this.onFinishCallback = onFinishCallback;

        this.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/puzzle.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/settings.css")).toExternalForm()
        );

        try {
            Image bgImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/canvas/fon_ventili.png")));
            ImageView backgroundView = new ImageView(bgImg);
            backgroundView.setFitWidth(1920);
            backgroundView.setFitHeight(1080);
            backgroundView.setPreserveRatio(false);

            backgroundView.setEffect(new GaussianBlur(8));
            this.getChildren().add(backgroundView);
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити fon_ventili.png, заливаємо темним.");
            this.setStyle("-fx-background-color: #1a1410;");
        }

        Pane dimOverlay = new Pane();
        dimOverlay.setStyle("-fx-background-color: rgba(15, 12, 10, 0.45);");
        this.getChildren().add(dimOverlay);

        // --- ГОЛОВНИЙ МАКЕТ ---
        mainLayout = new VBox();
        mainLayout.getStyleClass().add("gradient-puzzle-container");
        mainLayout.setVisible(false);

        // --- ВЕРХНЯ ПАНЕЛЬ ---
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        VBox.setMargin(topBox, new Insets(25, 0, 0, 0));

        hintText = new Text("Налаштуйте вентилі так, щоб смола заповнила приймач...");
        hintText.setFill(Color.web("#cbd5e1"));
        try {
            Font customFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 24);
            hintText.setFont(customFont != null ? customFont : Font.font("Verdana", 22));
        } catch (Exception e) { hintText.setFont(Font.font("Verdana", 22)); }

        timerText = new Text();
        timerText.setFill(Color.web("#f87171"));
        try {
            Font timerFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Creepster-Regular.ttf"), 42);
            timerText.setFont(timerFont != null ? timerFont : Font.font("Verdana", 36));
        } catch (Exception e) {
            timerText.setFont(Font.font("Verdana", 36));
        }

        topBox.getChildren().addAll(hintText, timerText);
        mainLayout.getChildren().add(topBox);

        secondsLeft = 25;
        updateTimerDisplay();

        AnchorPane circuitPane = new AnchorPane();
        circuitPane.setPrefSize(920, 520);
        circuitPane.getStyleClass().add("puzzle-ventili-frame"); // Наш новий напівпрозорий CSS клас
        circuitPane.setMaxSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);

        // Стовпчик 1: Крантики-вентилі
        VBox valvesBox = new VBox(22);
        valvesBox.setAlignment(Pos.CENTER);
        ToggleButton tvA = createValveButton("Вентиль А");
        ToggleButton tvB = createValveButton("Вентиль Б");
        ToggleButton tvC = createValveButton("Вентиль В (NOT)");
        ToggleButton tvD = createValveButton("Вентиль Г");
        ToggleButton tvE = createValveButton("Вентиль Д");
        valvesBox.getChildren().addAll(tvA, tvB, tvC, tvD, tvE);

        AnchorPane.setLeftAnchor(valvesBox, 40.0);
        AnchorPane.setTopAnchor(valvesBox, 40.0);

        pipeA = createPipeLine(240, 82, 440, 120);
        pipeB = createPipeLine(240, 145, 440, 120);

        pipeC = createPipeLine(240, 210, 440, 280);
        pipeD = createPipeLine(240, 272, 440, 280);

        pipeAnd1Out = createPipeLine(440, 120, 828, 228); // Верхня гілка йде прямо до фіналу

        pipeOrOut = createPipeLine(440, 280, 640, 380);  // Вихід з OR йде на вхід другого AND
        pipeE = createPipeLine(240, 338, 640, 380);      // Вентиль Д йде на вхід другого AND

        pipeAnd2Out = createPipeLine(640, 380, 828, 228); // Вихід з другого AND йде до фіналу
        pipeFinal = createPipeLine(828, 228, 910, 228);

        StackPane nodeAND1 = createVisualGate("AND", gateAND1 = new Circle(20), textAND1 = new Text("AND"), 420, 100);
        StackPane nodeOR = createVisualGate("OR", gateOR = new Circle(20), textOR = new Text("OR"), 420, 260);
        StackPane nodeAND2 = createVisualGate("AND 2", gateAND2 = new Circle(20), textAND2 = new Text("AND"), 620, 360);

        StackPane nodeReceiver = createVisualGate("REC", mainReceiver = new Circle(28), textReceiver = new Text("*"), 800, 200);

        // Збираємо до купи на AnchorPane
        circuitPane.getChildren().addAll(pipeA, pipeB, pipeC, pipeD, pipeE, pipeAnd1Out, pipeOrOut, pipeAnd2Out, pipeFinal);
        circuitPane.getChildren().addAll(valvesBox, nodeAND1, nodeOR, nodeAND2, nodeReceiver);
        mainLayout.getChildren().add(circuitPane);

        // Кліки
        tvA.setOnAction(e -> { valveA = tvA.isSelected(); updateHydraulics(); });
        tvB.setOnAction(e -> { valveB = tvB.isSelected(); updateHydraulics(); });
        tvC.setOnAction(e -> { valveC = tvC.isSelected(); updateHydraulics(); });
        tvD.setOnAction(e -> { valveD = tvD.isSelected(); updateHydraulics(); });
        tvE.setOnAction(e -> { valveE = tvE.isSelected(); updateHydraulics(); });

        // --- ПАНЕЛЬ РЕЗУЛЬТАТУ ---
        resultBox = new VBox(15);
        resultBox.getStyleClass().add("puzzle-result-box");
        VBox.setMargin(resultBox, new Insets(0, 0, 30, 0));
        resultBox.setVisible(false);
        resultBox.managedProperty().bind(resultBox.visibleProperty());

        resultTitle = new Text();
        resultTitle.setFont(Font.font("Verdana", 24));
        resultButton = new Button();
        resultButton.getStyleClass().add("puzzle-action-button");
        resultBox.getChildren().addAll(resultTitle, resultButton);
        mainLayout.getChildren().add(resultBox);

        this.getChildren().add(mainLayout);

        updateHydraulics();
        showInstructionOverlay();
    }

    private ToggleButton createValveButton(String text) {
        ToggleButton btn = new ToggleButton(text);
        btn.getStyleClass().add("settings-button");
        btn.setStyle("-fx-pref-width: 170px; -fx-pref-height: 40px; -fx-cursor: hand; -fx-font-size: 13px;");
        return btn;
    }

    private Line createPipeLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(12);
        line.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        line.setStroke(EMPTY_PIPE);
        return line;
    }

    private StackPane createVisualGate(String id, Circle circle, Text text, double x, double y) {
        StackPane pane = new StackPane();
        pane.setLayoutX(x);
        pane.setLayoutY(y);

        circle.setRadius(circle.getRadius());
        circle.getStyleClass().add("puzzle-gate-node");

        text.getStyleClass().add("puzzle-gate-text");

        pane.getChildren().addAll(circle, text);
        pane.setEffect(new DropShadow(15, Color.BLACK));
        return pane;
    }

    /**
     * ⚡ ПРОРАХУНОК БУЛЕВОЇ ЛОГІКИ ТА КОЛЬОРУ ТРУБ
     */
    private void updateHydraulics() {
        if (!isGameActive) return;

        // Рівень 1: Вентилі А та Б
        pipeA.setStroke(valveA ? RESIN_FLOW : EMPTY_PIPE);
        pipeB.setStroke(valveB ? RESIN_FLOW : EMPTY_PIPE);

        boolean and1Open = valveA && valveB;
        gateAND1.setFill(and1Open ? RESIN_GLOW : Color.rgb(160, 50, 50));
        textAND1.setFill(and1Open ? Color.rgb(20, 40, 20) : Color.WHITE);
        pipeAnd1Out.setStroke(and1Open ? RESIN_FLOW : EMPTY_PIPE);

        // Рівень 2: Вентилі В (NOT) та Г
        boolean valveCFlow = !valveC;
        pipeC.setStroke(valveCFlow ? RESIN_FLOW : EMPTY_PIPE);
        pipeD.setStroke(valveD ? RESIN_FLOW : EMPTY_PIPE);

        boolean orOpen = valveCFlow || valveD;
        gateOR.setFill(orOpen ? RESIN_GLOW : Color.rgb(160, 50, 50));
        textOR.setFill(orOpen ? Color.rgb(20, 40, 20) : Color.WHITE);
        pipeOrOut.setStroke(orOpen ? RESIN_FLOW : EMPTY_PIPE);

        // Рівень 3: Вхід від OR та нового Вентиля Д (AND 2)
        pipeE.setStroke(valveE ? RESIN_FLOW : EMPTY_PIPE);

        boolean and2Open = orOpen && valveE;
        gateAND2.setFill(and2Open ? RESIN_GLOW : Color.rgb(160, 50, 50));
        textAND2.setFill(and2Open ? Color.rgb(20, 40, 20) : Color.WHITE);
        pipeAnd2Out.setStroke(and2Open ? RESIN_FLOW : EMPTY_PIPE);

        // Фінал: Перемога, якщо верхня гілка AND1 та нижня AND2 активні разом
        boolean puzzleSolved = and1Open && and2Open;
        pipeFinal.setStroke(puzzleSolved ? RESIN_FLOW : EMPTY_PIPE);
        mainReceiver.setFill(puzzleSolved ? Color.LIGHTGREEN : Color.rgb(130, 40, 40));
        textReceiver.setFill(puzzleSolved ? Color.rgb(10, 30, 10) : Color.WHITE);

        if (puzzleSolved) {
            handleWin();
        }
    }

    private void showInstructionOverlay() {
        VBox tutorialBox = new VBox(20);
        tutorialBox.setAlignment(Pos.CENTER);
        tutorialBox.setMaxWidth(500);
        tutorialBox.setMaxHeight(380);
        tutorialBox.setPadding(new Insets(30, 40, 30, 40));
        tutorialBox.getStyleClass().add("settings-dialog");

        Label titleLabel = new Label("Вентилі дерева");
        titleLabel.getStyleClass().add("settings-title");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(
                "Направте гарячу смолу по каналах кори до головного приймача:\n\n" +
                        "• Шлюз AND вимагає відкриття обох вентилів одночасно.\n" +
                        "• Шлюз OR пропустить потік, якщо хоча б один кран відкритий.\n" +
                        "Впорайтесь, поки є час!"
        );
        descriptionLabel.getStyleClass().add("settings-label");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-alignment: center; -fx-line-spacing: 4;");

        Button acceptButton = new Button("ЗАПУСТИТИ");
        acceptButton.getStyleClass().add("settings-button");
        acceptButton.setPrefWidth(160);
        acceptButton.setStyle("-fx-cursor: hand;");

        acceptButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            this.getChildren().remove(tutorialBox);
            mainLayout.setVisible(true);

            session.setKarmaListener((currentKarma, addedPoints) -> {
                ukma.fourgirls.core.StatNotification.show(this, currentKarma, addedPoints);
                var notification = this.getChildren().get(this.getChildren().size() - 1);
                StackPane.setAlignment(notification, Pos.TOP_CENTER);
                StackPane.setMargin(notification, new Insets(60, 0, 0, 0));
                notification.setTranslateX(0);
                notification.toFront();
            });
            startTimer();
        });

        tutorialBox.getChildren().addAll(titleLabel, descriptionLabel, acceptButton);
        this.getChildren().add(tutorialBox);
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            updateTimerDisplay();
            if (secondsLeft <= 0) handleLose();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerDisplay() {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void handleWin() {
        isGameActive = false;
        timeline.stop();
        timerText.setFill(Color.LIGHTGREEN);
        hintText.setFill(Color.LIGHTGREEN);

        session.changeKarma(1);

        resultTitle.setText("Потік смоли успішно спрямовано!");
        resultTitle.getStyleClass().removeAll("puzzle-text-lose");
        resultTitle.getStyleClass().add("puzzle-text-win");

        resultButton.setText("Продовжити шлях");
        resultButton.setOnAction(e -> {
            if (onFinishCallback != null) onFinishCallback.accept(1);
        });
        resultBox.setVisible(true);
    }

    private void handleLose() {
        isGameActive = false;
        timeline.stop();
        timerText.setText("00:00");
        timerText.setFill(Color.RED);
        hintText.setFill(Color.RED);

        session.changeKarma(-1);

        resultTitle.setText("Час вийшов! Багато смоли пролилось.");
        resultTitle.getStyleClass().removeAll("puzzle-text-win");
        resultTitle.getStyleClass().add("puzzle-text-lose");

        resultButton.setText("Прийняти долю");
        resultButton.setOnAction(e -> {
            if (onFinishCallback != null) onFinishCallback.accept(0); // 0 означає невдачу, але сюжет рухається далі
        });
        resultBox.setVisible(true);
    }
}
