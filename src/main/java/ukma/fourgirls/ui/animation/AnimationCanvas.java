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
    private final ParticleSystem particleSystem;
    private final Scale scaleTransform;

    public AnimationCanvas() {
        canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        particleSystem = new ParticleSystem();

        scaleTransform = new Scale(1, 1, 0, 0);
        canvas.getTransforms().add(scaleTransform);

        this.getChildren().add(canvas);

        // Запускаємо ігровий цикл анімації
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                particleSystem.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

                // Очищаємо Canvas
                gc.clearRect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

                // Малюємо пил у промені світла
                gc.setFill(Color.rgb(178, 141, 148, 0.4));
                for (var p : particleSystem.getDustParticles()) {
                    if (p.x > 1000 && p.x < 1750) {
                        if (p.y > 100 && p.y < 1100) {
                            gc.fillOval(p.x, p.y, p.size, p.size);
                        }
                    }
                }

                // 4. Малюємо дим від свічки
                for (var p : particleSystem.getSmokeParticles()) {
                    gc.setFill(Color.rgb(155, 168, 158, p.opacity * 0.2));
                    double currentSize = p.size * (2.5 - p.opacity); // дим розширюється
                    gc.fillOval(p.x, p.y, currentSize, currentSize);
                }
            }
        };
        timer.start();
    }

    // Цей метод автоматично підлаштовує розмір Canvas під поточний розмір вікна
    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double currentWidth = getWidth();
        double currentHeight = getHeight();

        if (currentWidth <= 0 || currentHeight <= 0) return;

        // Рахуємо коефіцієнти розтягування
        double scaleX = currentWidth / VIRTUAL_WIDTH;
        double scaleY = currentHeight / VIRTUAL_HEIGHT;

        // Вибираємо максимальний коефіцієнт, щоб анімація заповнювала весь екран без деформацій
        double scale = Math.max(scaleX, scaleY);

        scaleTransform.setX(scale);
        scaleTransform.setY(scale);

        // Обчислюємо реальні розміри Canvas після масштабування
        double scaledWidth = VIRTUAL_WIDTH * scale;
        double scaledHeight = VIRTUAL_HEIGHT * scale;

        // Ідеально центруємо Canvas, щоб він збігався з BackgroundPosition.CENTER вашої партнерки
        canvas.setLayoutX((currentWidth - scaledWidth) / 2);
        canvas.setLayoutY((currentHeight - scaledHeight) / 2);
    }
}
