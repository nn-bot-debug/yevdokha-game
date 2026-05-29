package ukma.fourgirls.domain;

public class Lukyan extends Creature {
    private boolean isFound;

    public Lukyan() {
        super("Лук'ян");
        this.isFound = false;
    }

    public boolean isFound() {
        return isFound;
    }

    public void discover() {
        this.isFound = true;
    }
}