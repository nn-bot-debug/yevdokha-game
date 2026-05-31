package ukma.fourgirls;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class SceneManager {

    private static SceneManager instance;

    private Stage primaryStage;
    private Parent mainMenuRoot;

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

    public double getWidth() { return primaryStage.getWidth(); }
    public double getHeight() { return primaryStage.getHeight(); }
}