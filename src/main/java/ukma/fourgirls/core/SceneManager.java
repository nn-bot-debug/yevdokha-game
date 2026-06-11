package ukma.fourgirls.core;

import javafx.scene.Parent;
import javafx.stage.Stage;
import ukma.fourgirls.ui.roots.Place;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SceneManager {

    private Stage primaryStage;
    private Parent mainMenuRoot;

    private final Map<String, Place> cachedRooms = new HashMap<>();

    private SceneManager() {}

    public static SceneManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final SceneManager INSTANCE = new SceneManager();
    }

    public void init(Stage stage) {
        if (stage == null) {
            System.err.println("Error: Stage cannot be null during SceneManager initialization!");
        }
        this.primaryStage = stage;
    }

    public void setMainMenuRoot(Parent root) {
        this.mainMenuRoot = root;
    }

    // --- МЕТОДИ ПЕРЕМИКАННЯ ЕКРАНІВ ---

    public void switchToMainMenu() {
        if (mainMenuRoot != null) {
            switchToRoot(mainMenuRoot);
        } else {
            System.err.println("Error: Main menu is not set! Please call setMainMenuRoot() first.");
        }
    }

    public void switchToRoot(Parent newRoot) {
        if (primaryStage != null && primaryStage.getScene() != null) {
            primaryStage.getScene().setRoot(newRoot);
        } else {
            System.err.println("Error: primaryStage or Scene has not been initialized yet!");
        }
    }

    /**
     * @param roomKey Unique identifier for the room (e.g., "MomRoom")
     * @param roomCreator Logic for creating the room if it is not in the cache
     */
    public void switchToCachedRoom(String roomKey, Supplier<Place> roomCreator) {
        if (primaryStage != null && primaryStage.getScene() != null) {
            var room = cachedRooms.computeIfAbsent(roomKey, e -> roomCreator.get());
            primaryStage.getScene().setRoot(room.getRoot());
            room.onEnter();
        } else {
            System.err.println("Error: Cannot switch to room " + roomKey + " without primaryStage!");
        }
    }

    public void clearCache() {
        cachedRooms.clear();
    }

    public double getWidth() {
        return primaryStage != null ? primaryStage.getWidth() : 0;
    }

    public double getHeight() {
        return primaryStage != null ? primaryStage.getHeight() : 0;
    }
}