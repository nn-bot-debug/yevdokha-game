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

        breadView.setFitWidth(180);
        breadView.setPreserveRatio(true);


        breadView.setTranslateX(540);
        breadView.setTranslateY(170);

        ColorAdjust darkenEffect = new ColorAdjust();
        darkenEffect.setBrightness(-0.25);
        darkenEffect.setContrast(0.05);
        darkenEffect.setSaturation(-0.1);
        breadView.setEffect(darkenEffect);

        breadView.setOnMouseEntered(e -> {
            breadView.setEffect(null);
        });
        breadView.setOnMouseExited(e -> breadView.setEffect(darkenEffect));

        breadView.setPickOnBounds(true);
        breadView.setStyle("-fx-cursor: hand;");

        return breadView;
    }

    public ImageView getInteractiveBread() {
        return interactiveBread;
    }
}