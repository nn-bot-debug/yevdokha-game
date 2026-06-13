package ukma.fourgirls.state;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ukma.fourgirls.domain.Item;

public class InventoryState {

    private static final ObservableList<Item> items = FXCollections.observableArrayList();

    public static void addItem(Item item) {
        boolean alreadyExists = items.stream()
                .anyMatch(existing -> existing.getName().equals(item.getName()));

        if (!alreadyExists) {
            items.add(item);
        }
    }

    public static boolean hasItem(String itemName) {
        return items.stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(itemName));
    }

    public static void removeItem(String itemName) {
        items.removeIf(item -> item.getName().equals(itemName));
    }

    public static ObservableList<Item> getItems() {
        return items;
    }

    public static void reset() {
        items.clear();
    }
}