package ukma.fourgirls.ui.roots;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import ukma.fourgirls.SceneManager;
import ukma.fourgirls.ui.CameraController;

import java.util.Objects;

public abstract class Place {
    private final Parent root;

    public Place(String imagePath) {
        StackPane rootPane = new StackPane();

        ImageView roomView = setupRoomImage(imagePath);
        ScrollPane scrollPane = new ScrollPane(roomView);

        roomView.fitHeightProperty().bind(rootPane.heightProperty());

        scrollPane.setPannable(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Button backButton = createBackButton();

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));

        rootPane.getChildren().addAll(scrollPane, backButton);

        // === ПІДКЛЮЧАЄМО МЕХАНІКУ З ОКРЕМОГО КЛАСУ ===
        CameraController.enableMousePanning(rootPane, scrollPane);
        javafx.application.Platform.runLater(() -> scrollPane.setHvalue(0.5));

        this.root = rootPane;
    }

    public Parent getRoot() {
        return root;
    }

    private Button createBackButton() {
        Button button = new Button("Back to Menu");
        button.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());
        return button;
    }

    private ImageView setupRoomImage(String imagePath) {
        Image roomImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(roomImage);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}