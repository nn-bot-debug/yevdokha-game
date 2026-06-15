package ukma.fourgirls.core;

import ukma.fourgirls.ui.roots.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class LocationRegistry {
    private static final Map<String, Location> LOCATIONS = new LinkedHashMap<>();

    static {
        register("MomRoom", "Кімната матері", MomRoom::new, true);
        register("Kitchen", "Кухня", Kitchen::new, true);
        register("ChildRoom", "Дитяча кімната", ChildRoom::new, true);
        register("Corridor", "Коридор", Corridor::new, true);
        register("Yard", "Подвір'я", Yard::new, true);
        register("Forest", "Ліс", Forest::new, false);
        register("Tree", "Дерево", Tree::new, false);
        register("DeeperForest", "Глиб лісу", DeeperForest::new, false);
    }

    private LocationRegistry() {}

    private static void register(String id, String displayName, Supplier<Place> roomCreator, boolean visibleInNavigation) {
        LOCATIONS.put(id, new Location(id, displayName, roomCreator, visibleInNavigation));
    }

    public static Optional<Location> find(String id) {
        return Optional.ofNullable(LOCATIONS.get(id));
    }

    public static Collection<Location> navigationLocations() {
        return LOCATIONS.values().stream()
                .filter(Location::isVisibleInNavigation)
                .toList();
    }

    public static boolean switchTo(String id) {
        Optional<Location> location = find(id);
        location.ifPresent(value ->
                SceneManager.getInstance().switchToCachedRoom(value.getId(), value.getRoomCreator())
        );
        return location.isPresent();
    }

    public static final class Location {
        private final String id;
        private final String displayName;
        private final Supplier<Place> roomCreator;
        private final boolean visibleInNavigation;

        private Location(String id, String displayName, Supplier<Place> roomCreator, boolean visibleInNavigation) {
            this.id = id;
            this.displayName = displayName;
            this.roomCreator = roomCreator;
            this.visibleInNavigation = visibleInNavigation;
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Supplier<Place> getRoomCreator() {
            return roomCreator;
        }

        public boolean isVisibleInNavigation() {
            return visibleInNavigation;
        }
    }
}
