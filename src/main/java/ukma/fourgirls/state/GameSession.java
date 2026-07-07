package ukma.fourgirls.state;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ukma.fourgirls.domain.model.Item;

import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private final Set<String> unlockedLocations = new HashSet<>();
    private final ObservableList<Item> inventoryItems = FXCollections.observableArrayList();

    private int karmaBalance;
    private boolean inventoryUnlocked;
    private boolean momRoomVisited;
    private boolean kitchenStormFinished;
    private boolean childRoomIntroPlayed;
    private boolean drawingPickedUp;
    private boolean cutsceneActive;
    private boolean momRoomRatScenePlayed;
    private String activeSceneId = "";
    private KarmaChangeListener karmaListener;

    public GameSession() {
        reset();
    }

    public interface KarmaChangeListener {
        void onKarmaChange(int currentKarma, int addedPoints);
    }

    public void reset() {
        unlockedLocations.clear();
        unlockedLocations.add("ChildRoom");
        inventoryItems.clear();
        karmaBalance = 0;
        inventoryUnlocked = false;
        momRoomVisited = false;
        kitchenStormFinished = false;
        childRoomIntroPlayed = false;
        drawingPickedUp = false;
        cutsceneActive = false;
        momRoomRatScenePlayed = false;
        activeSceneId = "";
        karmaListener = null;
    }

    public void loadFrom(SaveData data) {
        reset();
        if (data == null) {
            return;
        }

        karmaBalance = data.karmaBalance;
        activeSceneId = data.currentDialogNodeId != null ? data.currentDialogNodeId : "";
        momRoomVisited = data.momRoomVisited;
        kitchenStormFinished = data.kitchenStormFinished;
        childRoomIntroPlayed = data.childRoomIntroPlayed;
        drawingPickedUp = data.drawingPickedUp;
        momRoomRatScenePlayed = data.momRoomRatScenePlayed;
        inventoryUnlocked = data.inventoryUnlocked;

        if (data.unlockedLocations != null) {
            unlockedLocations.clear();
            unlockedLocations.addAll(data.unlockedLocations);
        }

        if (data.inventoryItemNames != null) {
            for (String itemName : data.inventoryItemNames) {
                Item restoredItem = ItemRegistry.getItemByName(itemName);
                if (restoredItem != null) {
                    addItem(restoredItem);
                }
            }
        }
    }

    public Set<String> getUnlockedLocations() {
        return new HashSet<>(unlockedLocations);
    }

    public boolean isUnlocked(String locationId) {
        return unlockedLocations.contains(locationId);
    }

    public void unlockLocation(String locationId) {
        unlockedLocations.add(locationId);
    }

    public void lockLocation(String locationId) {
        unlockedLocations.remove(locationId);
    }

    public ObservableList<Item> getInventoryItems() {
        return inventoryItems;
    }

    public void addItem(Item item) {
        boolean alreadyExists = inventoryItems.stream()
                .anyMatch(existing -> existing.getName().equals(item.getName()));

        if (!alreadyExists) {
            inventoryItems.add(item);
        }
    }

    public void removeItem(String itemName) {
        inventoryItems.removeIf(item -> item.getName().equals(itemName));
    }

    public boolean hasItem(String itemName) {
        return inventoryItems.stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(itemName));
    }

    public int getKarmaBalance() {
        return karmaBalance;
    }

    public void changeKarma(int points) {
        karmaBalance += points;
        System.out.println("Balance: " + karmaBalance);

        if (karmaListener != null) {
            karmaListener.onKarmaChange(karmaBalance, points);
        }
    }

    public void setKarmaListener(KarmaChangeListener karmaListener) {
        this.karmaListener = karmaListener;
    }

    public boolean isInventoryUnlocked() {
        return inventoryUnlocked;
    }

    public void unlockInventory() {
        inventoryUnlocked = true;
    }

    public boolean isMomRoomVisited() {
        return momRoomVisited;
    }

    public void setMomRoomVisited(boolean momRoomVisited) {
        this.momRoomVisited = momRoomVisited;
    }

    public boolean isKitchenStormFinished() {
        return kitchenStormFinished;
    }

    public void setKitchenStormFinished(boolean kitchenStormFinished) {
        this.kitchenStormFinished = kitchenStormFinished;
    }

    public boolean isChildRoomIntroPlayed() {
        return childRoomIntroPlayed;
    }

    public void setChildRoomIntroPlayed(boolean childRoomIntroPlayed) {
        this.childRoomIntroPlayed = childRoomIntroPlayed;
    }

    public boolean isDrawingPickedUp() {
        return drawingPickedUp;
    }

    public void setDrawingPickedUp(boolean drawingPickedUp) {
        this.drawingPickedUp = drawingPickedUp;
    }

    public boolean isCutsceneActive() {
        return cutsceneActive;
    }

    public void setCutsceneActive(boolean cutsceneActive) {
        this.cutsceneActive = cutsceneActive;
    }

    public boolean isMomRoomRatScenePlayed() {
        return momRoomRatScenePlayed;
    }

    public void setMomRoomRatScenePlayed(boolean momRoomRatScenePlayed) {
        this.momRoomRatScenePlayed = momRoomRatScenePlayed;
    }

    public String getActiveSceneId() {
        return activeSceneId;
    }

    public void setActiveSceneId(String activeSceneId) {
        this.activeSceneId = activeSceneId != null ? activeSceneId : "";
    }
}
