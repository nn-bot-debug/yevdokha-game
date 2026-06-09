package ukma.fourgirls.core;

import javafx.scene.Parent;
import javafx.stage.Stage;
import ukma.fourgirls.ui.roots.Place;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SceneManager {

    private static SceneManager instance;

    private Stage primaryStage;
    private Parent mainMenuRoot;

    private final Map<String, Place> cachedRooms = new HashMap<>();

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    public void setMainMenuRoot(Parent root) {
        this.mainMenuRoot = root;
    }

    // --- МЕТОДИ ПЕРЕМИКАННЯ ЕКРАНІВ ---

    public void switchToMainMenu() {
        if (primaryStage != null && primaryStage.getScene() != null && mainMenuRoot != null) {
            primaryStage.getScene().setRoot(mainMenuRoot);
        }
    }

    public void switchToRoot(Parent newRoot) {
        if (primaryStage != null && primaryStage.getScene() != null) {
            primaryStage.getScene().setRoot(newRoot);
        }
    }

    /**
     * @param roomKey Унікальний ідентифікатор кімнати (наприклад, "MomRoom")
     * @param roomCreator Логіка створення кімнати, якщо її немає в кеші
     */
    public void switchToCachedRoom(String roomKey, Supplier<Place> roomCreator) {
        if (primaryStage != null && primaryStage.getScene() != null) {
            Place room = cachedRooms.computeIfAbsent(roomKey, k -> roomCreator.get());
            primaryStage.getScene().setRoot(room.getRoot());
            room.onEnter();
        }
    }

    public void clearCache() {
        cachedRooms.clear();
    }

    public double getWidth() { return primaryStage.getWidth(); }
    public double getHeight() { return primaryStage.getHeight(); }
}