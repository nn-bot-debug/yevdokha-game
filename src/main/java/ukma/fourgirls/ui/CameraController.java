package ukma.fourgirls.ui;

import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import ukma.fourgirls.core.SceneManager;

public class CameraController {

    private static boolean isPanningEnabled = true;

    /**
     * Додає ефект повороту голови (панорами) за курсором миші.
     *
     * @param interactiveNode Контейнер, який відловлює рух миші (наприклад, VBox кімнати)
     * @param scrollPane      Скрол-панель із зображенням, яку потрібно рухати
     */
    public static void enableMousePanning(Parent interactiveNode, ScrollPane scrollPane) {
        interactiveNode.setOnMouseMoved(event -> {
            if (!isPanningEnabled) {
                return;
            }

            double mouseX = event.getSceneX();
            double sceneWidth = SceneManager.getInstance().getWidth();

            if (sceneWidth > 0) {
                double scrollValue = mouseX / sceneWidth;
                scrollPane.setHvalue(scrollValue);
            }
        });
    }
    public static void setPanningEnabled(boolean enabled) {
        isPanningEnabled = enabled;
    }
}