package ukma.fourgirls.ui.roots;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Inventory {
    private final StackPane container;
    private final HBox inventoryBoard;
    private final StackPane window;
    private static final double BOARD_HEIGHT = 100;

    public Inventory() {
        this.inventoryBoard = createBoard();

        this.window = createWindow();

        this.container = new StackPane(inventoryBoard, window);
        this.container.setPickOnBounds(false);
        StackPane.setAlignment(inventoryBoard, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(window, Pos.BOTTOM_CENTER);

        animation();
    }

    private StackPane createWindow() {
        StackPane window = new StackPane();
        window.setStyle(
                "-fx-background-color: #3d2b22; " +
                "-fx-background-radius: 4 4 0 0; " +
                "-fx-border-color: #4a3b32; " +
                "-fx-border-width: 2 2 0 2;"
        );
        window.setPrefSize(100, 30);
        window.setMaxWidth(100);
        window.setMaxHeight(30);
        return window;
    }

    private HBox createBoard() {
        HBox inventoryBoard = new HBox();
        inventoryBoard.setAlignment(Pos.CENTER);
        inventoryBoard.setStyle(
                "-fx-background-color: #2a1f1a; " +
                "-fx-border-color: #4a3b32; " +
                "-fx-border-width: 5; " +
                "-fx-background-radius: 4 4 0 0; " +
                "-fx-border-radius: 4 4 0 0;"
        );

        for (int i = 0; i < 5; i++) {
            StackPane cell = new StackPane();
            cell.setPrefSize(90, 90);
            cell.setStyle(
                "-fx-border-color: #4a3b32; " +
                "-fx-border-width: 2;"
            );
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

    public void attachTo(StackPane root) {
        root.getChildren().add(this.container);
    }
}