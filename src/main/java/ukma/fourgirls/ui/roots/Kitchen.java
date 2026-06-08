package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.ui.animation.AnimationCanvas;

import java.util.Objects;

public class Kitchen extends Place {
    private static final String IMAGE_PATH = "/images/kitchen.png";
    private ImageView backgroundView;
    private final ImageView interactiveBread;
    private final AnimationCanvas animationCanvas;
    private final Rectangle flashOverlay;

    public Kitchen() {
        super(IMAGE_PATH);

        for (javafx.scene.Node topNode : this.root.getChildren()) {
            if (topNode instanceof ScrollPane sp) {
                if (sp.getContent() instanceof Pane container) {
                    for (javafx.scene.Node innerNode : container.getChildren()) {
                        if (innerNode instanceof ImageView iv) {
                            this.backgroundView = iv;
                            break;
                        }
                    }
                }
            }
        }

        this.animationCanvas = new AnimationCanvas();
        this.roomContentLayer.getChildren().add(animationCanvas);

        this.interactiveBread = createInteractiveBread();
        this.roomContentLayer.getChildren().add(interactiveBread);

        // Спалах блискавки
        this.flashOverlay = new Rectangle();
        this.flashOverlay.widthProperty().bind(this.root.widthProperty());
        this.flashOverlay.heightProperty().bind(this.root.heightProperty());
        this.flashOverlay.setOpacity(0.0);
        this.flashOverlay.setMouseTransparent(true);
        this.root.getChildren().add(flashOverlay);

        setupNavigation("Kitchen");
    }

    /**
     * Вмикає зливу в системі частинок та змінює фон вікна
     */
    public void startStormEffects() {
        animationCanvas.setRainActive(true);
        try {
            Image rainBg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/rain_in_kitchen.png")));

            if (this.backgroundView != null) {
                // Якщо знайшли ImageView всередині ScrollPane — просто оновлюємо картинку
                this.backgroundView.setImage(rainBg);
            }
        } catch (Exception e) {
            System.err.println("Не вдалося оновити фон кухні: " + e.getMessage());
        }
    }

    public void triggerLightningFlash(Runnable onFlashComplete) {
        flashOverlay.setFill(Color.WHITE);
        flashOverlay.setOpacity(0.0);

        FadeTransition strike = new FadeTransition(Duration.millis(80), flashOverlay);
        strike.setToValue(0.8);

        FadeTransition fade = new FadeTransition(Duration.millis(300), flashOverlay);
        fade.setToValue(0.0);

        SequentialTransition seq = new SequentialTransition(strike, fade);
        seq.setOnFinished(e -> onFlashComplete.run());
        seq.play();
    }

    public void fadeToBlackout(Runnable onBlackoutComplete) {
        flashOverlay.setFill(Color.BLACK);

        FadeTransition fade = new FadeTransition(Duration.millis(300), flashOverlay);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setOnFinished(e -> onBlackoutComplete.run());
        fade.play();
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