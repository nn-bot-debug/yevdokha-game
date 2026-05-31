package ukma.fourgirls.ui;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ukma.fourgirls.SceneManager;

public class InstructionsScreen {

    private final Parent root;

    public InstructionsScreen() {
        VBox instructionsRoot = new VBox(30);

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());

        instructionsRoot.getChildren().add(backButton);

        this.root = instructionsRoot;
    }

    public Parent getRoot() {
        return root;
    }
}