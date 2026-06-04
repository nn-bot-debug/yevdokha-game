package ukma.fourgirls.state;

import java.util.HashSet;
import java.util.Set;

public class GameState {
    private static final Set<String> unlockedLocations = new HashSet<>();

    public static void unlockLocation(String locationId) {
        unlockedLocations.add(locationId);
    }

    public static void lockLocation(String locationId) {
        unlockedLocations.remove(locationId);
    }

    public static boolean isUnlocked(String locationId) {
        return unlockedLocations.contains(locationId);
    }
}