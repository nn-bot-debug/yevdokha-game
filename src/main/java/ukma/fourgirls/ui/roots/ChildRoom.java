package ukma.fourgirls.ui.roots;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.InventoryState;
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

    /**
     * Головний метод активації гри.
     */
    public void activateGameplay() {
        Image newBackground = new Image(Objects.requireNonNull(getClass().getResourceAsStream(GAMEPLAY_IMAGE_PATH)));
        this.roomView.setImage(newBackground);
        CameraController.setPanningEnabled(true);

        this.interactiveDrawing = createInteractiveDrawing();

        this.roomContentLayer.getChildren().add(interactiveDrawing);
    }

    /**
     * Окремий метод, який займається виключно геометрією та стилізацією малюнка
     */
    private ImageView createInteractiveDrawing() {
        Image drawingImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/drawing.png")));
        ImageView drawingView = new ImageView(drawingImg);

        drawingView.setFitWidth(160);
        drawingView.setPreserveRatio(true);

        // 3D Трансформація
        Rotate tiltX = new Rotate(-70, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(15, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(-25, Rotate.Z_AXIS);
        drawingView.getTransforms().addAll(tiltX, rotateY, rotateZ);

        drawingView.setTranslateX(-230);
        drawingView.setTranslateY(340);

        ColorAdjust darkenEffect = new ColorAdjust();
        darkenEffect.setBrightness(-0.15);
        darkenEffect.setContrast(-0.1);
        darkenEffect.setSaturation(-0.15);
        drawingView.setEffect(darkenEffect);

        // Логіка наведення миші
        drawingView.setOnMouseEntered(e -> {
            ColorAdjust hoverEffect = new ColorAdjust();
            hoverEffect.setBrightness(-0.05);
            hoverEffect.setContrast(0.0);
            drawingView.setEffect(hoverEffect);
        });
        drawingView.setOnMouseExited(e -> drawingView.setEffect(darkenEffect));

        drawingView.setPickOnBounds(true);
        drawingView.setStyle("-fx-cursor: hand;");

        return drawingView;
    }

    public ImageView getInteractiveDrawing() {
        return interactiveDrawing;
    }

    public void enableNavigation() {
        setupNavigation("ChildRoom");
    }
}