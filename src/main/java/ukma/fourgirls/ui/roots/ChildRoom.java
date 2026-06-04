package ukma.fourgirls.ui.roots;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ukma.fourgirls.ui.CameraController;
import java.util.Objects;

public class ChildRoom extends Place {
    private static final String INTRO_IMAGE_PATH = "/images/Yevdokha_drawing.png";
    private static final String GAMEPLAY_IMAGE_PATH = "/images/Yevdokha_room.png";
    private ImageView interactiveDrawing;

    public ChildRoom() {
        super(INTRO_IMAGE_PATH);
        CameraController.setPanningEnabled(false);
    }

    public void activateGameplay() {
        Image newBackground = new Image(Objects.requireNonNull(getClass().getResourceAsStream(GAMEPLAY_IMAGE_PATH)));
        this.roomView.setImage(newBackground);
        CameraController.setPanningEnabled(true);

        Image drawingImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/drawing.png")));
        interactiveDrawing = new ImageView(drawingImg);
        interactiveDrawing.setFitWidth(70);
        interactiveDrawing.setPreserveRatio(true);
        interactiveDrawing.setRotate(-25);
        interactiveDrawing.setTranslateX(-160);
        interactiveDrawing.setTranslateY(195);
        interactiveDrawing.setPickOnBounds(true);
        interactiveDrawing.setStyle("-fx-cursor: hand;");

        this.roomContentLayer.getChildren().add(interactiveDrawing);
    }

    public ImageView getInteractiveDrawing() {
        return interactiveDrawing;
    }

    public void enableNavigation() {
        setupNavigation("ChildRoom");
    }
}