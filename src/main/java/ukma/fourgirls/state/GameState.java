package ukma.fourgirls.state;

import java.util.HashSet;
import java.util.Set;

public class GameState {
    private static final Set<String> unlockedLocations = new HashSet<>();
    private static int karmaBalance = 0;
    private static boolean inventoryUnlocked = false;

    public interface KarmaChangeListener {
        void onKarmaChange(int currentKarma, int addedPoints);
    }

    private static KarmaChangeListener karmaListener;

    public static void setKarmaListener(KarmaChangeListener listener) {
        karmaListener = listener;
    }

    public static void changeKarma(int points) {
        karmaBalance += points;
        System.out.println("Баланс: " + karmaBalance);

        if (karmaListener != null) {
            karmaListener.onKarmaChange(karmaBalance, points);
        }
    }

    public static int getKarmaBalance() {
        return karmaBalance;
    }

    public static void unlockLocation(String locationId) {
        unlockedLocations.add(locationId);
    }

    public static void lockLocation(String locationId) {
        unlockedLocations.remove(locationId);
    }

    public static boolean isUnlocked(String locationId) {
        return unlockedLocations.contains(locationId);
    }

    public static boolean isInventoryUnlocked() {
        return inventoryUnlocked;
    }

    public static void unlockInventory() {
        inventoryUnlocked = true;
    }
}