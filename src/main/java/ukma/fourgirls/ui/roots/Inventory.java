package ukma.fourgirls.ui.roots;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.InventoryState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inventory {
    private final StackPane container;
    private final HBox inventoryBoard;
    private final StackPane window;
    private static final double BOARD_HEIGHT = 100;

    private final List<StackPane> cells = new ArrayList<>();

    public Inventory() {
        this.inventoryBoard = createBoard();
        this.window = createWindow();

        this.container = new StackPane(inventoryBoard, window);
        this.container.setPickOnBounds(false);
        StackPane.setAlignment(inventoryBoard, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(window, Pos.BOTTOM_CENTER);

        this.container.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/inventory.css")).toExternalForm());

        animation();

        setupStateListener();
    }

    private void setupStateListener() {
        InventoryState.getItems().addListener((ListChangeListener<Item>) change -> {
            updateUI();
        });

        updateUI();
    }

    private void updateUI() {
        List<Item> currentItems = InventoryState.getItems();

        for (int i = 0; i < cells.size(); i++) {
            StackPane cell = cells.get(i);
            cell.getChildren().clear();

            if (i < currentItems.size()) {
                Item item = currentItems.get(i);

                try {
                    String path = item.getImagePath();
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }

                    java.io.InputStream imgStream = getClass().getClassLoader().getResourceAsStream(path);

                    if (imgStream == null) {
                        throw new java.io.FileNotFoundException("Файл не знайдено в resources: " + path);
                    }

                    javafx.scene.image.Image img = new javafx.scene.image.Image(imgStream);
                    javafx.scene.image.ImageView icon = new javafx.scene.image.ImageView(img);

                    icon.setFitWidth(70);
                    icon.setFitHeight(65);
                    icon.setPreserveRatio(true);

                    cell.getChildren().add(icon);
                } catch (Exception e) {
                    System.err.println("Помилка завантаження іконки (" + item.getName() + "): " + e.getMessage());

                    Label fallbackLabel = new Label(item.getName());
                    fallbackLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-text-alignment: center;");
                    fallbackLabel.setWrapText(true);
                    cell.getChildren().add(fallbackLabel);
                }
            }
        }
    }

    private StackPane createWindow() {
        StackPane window = new StackPane();
        window.getStyleClass().add("inventory-window");

        window.setPrefSize(100, 30);
        window.setMaxWidth(100);
        window.setMaxHeight(30);
        return window;
    }

    private HBox createBoard() {
        HBox inventoryBoard = new HBox();
        inventoryBoard.setAlignment(Pos.CENTER);
        inventoryBoard.getStyleClass().add("inventory-board");

        for (int i = 0; i < 5; i++) {
            StackPane cell = new StackPane();
            cell.setPrefSize(90, 90);
            cell.getStyleClass().add("inventory-cell");

            cells.add(cell);

            inventoryBoard.getChildren().add(cell);
        }

        inventoryBoard.setMaxWidth(460);
        inventoryBoard.setMaxHeight(BOARD_HEIGHT);
        inventoryBoard.setTranslateY(BOARD_HEIGHT);
        return inventoryBoard;
    }

    private void animation() {
        TranslateTransition show = new TranslateTransition(Duration.millis(300), inventoryBoard);
        TranslateTransition hide = new TranslateTransition(Duration.millis(300), inventoryBoard);

        window.setOnMouseEntered(e -> {
            hide.stop();
            show.setToY(0);
            show.play();
            window.setVisible(false);
        });

        inventoryBoard.setOnMouseExited(e -> {
            show.stop();
            hide.setToY(BOARD_HEIGHT);
            hide.play();

            PauseTransition delay = new PauseTransition(Duration.millis(250));
            delay.setOnFinished(ev -> window.setVisible(true));
            delay.play();
        });
    }

    public void setVisible(boolean isVisible) {
        this.container.setVisible(isVisible);
    }

    public void attachTo(StackPane root) {
        root.getChildren().add(this.container);
    }
}