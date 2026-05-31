package ukma.fourgirls.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ukma.fourgirls.SceneManager;

public class ChildRoom {
    private final Scene scene;

    public ChildRoom() {
        VBox instructionsRoot = new VBox(30);
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());
        instructionsRoot.getChildren().add(backButton);
        double width = SceneManager.getInstance().getWidth();
        double height = SceneManager.getInstance().getHeight();
        this.scene = new Scene(instructionsRoot, width, height);
    }

    public Scene getScene() {
        return scene;
    }
}
