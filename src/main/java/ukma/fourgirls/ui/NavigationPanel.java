package ukma.fourgirls.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class NavigationPanel {
    private final VBox container;

    public NavigationPanel() {
        this.container = new VBox();
        this.container.setAlignment(Pos.TOP_CENTER);
        this.container.setSpacing(5);

        String containerStyle =
                "-fx-background-color: rgba(15, 20, 18, 0.85);" +
                        "-fx-border-color: #3b4238;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-radius: 3px;" +
                        "-fx-border-radius: 3px;" +
                        "-fx-padding: 15px 25px 15px 25px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0, 0, 5);";

        this.container.setStyle(containerStyle);
        this.container.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
    }

    public void addNavigationTarget(String label, Runnable onClickAction) {
        Button button = new Button(label);
        button.setMaxWidth(Double.MAX_VALUE);

        // === СТИЛЬ КНОПОК ===
        String baseStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #a8a08d;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 8px 10px;" +
                        "-fx-border-color: transparent transparent #2a3028 transparent;" +
                        "-fx-border-width: 1px;" +
                        "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 8px 10px;" +
                        "-fx-border-color: transparent transparent #515c4d transparent;" +
                        "-fx-border-width: 1px;" +
                        "-fx-cursor: hand;";

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnAction(e -> onClickAction.run());

        container.getChildren().add(button);
    }

    public void attachTo(StackPane rootPane) {
        StackPane.setAlignment(container, Pos.TOP_RIGHT);
        StackPane.setMargin(container, new Insets(10, 10, 0, 0));
        rootPane.getChildren().add(container);
    }

    public void detachFrom(StackPane rootPane) {
        rootPane.getChildren().remove(this.container);
    }
}