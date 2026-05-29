package ukma.fourgirls.domain;

import java.util.ArrayList;
import java.util.List;

public class Yevdokha extends Creature {
    private final int age = 5;
    private int sanity;
    private final List<String> inventory;

    public Yevdokha(int initialSanity) {
        super("Євдоха");
        this.sanity = initialSanity;
        this.inventory = new ArrayList<>();
    }

    public void collectItem(String item) {
        this.inventory.add(item);
    }

    public void changeSanity(int amount) {
        this.sanity += amount;
    }

    public int getAge() { return age; }

    public int getSanity() { return sanity; }

    public List<String> getInventory() {
        return new ArrayList<>(inventory);
    }
}