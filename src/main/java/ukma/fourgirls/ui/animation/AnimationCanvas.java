package ukma.fourgirls.ui.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import ukma.fourgirls.ui.animation.ParticleSystem;

public class AnimationCanvas extends Pane{

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final ParticleSystem particleSystem;

    public AnimationCanvas() {
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        particleSystem = new ParticleSystem();

        this.getChildren().add(canvas);

        // Запускаємо ігровий цикл анімації
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double w = canvas.getWidth();
                double h = canvas.getHeight();

                if (w <= 0 || h <= 0) return;

                // 1. Оновлюємо логіку частинок
                particleSystem.update(w, h);

                // 2. Очищаємо Canvas (сам фон малювати не треба, він буде ззаду на StackPane)
                gc.clearRect(0, 0, w, h);

                // 3. Малюємо пил у промені світла
                gc.setFill(Color.rgb(155, 168, 158, 0.25)); // Колір підібрано під колір тексту кнопок партнерки
                for (var p : particleSystem.getDustParticles()) {
                    // Пилинки загораються в зоні дверного прорізу (приблизно центр та праворуч)
                    if (p.x > w * 0.45 && p.x < w * 0.85) {
                        gc.fillOval(p.x, p.y, p.size, p.size);
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
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
    }
}
