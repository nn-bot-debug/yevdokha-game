package ukma.fourgirls.ui.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RainSystem {
    private final List<Particle> rainParticles = new ArrayList<>();
    private final Random random = new Random();
    private boolean isRainActive = false;

    public void update(double width, double height) {
        if (isRainActive) {
            // Густота зливи
            for (int i = 0; i < 8; i++) {
                generateRain();
            }
        }

        Iterator<Particle> it = rainParticles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.x < 0 || p.x > width || p.y > height) {
                it.remove();
            }
        }
    }

    private void generateRain() {
        double x = 1180 + random.nextDouble() * 150;
        double y = 380 + random.nextDouble() * 320;

        double vx = -5.5 - random.nextDouble() * 1.5;
        double vy = 8.0 + random.nextDouble() * 3.0;
        double size = random.nextDouble() * 1.0 + 1;

        rainParticles.add(new Particle(x, y, vx, vy, size, -1));
    }

    public void setRainActive(boolean active) {
        this.isRainActive = active;
    }

    public List<Particle> getRainParticles() {
        return rainParticles;
    }
}
