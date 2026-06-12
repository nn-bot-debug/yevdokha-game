package ukma.fourgirls.state;

import java.util.List;
import java.util.Set;

public class SaveData {
    public int karmaBalance;
    public Set<String> unlockedLocations;
    public boolean inventoryUnlocked;
    public boolean momRoomVisited;
    public boolean kitchenStormFinished;
    public boolean childRoomIntroPlayed;
    public boolean drawingPickedUp;

    public List<String> inventoryItemNames;

    public String currentRoomId;
    public String currentDialogNodeId;
}