package ukma.fourgirls.state;

import ukma.fourgirls.domain.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {
    private static final Map<String, Item> ALL_ITEMS = new HashMap<>();

    static {
        registerItem(new Item("Малюнок", "/images/drawing.png"));
        registerItem(new Item("Ключ від дверей", "/images/key.png"));
        registerItem(new Item("Брошка", "/images/brooch.png"));
        registerItem(new Item("Зацвілий хліб", "/images/bread.png"));
        registerItem(new Item("Порожній горщик", "/images/empty_pot.png"));
        registerItem(new Item("Горщик зі смолою", "/images/full_pot.png"));
    }

    private static void registerItem(Item item) {
        ALL_ITEMS.put(item.getName(), item);
    }

    public static Item getItemByName(String name) {
        Item item = ALL_ITEMS.get(name);
        if (item == null) {
            System.err.println("Error: Item with name '" + name + "' not found in ItemRegistry!");
        }
        return item;
    }
}