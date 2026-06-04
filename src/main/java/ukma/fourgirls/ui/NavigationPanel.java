package ukma.fourgirls.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class NavigationPanel {
    private final VBox container;

    public NavigationPanel() {
        this.container = new VBox();
        this.container.setAlignment(Pos.TOP_CENTER);
        this.container.setSpacing(5);

        this.container.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/navigation.css")).toExternalForm());
        this.container.getStyleClass().add("nav-panel");

        this.container.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
    }

    public void addNavigationTarget(String label, Runnable onClickAction) {
        Button button = new Button(label);
        button.setMaxWidth(Double.MAX_VALUE);

        button.getStyleClass().add("nav-button");

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