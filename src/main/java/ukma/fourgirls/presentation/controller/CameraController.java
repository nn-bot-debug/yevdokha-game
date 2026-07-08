package ukma.fourgirls.presentation.controller;

import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import ukma.fourgirls.GameContext;

public class CameraController {

    private boolean isPanningEnabled = true;
    private final GameContext context;

    public CameraController(GameContext context) {
        this.context = context;
    }

    /**
     * Додає ефект повороту голови (панорами) за курсором миші.
     *
     * @param interactiveNode Контейнер, який відловлює рух миші (наприклад, VBox кімнати)
     * @param scrollPane      Скрол-панель із зображенням, яку потрібно рухати
     */
    public void enableMousePanning(Parent interactiveNode, ScrollPane scrollPane) {
        interactiveNode.setOnMouseMoved(event -> {
            if (!isPanningEnabled) {
                return;
            }

            double mouseX = event.getSceneX();
            double sceneWidth = context.getScene().getWidth();

            if (sceneWidth > 0) {
                double scrollValue = mouseX / sceneWidth;
                scrollPane.setHvalue(scrollValue);
            }
        });
    }
    public void setPanningEnabled(boolean enabled) {
        isPanningEnabled = enabled;
    }
}