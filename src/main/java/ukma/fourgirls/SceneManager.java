package ukma.fourgirls;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static SceneManager instance;

    private Stage primaryStage;
    private Scene mainMenuScene;

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

    public void setMainMenuScene(Scene scene) {
        this.mainMenuScene = scene;
    }

    // --- МЕТОДИ ПЕРЕМИКАННЯ СЦЕН ---

    public void switchToMainMenu() {
        if (primaryStage != null && mainMenuScene != null) {
            primaryStage.setScene(mainMenuScene);
        }
    }

    public void switchToScene(Scene newScene) {
        if (primaryStage != null) {
            primaryStage.setScene(newScene);
        }
    }

    public double getWidth() { return primaryStage.getWidth(); }
    public double getHeight() { return primaryStage.getHeight(); }
}