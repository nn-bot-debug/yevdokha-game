package ukma.fourgirls.ui;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ukma.fourgirls.SceneManager;

public class SettingsScreen {
    private final Parent root;

    public SettingsScreen() {
        VBox settingsRoot = new VBox(30);

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());

        settingsRoot.getChildren().add(backButton);

        this.root = settingsRoot;
    }

    public Parent getRoot() {
        return root;
    }
}