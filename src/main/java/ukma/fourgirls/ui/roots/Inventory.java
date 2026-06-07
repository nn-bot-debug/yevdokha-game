package ukma.fourgirls.ui.roots;

import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.InventoryState;

import java.util.List;
import java.util.Objects;

public class Inventory {
    private final StackPane container;
    private final HBox inventoryBoard;
    private final StackPane window;
    private static final double BOARD_HEIGHT = 170;

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

        inventoryBoard.getChildren().clear();
        List<Item> currentItems = InventoryState.getItems();

        for(Item item : currentItems){
            try {
                String path = item.getImagePath();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }

                java.io.InputStream imgStream = getClass().getClassLoader().getResourceAsStream(path);

                if (imgStream == null) {
                    throw new java.io.FileNotFoundException("Файл не знайдено в resources: " + path);
                }

                Image image = new Image(imgStream);
                ImageView icon = new ImageView(image);

                icon.setFitWidth(70);
                icon.setFitHeight(65);
                icon.setPreserveRatio(true);

                inventoryBoard.getChildren().add(icon);
            }
            catch (Exception e) {
                System.err.println("Помилка завантаження іконки (" + item.getName() + "): " + e.getMessage());

                Label fallbackLabel = new Label(item.getName());
                fallbackLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-text-alignment: center;");
                fallbackLabel.setWrapText(true);
                inventoryBoard.getChildren().add(fallbackLabel);
            }
        }
    }

    private StackPane createWindow() {
        StackPane window = new StackPane();
        window.getStyleClass().add("inventory-window");

        window.setPrefSize(340, 120);
        window.setMaxWidth(340);
        window.setMaxHeight(120);

        window.setTranslateY(50);
        return window;
    }

    private HBox createBoard() {
        HBox inventoryBoard = new HBox();
        inventoryBoard.setAlignment(Pos.CENTER_LEFT);
        inventoryBoard.getStyleClass().add("inventory-board");

// inventoryBoard.setSpacing(0);
        inventoryBoard.setPadding(new Insets(4, 0, 0, 57));

        inventoryBoard.setMaxWidth(480);
        inventoryBoard.setMaxHeight(BOARD_HEIGHT);
        inventoryBoard.setTranslateY(BOARD_HEIGHT);
        return inventoryBoard;
    }

    private void animation() {
        TranslateTransition showBoard = new TranslateTransition(Duration.millis(300), inventoryBoard);
        TranslateTransition hideBoard = new TranslateTransition(Duration.millis(300), inventoryBoard);

        TranslateTransition showWindow = new TranslateTransition(Duration.millis(300), window);
        TranslateTransition hideWindow = new TranslateTransition(Duration.millis(300), window);

        window.setOnMouseEntered(e -> {
            hideBoard.stop();
            hideWindow.stop();

            showBoard.setToY(32);
            showWindow.setToY(-92);

            showBoard.play();
            showWindow.play();
        });

        container.setOnMouseExited(e -> {

            showBoard.stop();
            showWindow.stop();

            hideBoard.setToY(BOARD_HEIGHT);
            hideWindow.setToY(50);

            hideBoard.play();
            hideWindow.play();
        });
    }

    public void setVisible(boolean isVisible) {
        this.container.setVisible(isVisible);
    }

    public void attachTo(StackPane root) {
        root.getChildren().add(this.container);
    }
}