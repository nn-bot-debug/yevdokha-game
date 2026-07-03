package ukma.fourgirls.core;

import ukma.fourgirls.ui.roots.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class LocationRegistry {
    private final Map<String, Location> LOCATIONS = new LinkedHashMap<>();
    private final GameContext context;

    public LocationRegistry(GameContext context) {
        this.context = context;
        register("MomRoom", "Кімната матері", () -> new MomRoom(context), true);
        register("Kitchen", "Кухня", () -> new Kitchen(context), true);
        register("ChildRoom", "Дитяча кімната", () -> new ChildRoom(context), true);
        register("Corridor", "Коридор", () -> new Corridor(context), true);
        register("Yard", "Подвір'я", () -> new Yard(context), true);
        register("Forest", "Ліс", () -> new Forest(context), false);
        register("Tree", "Дерево", () -> new Tree(context), false);
        register("DeeperForest", "Глиб лісу", () -> new DeeperForest(context), false);
        register("Lake", "Озеро з мавками", () -> new Lake(context), false);
    }

    private void register(String id, String displayName, Supplier<Place> roomCreator, boolean visibleInNavigation) {
        LOCATIONS.put(id, new Location(id, displayName, roomCreator, visibleInNavigation));
    }

    public Optional<Location> find(String id) {
        return Optional.ofNullable(LOCATIONS.get(id));
    }

    public Collection<Location> navigationLocations() {
        return LOCATIONS.values().stream()
                .filter(Location::isVisibleInNavigation)
                .toList();
    }

    public boolean switchTo(String id) {
        Optional<Location> location = find(id);
        location.ifPresent(value ->
                context.getScene().switchToCachedRoom(value.getId(), value.getRoomCreator())
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
