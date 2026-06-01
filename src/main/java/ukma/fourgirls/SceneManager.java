package ukma.fourgirls;

import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SceneManager {

    private static SceneManager instance;

    private Stage primaryStage;
    private Parent mainMenuRoot;

    private final Map<String, Parent> cachedRooms = new HashMap<>();

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
    public void switchToCachedRoom(String roomKey, Supplier<Parent> roomCreator) {
        if (primaryStage != null && primaryStage.getScene() != null) {
            Parent roomRoot = cachedRooms.computeIfAbsent(roomKey, k -> roomCreator.get());
            primaryStage.getScene().setRoot(roomRoot);
        }
    }

    public void clearCache() {
        cachedRooms.clear();
    }

    public double getWidth() { return primaryStage.getWidth(); }
    public double getHeight() { return primaryStage.getHeight(); }
}