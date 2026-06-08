package ukma.fourgirls.ui.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

public class MenuAnimationCanvas extends  Pane{
    private static final double VIRTUAL_WIDTH = 2048;
    private static final double VIRTUAL_HEIGHT = 1152;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final ParticleSystem particleSystem;
    private final Scale scaleTransform;

    public MenuAnimationCanvas() {
        canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        this.particleSystem = new ParticleSystem();

        scaleTransform = new Scale(1, 1, 0, 0);
        canvas.getTransforms().add(scaleTransform);
        this.getChildren().add(canvas);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                particleSystem.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

                gc.clearRect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

                // Пил
                gc.setFill(Color.rgb(178, 141, 148, 0.4));
                for (var p : particleSystem.getDustParticles()) {
                    gc.fillOval(p.x, p.y, p.size, p.size);
                }

                // Дим іфд свічки
                for (var p : particleSystem.getSmokeParticles()) {
                    gc.setFill(Color.rgb(155, 168, 158, p.opacity * 0.2));
                    double currentSize = p.size * (2.5 - p.opacity);
                    gc.fillOval(p.x, p.y, currentSize, currentSize);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double currentWidth = getWidth();
        double currentHeight = getHeight();
        if (currentWidth <= 0 || currentHeight <= 0) return;

        double scaleX = currentWidth / VIRTUAL_WIDTH;
        double scaleY = currentHeight / VIRTUAL_HEIGHT;
        double scale = Math.max(scaleX, scaleY);

        scaleTransform.setX(scale);
        scaleTransform.setY(scale);

        canvas.setLayoutX((currentWidth - scale * VIRTUAL_WIDTH) / 2);
        canvas.setLayoutY((currentHeight - scale * VIRTUAL_HEIGHT) / 2);
    }
}
