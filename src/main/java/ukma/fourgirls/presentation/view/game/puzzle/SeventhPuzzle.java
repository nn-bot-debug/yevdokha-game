package ukma.fourgirls.presentation.view.game.puzzle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import ukma.fourgirls.GameContext;
import ukma.fourgirls.presentation.component.NotificationManager;
import ukma.fourgirls.presentation.component.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SeventhPuzzle extends Puzzle {

    private final java.util.function.Consumer<Integer> onFinishCallback;

    private int weighingsLeft = 4;
    private boolean hasDrawing = false;
    private boolean isGameActive = true;

    private final Text counterText;
    private final Text hintText;

    private final AnchorPane gamePane;

    private final Button btnWeigh = new Button("Зважити");
    private final Button btnSubmit = new Button("Перевірити");

    // Компоненти ваг
    private Rotate beamRotation;
    private ImageView beam;
    private ImageView leftPan;
    private ImageView rightPan;

    private final List<DraggableItem> leftPanItems = new ArrayList<>();
    private final List<DraggableItem> rightPanItems = new ArrayList<>();

    // Нові колекції для сортування
    private final List<ImageView> trays = new ArrayList<>();
    private final List<DraggableItem> allItems = new ArrayList<>();

    public SeventhPuzzle(GameContext context, java.util.function.Consumer<Integer> onFinishCallback) {
        super(context);
        this.onFinishCallback = onFinishCallback;

        this.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/puzzle.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/settings.css")).toExternalForm()
        );

        setupBackground("/images/canvas/for_scale_puzzle.png",4);

        Pane dimOverlay = new Pane();
        dimOverlay.setStyle("-fx-background-color: rgba(10, 12, 15, 0.45);");
        this.getChildren().add(dimOverlay);

        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        VBox topLayout = new VBox(10);
        topLayout.setAlignment(Pos.TOP_CENTER);
        topLayout.setPadding(new Insets(25, 0, 0, 0));

        hintText = new Text("Розставте всі предмети на підноси у порядку зростання їх ваги.");
        hintText.setFill(Color.web("#e2e8f0"));
        hintText.setFont(Font.font("Verdana", 22));

        topLayout.getChildren().add(hintText);
        mainLayout.getChildren().add(topLayout);

        gamePane = new AnchorPane();
        gamePane.setPrefSize(1400, 600);
        gamePane.setMaxSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);

        VBox counterBox = new VBox(2);
        counterBox.getStyleClass().add("maze-counter-box");
        counterBox.setAlignment(Pos.CENTER);

        Text label = new Text("Залишилось зважувань:");
        label.setFill(Color.web("#94a3b8"));
        label.setFont(Font.font("Verdana", 12));

        counterText = new Text(String.valueOf(weighingsLeft));
        counterText.setFill(Color.web("#10b981"));
        counterText.setFont(Font.font("Verdana", 32));
        counterText.setEffect(new DropShadow(8, Color.web("#10b981")));

        counterBox.getChildren().addAll(label, counterText);
        AnchorPane.setLeftAnchor(counterBox, 60.0);
        AnchorPane.setTopAnchor(counterBox, 0.0);
        gamePane.getChildren().add(counterBox);

        initScaleGraphics();
        initTraysAndItems();
        initActionButtons();

        mainLayout.getChildren().add(gamePane);

        mainLayout.getChildren().add(createResultBox());
        this.getChildren().add(mainLayout);

        String title = "Головоломка: Золоті Ваги";
        String description =
                "Розмір обманливий. Знайдіть правильну вагу кожного предмета.\n\n" +
                        "Використайте ваги (4 спроби), щоб визначити масу, а потім розставте предмети на підноси " +
                        "від найлегшого (зліва) до найважчого (справа).";
        showInstructionOverlay(title, description, this::onStart);
        Inventory inventory = new Inventory(session);
        inventory.attachTo(this);
    }

    private void onStart() {}

    private void initScaleGraphics() {
        ImageView base = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/objects/scale.png"))));
        AnchorPane.setLeftAnchor(base, 500.0); AnchorPane.setTopAnchor(base, 100.0);

        Image beamImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/objects/koromyslo.png")));
        beam = new ImageView(beamImg);
        double targetBeamWidth = 400.0;
        beam.setFitWidth(targetBeamWidth);
        beam.setPreserveRatio(true);
        double targetBeamHeight = targetBeamWidth * (beamImg.getHeight() / beamImg.getWidth());

        beamRotation = new Rotate(0, targetBeamWidth / 2, targetBeamHeight / 2);
        beam.getTransforms().add(beamRotation);
        AnchorPane.setLeftAnchor(beam, 550.0); AnchorPane.setTopAnchor(beam, 130.0);

        Image leftPanImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/objects/chasha.png")));
        leftPan = new ImageView(leftPanImg);
        leftPan.setFitWidth(300.0);
        leftPan.setPreserveRatio(true);
        AnchorPane.setLeftAnchor(leftPan, 425.0); AnchorPane.setTopAnchor(leftPan, 225.0);

        Image rightPanImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/objects/chasha.png")));
        rightPan = new ImageView(rightPanImg);
        rightPan.setFitWidth(300.0);
        rightPan.setPreserveRatio(true);
        AnchorPane.setLeftAnchor(rightPan, 785.0); AnchorPane.setTopAnchor(rightPan, 225.0);

        gamePane.getChildren().addAll(beam, base, leftPan, rightPan);
    }

    private void initActionButtons() {
        btnWeigh.getStyleClass().add("puzzle-action-button");
        AnchorPane.setLeftAnchor(btnWeigh, 300.0); AnchorPane.setTopAnchor(btnWeigh, 150.0);

        btnSubmit.getStyleClass().add("puzzle-action-button");
        AnchorPane.setLeftAnchor(btnSubmit, 1000.0); AnchorPane.setTopAnchor(btnSubmit, 150.0);

        btnWeigh.setOnAction(e -> handleWeighAction());
        btnSubmit.setOnAction(e -> handleCheckSortingAction()); // Підключили нову логіку

        gamePane.getChildren().addAll(btnWeigh, btnSubmit);
    }

    private void initTraysAndItems() {
        List<String> files = new ArrayList<>(Arrays.asList("brush.png", "glechik.png", "namysto.png", "lusterko.png", "perlyna.png"));
        List<Integer> weights = new ArrayList<>(Arrays.asList(30, 10, 20, 40, 50));
        List<Double> widths = new ArrayList<>(Arrays.asList(160.0, 130.0, 90.0, 110.0, 60.0));
        if (session != null && session.getInventoryItems() != null) {
            hasDrawing = session.getInventoryItems().stream().anyMatch(item -> item.getName().equals("Малюнок"));
        }

        if (hasDrawing) {
            files.add("drawing.png");
            weights.add(60);
            widths.add(100.0);
        }

        int itemCount = files.size();

        double trayWidth = 140.0;
        double spacing = 200.0;
        double startX = (1400.0 - (itemCount * spacing)) / 2.0;
        double traysY = 600.0;
        for (int i = 0; i < itemCount; i++) {
            Image trayImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/objects/podnosik.png")));
            ImageView tray = new ImageView(trayImg);
            tray.setFitWidth(trayWidth);
            tray.setPreserveRatio(true);

            double currentX = startX + (i * spacing);
            AnchorPane.setLeftAnchor(tray, currentX);
            AnchorPane.setTopAnchor(tray, traysY);

            trays.add(tray);
            gamePane.getChildren().add(tray);
            Image itemImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/objects/" + files.get(i))));
            double itemX = currentX + (trayWidth - widths.get(i)) / 2;
            DraggableItem item = new DraggableItem(itemImg, weights.get(i), widths.get(i), itemX, traysY - 20);
            allItems.add(item);
            gamePane.getChildren().add(item);
        }
    }

    private void handleWeighAction() {
        if (!isGameActive || weighingsLeft <= 0) return;

        if (leftPanItems.isEmpty() || rightPanItems.isEmpty()) {
            NotificationManager.showNotification(this, "Покладіть предмети на обидві чаші для порівняння!");
            return;
        }

        context.getAudio().buttonSound("/music/button-click-sound.wav");

        weighingsLeft--;
        counterText.setText(String.valueOf(weighingsLeft));

        if (weighingsLeft == 0) {
            counterText.setFill(Color.web("#ef4444"));
            hintText.setText("Остання спроба вичерпана! Розставляйте предмети.");
            btnWeigh.setDisable(true);
        }

        int leftTotalWeight = leftPanItems.stream().mapToInt(DraggableItem::getWeight).sum();
        int rightTotalWeight = rightPanItems.stream().mapToInt(DraggableItem::getWeight).sum();

        int difference = rightTotalWeight - leftTotalWeight;
        double targetAngle = Math.max(-25, Math.min(25, difference * 1.5));

        beamRotation.setAngle(targetAngle);

        double beamRadius = beam.getFitWidth() / 2;
        double offset = Math.sin(Math.toRadians(targetAngle)) * beamRadius;

        leftPan.setTranslateY(-offset);
        rightPan.setTranslateY(offset);

        for (DraggableItem item : leftPanItems) item.setTranslateY(-offset);
        for (DraggableItem item : rightPanItems) item.setTranslateY(offset);
    }

    private void resetScale() {
        beamRotation.setAngle(0);
        leftPan.setTranslateY(0);
        rightPan.setTranslateY(0);
        for (DraggableItem item : leftPanItems) item.setTranslateY(0);
        for (DraggableItem item : rightPanItems) item.setTranslateY(0);
    }

    private void handleCheckSortingAction() {
        if (!isGameActive) return;

        List<Integer> sortedWeights = new ArrayList<>();
        for (ImageView tray : trays) {
            DraggableItem itemOnThisTray = null;
            int itemsCountOnTray = 0;
            double trayCenterX = AnchorPane.getLeftAnchor(tray) + (tray.getFitWidth() / 2);
            for (DraggableItem item : allItems) {
                if (Math.abs(item.getCenterX() - trayCenterX) < 5.0) {
                    itemOnThisTray = item;
                    itemsCountOnTray++;
                }
            }

            if (itemsCountOnTray != 1) {
                NotificationManager.showNotification(this, "Помилка: На кожному підносі має лежати рівно 1 предмет!");
                return;
            }

            sortedWeights.add(itemOnThisTray.getWeight());
        }
        boolean isSortedCorrectly = true;
        for (int i = 0; i < sortedWeights.size() - 1; i++) {
            if (sortedWeights.get(i) >= sortedWeights.get(i + 1)) {
                isSortedCorrectly = false;
                break;
            }
        }

        if (isSortedCorrectly) {
            handleWin();
        } else {
            NotificationManager.showNotification(this, "Послідовність неправильна. Спробуйте змінити порядок!");
        }
    }

    private void handleWin() {
        isGameActive = false;
        setupKarmaListener(80);

        int karmaReward = hasDrawing ? 2 : 1;
        if (session != null) {
            session.changeKarma(karmaReward);
        }

        showResultOverlay("Ідеальний баланс! Отримано карми: +" + karmaReward,true,"Йти далі",() -> onFinishCallback.accept(karmaReward));
    }


    class DraggableItem extends ImageView {
        private final int weight;
        private double mouseAnchorX;
        private double mouseAnchorY;
        private final double originalX;
        private final double originalY;

        public DraggableItem(Image image, int weight, double targetWidth, double startX, double startY) {
            super(image);
            this.weight = weight;
            this.originalX = startX;
            this.originalY = startY;

            this.setFitWidth(targetWidth);
            this.setPreserveRatio(true);
            AnchorPane.setLeftAnchor(this, startX);
            AnchorPane.setTopAnchor(this, startY);

            setOnMousePressed(event -> {
                if (!isGameActive) return;

                mouseAnchorX = event.getSceneX() - AnchorPane.getLeftAnchor(this);
                mouseAnchorY = event.getSceneY() - AnchorPane.getTopAnchor(this);

                boolean wasInLeft = leftPanItems.remove(this);
                boolean wasInRight = rightPanItems.remove(this);

                if (wasInLeft || wasInRight) {
                    resetScale();
                }

                setTranslateY(0);
                toFront();
            });

            setOnMouseDragged(event -> {
                if (!isGameActive) return;
                AnchorPane.setLeftAnchor(this, event.getSceneX() - mouseAnchorX);
                AnchorPane.setTopAnchor(this, event.getSceneY() - mouseAnchorY);
            });

            setOnMouseReleased(event -> {
                if (!isGameActive) return;

                boolean placed = false;

                if (isInside(getCenterX(), getCenterY(), leftPan)) {
                    leftPanItems.add(this);
                    placed = true;
                } else if (isInside(getCenterX(), getCenterY(), rightPan)) {
                    rightPanItems.add(this);
                    placed = true;
                } else {
                    ImageView closestTray = null;
                    double minDistance = Double.MAX_VALUE;

                    for (ImageView tray : trays) {
                        double trayCenterX = AnchorPane.getLeftAnchor(tray) + (tray.getFitWidth() / 2);
                        double distance = Math.abs(this.getCenterX() - trayCenterX);

                        // Якщо предмет перебуває в межах зони підноса по горизонталі
                        if (distance < 100.0 && distance < minDistance) {
                            minDistance = distance;
                            closestTray = tray;
                        }
                    }

                    if (closestTray != null) {
                        double snapX = AnchorPane.getLeftAnchor(closestTray) + (closestTray.getFitWidth() - this.getBoundsInParent().getWidth()) / 2;
                        double snapY = AnchorPane.getTopAnchor(closestTray) - this.getBoundsInParent().getHeight() + 40;

                        AnchorPane.setLeftAnchor(this, snapX);
                        AnchorPane.setTopAnchor(this, snapY);
                        placed = true;
                    }
                }

                if (!placed) {
                    AnchorPane.setLeftAnchor(this, originalX);
                    AnchorPane.setTopAnchor(this, originalY);
                }
            });
        }

        public int getWeight() { return weight; }

        public double getCenterX() {
            return AnchorPane.getLeftAnchor(this) + (getBoundsInParent().getWidth() / 2);
        }

        public double getCenterY() {
            return AnchorPane.getTopAnchor(this) + (getBoundsInParent().getHeight() / 2);
        }
    }
    private boolean isInside(double x, double y, ImageView target) {
        double targetX = AnchorPane.getLeftAnchor(target);
        double targetY = AnchorPane.getTopAnchor(target) + target.getTranslateY();
        return x >= targetX && x <= targetX + target.getBoundsInParent().getWidth() &&
                y >= targetY && y <= targetY + target.getBoundsInParent().getHeight();
    }
}
