package ukma.fourgirls.ui.animation;

public class Particle {

    public double x, y;
    public double vx, vy;
    public double size;
    public double opacity;
    public double life;
    public double maxLife;

    public Particle(double x, double y, double vx, double vy, double size, double maxLife) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.maxLife = maxLife;
        this.life = maxLife;
        this.opacity = 1.0;
    }

    public void update() {
        x += vx;
        y += vy;
        if (maxLife > 0) {
            life--;
            opacity = life / maxLife; // Згасання диму
        }
    }

    public boolean isDead() {
        return maxLife > 0 && life <= 0;
    }
}
