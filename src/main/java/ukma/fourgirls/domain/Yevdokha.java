package ukma.fourgirls.domain;

import java.util.ArrayList;
import java.util.List;

public class Yevdokha extends Creature {
    private int sanity;
    private final Inventory inventory;

    public Yevdokha(int initialSanity) {
        super("Євдоха");
        this.sanity = initialSanity;
        this.inventory = new Inventory();
    }

    public void changeSanity(int amount) {
        this.sanity += amount;
    }

    public int getSanity() {
        return sanity;
    }

    public Inventory getInventory() {
        return inventory;
    }

    // --- Вкладений клас ---
    public static class Inventory {
        private final List<String> items;

        private Inventory() {
            this.items = new ArrayList<>();
        }

        public void add(String item) {
            this.items.add(item);
        }

        public boolean remove(String item) {
            return this.items.remove(item);
        }

        public boolean hasItem(String item) {
            return this.items.contains(item);
        }

        public List<String> getItems() {
            return new ArrayList<>(items);
        }
    }
}