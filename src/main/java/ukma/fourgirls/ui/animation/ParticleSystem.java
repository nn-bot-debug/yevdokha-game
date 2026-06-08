package ukma.fourgirls.ui.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private final List<Particle> dustParticles = new ArrayList<>();
    private final List<Particle> smokeParticles = new ArrayList<>();
    private final Random random = new Random();

    private double CANDLE_X = 1510;
    private double CANDLE_Y = 553;

    public void setCandleLocation(double x, double y) {
        this.CANDLE_X = x;
        this.CANDLE_Y = y;
    }

    public void update(double width, double height) {
        // Керування пилом
        if (dustParticles.size() < 100) {
            // Пил з'являється у випадкових місцях
            generateDust(random.nextDouble() * width, random.nextDouble() * height);
        }
        updateList(dustParticles, width, height);

        // Керування димом від свічки
        if (random.nextDouble() < 0.25) {
            generateSmoke(CANDLE_X, CANDLE_Y);
        }
        updateList(smokeParticles, width, height);
    }

    private void generateDust(double x, double y) {
        double vx = (random.nextDouble() - 0.5) * 0.15;
        double vy = (random.nextDouble() - 0.4) * 0.1;
        double size = random.nextDouble() * 5 + 5;
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