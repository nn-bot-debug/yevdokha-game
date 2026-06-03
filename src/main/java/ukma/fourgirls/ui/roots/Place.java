package ukma.fourgirls.ui.roots;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import ukma.fourgirls.SceneManager;
import ukma.fourgirls.ui.CameraController;

import java.util.Objects;

public abstract class Place {
    protected final StackPane root;
    protected final StackPane roomContentLayer;     // Шари всередині скролу (Фон + предмети)
    protected final ImageView roomView;
    private Font font;

    public Place(String imagePath) {
        StackPane rootPane = new StackPane();

        try {
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 22);
        } catch (Exception e) {
            font = Font.font("Arial", 24);
        }

        this.roomView = setupRoomImage(imagePath);

        this.roomContentLayer = new StackPane(roomView);
        this.roomContentLayer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(roomContentLayer);
        roomView.fitHeightProperty().bind(rootPane.heightProperty());
        roomContentLayer.maxHeightProperty().bind(rootPane.heightProperty());

        scrollPane.setPannable(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Button backButton = createBackButton();
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(14, 0, 0, 14));

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
        Button backButton = new Button("Back to Menu");
        backButton.setFont(font);

        String BackBtnImage = String.valueOf(Objects.requireNonNull(getClass().getResource("/images/buttonBackground.jpeg")));
        String btnStyle =
                "-fx-background-image: url('" + BackBtnImage + "'); "+
                        "-fx-text-fill: #a4bfa7; " +
                        "-fx-border-color: #2e261b;" +
                        "-fx-border-width: 1px;" +
                        "-fx-background-size: cover;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-border-radius: 6px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(60, 80, 80, 0.8), 15, 0.0, 0,4);" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10px 30px;";

        backButton.setStyle(btnStyle);

        backButton.setOnMouseEntered(e -> {
            backButton.setStyle(
                    btnStyle +
                            "-fx-text-fill: #7b9c7b; " +
                            "-fx-border-width: 3px;" +
                            "-fx-opacity:0.95; "
            );
            backButton.setFont(font);
        });

        backButton.setOnMouseExited(e->{
            backButton.setStyle(btnStyle);
            backButton.setFont(font);
        });

        backButton.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());
        return backButton;
    }

    private ImageView setupRoomImage(String imagePath) {
        Image roomImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(roomImage);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}