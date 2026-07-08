package ukma.fourgirls.domain.model;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {
    private static final Map<String, Item> ALL_ITEMS = new HashMap<>();

    static {
        registerItem(new Item("Малюнок", "/images/objects/drawing.png"));
        registerItem(new Item("Ключ від дверей", "/images/objects/key.png"));
        registerItem(new Item("Брошка", "/images/objects/brooch.png"));
        registerItem(new Item("Зацвілий хліб", "/images/objects/bread.png"));
        registerItem(new Item("Порожній горщик", "/images/objects/empty_pot.png"));
        registerItem(new Item("Горщик зі смолою", "/images/objects/full_pot.png"));
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