package ukma.fourgirls.ui.roots;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ukma.fourgirls.SceneManager;
import ukma.fourgirls.ui.CameraController;

import java.util.Objects;

public class ChildRoom {
    private final Parent root;
    private static final String PLACE = "/room.png";

    public ChildRoom() {
        VBox container = new VBox(10);

        ImageView roomView = setupRoomImage();
        ScrollPane scrollPane = new ScrollPane(roomView);
        roomView.fitHeightProperty().bind(container.heightProperty());

        scrollPane.setPannable(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        container.getChildren().addAll(createBackButton(), scrollPane);

        // === ПІДКЛЮЧАЄМО МЕХАНІКУ З ОКРЕМОГО КЛАСУ ===
        CameraController.enableMousePanning(container, scrollPane);
        javafx.application.Platform.runLater(() -> scrollPane.setHvalue(0.5));
        this.root = container;
    }

    public Parent getRoot() {
        return root;
    }

    private Button createBackButton() {
        Button button = new Button("Back to Menu");
        button.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());
        return button;
    }

    private ImageView setupRoomImage() {
        Image roomImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(PLACE)));
        ImageView imageView = new ImageView(roomImage);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}