package ukma.fourgirls.ui.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private final List<Particle> dustParticles = new ArrayList<>();
    private final List<Particle> smokeParticles = new ArrayList<>();
    private final Random random = new Random();

    private static final double CANDLE_X_PCT = 0.68;
    private static final double CANDLE_Y_PCT = 0.5;

    public void update(double width, double height) {
        // 1. Керування пилом
        if (dustParticles.size() < 40) {
            // Пил з'являється у випадкових місцях
            generateDust(random.nextDouble() * width, random.nextDouble() * height);
        }
        updateList(dustParticles, width, height);

        // 2. Керування димом від свічки
        if (random.nextDouble() < 0.25) {
            // Вираховуємо поточні координати свічки відповідно до поточного розміру вікна
            double currentCandleX = width * CANDLE_X_PCT;
            double currentCandleY = height * CANDLE_Y_PCT;
            generateSmoke(currentCandleX, currentCandleY);
        }
        updateList(smokeParticles, width, height);
    }

    private void generateDust(double x, double y) {
        double vx = (random.nextDouble() - 0.5) * 0.15;
        double vy = (random.nextDouble() - 0.4) * 0.1;
        double size = random.nextDouble() * 2 + 1;
        dustParticles.add(new Particle(x, y, vx, vy, size, -1));
    }

    private void generateSmoke(double x, double y) {
        double vx = (random.nextDouble() - 0.4) * 0.2; // легкий протяг вбік
        double vy = -random.nextDouble() * 0.4 - 0.3; // вгору
        double size = random.nextDouble() * 2 + 2;
        double maxLife = random.nextInt(100) + 160;
        smokeParticles.add(new Particle(x, y, vx, vy, size, maxLife));
    }

    private void updateList(List<Particle> particles, double width, double height) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.isDead() || p.x < 0 || p.x > width || p.y < 0 || p.y > height) {
                it.remove();
            }
        }
    }

    public List<Particle> getDustParticles() { return dustParticles; }
    public List<Particle> getSmokeParticles() { return smokeParticles; }
}
