package ukma.fourgirls.ui.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

public class AnimationCanvas extends Pane{

    private static final double VIRTUAL_WIDTH = 2048;
    private static final double VIRTUAL_HEIGHT = 1152;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final RainSystem rainSystem;
    private final Scale scaleTransform;

    public AnimationCanvas() {
        canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        this.rainSystem = new RainSystem();

        scaleTransform = new Scale(1, 1, 0, 0);
        canvas.getTransforms().add(scaleTransform);
        this.getChildren().add(canvas);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                rainSystem.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

                // Очищаємо Canvas
                gc.clearRect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

                // Малюємо дощ
                gc.setStroke(Color.rgb(150, 175, 190, 0.35));
                for (var p : rainSystem.getRainParticles()) {
                    gc.setLineWidth(p.size);
                    gc.strokeLine(p.x, p.y, p.x + p.vx * 0.7, p.y + p.vy * 0.7);
                }
            }
        };
        timer.start();
    }

    public void setRainActive(boolean active) {
        rainSystem.setRainActive(active);
    }

    // Цей метод автоматично підлаштовує розмір Canvas під поточний розмір вікна
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
