package ukma.fourgirls.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ukma.fourgirls.SceneManager;

public class SettingsScreen {

    private final Scene scene;

    public SettingsScreen() {
        VBox settingsRoot = new VBox(30);
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());
        settingsRoot.getChildren().add(backButton);
        double width = SceneManager.getInstance().getWidth();
        double height = SceneManager.getInstance().getHeight();
        this.scene = new Scene(settingsRoot, width, height);
    }

    public Scene getScene() {
        return scene;
    }
}
