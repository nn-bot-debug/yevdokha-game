package ukma.fourgirls.ui.roots;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import java.util.Objects;

public class Kitchen extends Place {
    private static final String IMAGE_PATH = "/images/kitchen.png";
    private ImageView interactiveBread;

    public Kitchen() {
        super(IMAGE_PATH);
        this.interactiveBread = createInteractiveBread();
        this.roomContentLayer.getChildren().add(interactiveBread);
        setupNavigation("Kitchen");
    }

    /**
     * Геометрія, 3D-нахил та стилізація зацвілого хліба на столі
     */
    private ImageView createInteractiveBread() {
        Image breadImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bread.png")));
        ImageView breadView = new ImageView(breadImg);

        breadView.setFitWidth(220);
        breadView.setPreserveRatio(true);

        Rotate tiltX = new Rotate(-10, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(-35, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(-10, Rotate.Z_AXIS);
        breadView.getTransforms().addAll(tiltX, rotateY, rotateZ);

        breadView.setTranslateX(-1250);
        breadView.setTranslateY(290);

        ColorAdjust brightEffect = new ColorAdjust();
        brightEffect.setBrightness(0.08);
        brightEffect.setContrast(0.1);
        brightEffect.setSaturation(0.05);
        breadView.setEffect(brightEffect);

        breadView.setOnMouseEntered(e -> {
            breadView.setEffect(null);
        });
        breadView.setOnMouseExited(e -> breadView.setEffect(brightEffect));

        breadView.setPickOnBounds(true);
        breadView.setStyle("-fx-cursor: hand;");

        return breadView;
    }

    public ImageView getInteractiveBread() {
        return interactiveBread;
    }
}