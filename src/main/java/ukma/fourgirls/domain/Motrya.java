package ukma.fourgirls.domain;

public class Motrya extends Creature {
    private double lifeEnergy;
    private final boolean isAddicted;

    public Motrya(double initialEnergy, boolean isAddicted) {
        super("Мотря");
        this.lifeEnergy = initialEnergy;
        this.isAddicted = isAddicted;
    }

    public void singLullaby() {
        System.out.println(getName() + " співає колиску для своєї дівчинки...");
    }

    public void decreaseEnergy(double amount) {
        this.lifeEnergy -= amount;
    }

    public double getLifeEnergy() { return lifeEnergy; }
    public boolean isAddicted() { return isAddicted; }
}