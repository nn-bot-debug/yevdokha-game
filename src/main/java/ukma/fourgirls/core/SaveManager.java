package ukma.fourgirls.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.state.SaveData;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveGame(GameSession session, String currentRoomId, String currentDialogNodeId) {
        SaveData data = new SaveData();

        data.karmaBalance = session.getKarmaBalance();
        data.unlockedLocations = session.getUnlockedLocations();
        data.inventoryUnlocked = session.isInventoryUnlocked();
        data.momRoomVisited = session.isMomRoomVisited();
        data.kitchenStormFinished = session.isKitchenStormFinished();
        data.childRoomIntroPlayed = session.isChildRoomIntroPlayed();
        data.drawingPickedUp = session.isDrawingPickedUp();
        data.momRoomRatScenePlayed = session.isMomRoomRatScenePlayed();


        data.inventoryItemNames = session.getInventoryItems().stream()
                .map(Item::getName)
                .collect(Collectors.toList());

        data.currentRoomId = currentRoomId;
        data.currentDialogNodeId = currentDialogNodeId;

        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            gson.toJson(data, writer);
            System.out.println("Game successfully saved to " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("Error saving the game: " + e.getMessage());
        }
    }

    public static SaveData loadGame() {
        try (FileReader reader = new FileReader(SAVE_FILE)) {
            return gson.fromJson(reader, SaveData.class);
        } catch (IOException e) {
            System.out.println("Save file not found or an error occurred.");
            return null;
        }
    }
}
