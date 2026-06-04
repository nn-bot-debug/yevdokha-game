package ukma.fourgirls.logic;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ukma.fourgirls.core.DialogueManager;
import ukma.fourgirls.core.InventoryManager; // Додали імпорт
import ukma.fourgirls.core.NotificationManager;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.GameState;
import ukma.fourgirls.ui.roots.ChildRoom;

public class StoryController {

    public static void startStory() {
        firstPart();
    }

    private static void firstPart() {
        ChildRoom childRoom = new ChildRoom();
        GameState.unlockLocation("ChildRoom");
        StackPane roomRoot = (StackPane) childRoom.getRoot();
        SceneManager.getInstance().switchToRoot(roomRoot);

        String[] introDialogue = {
                "Дівчинка очей не зімкнула, все малювала аж до поки світати не почало.",
                "Коли закінчила у неї було одне бажання - показати матері, щоб хоч трошки її підбадьорити."
        };

        DialogueManager.getInstance().play(roomRoot, introDialogue, () -> {
            childRoom.activateGameplay();
            NotificationManager.showNotification(roomRoot, "Завдання: Підніміть малюнок зі столу.");
            Item yevdokhaDrawing = new Item("Малюнок", "/images/drawing_icon.png");

            Node drawing = childRoom.getInteractiveDrawing();

            InventoryManager.setupPickupAction(
                    drawing,
                    yevdokhaDrawing,
                    roomRoot,
                    "Ви підняли малюнок! Тепер покажіть його матері.",
                    () -> {
                        GameState.unlockLocation("MomRoom");
                        childRoom.enableNavigation();
                        PauseTransition pause = new PauseTransition(Duration.seconds(5));
                        pause.setOnFinished(event -> {
                            NotificationManager.showNotification(roomRoot, "Підказка: Використайте панель навігації праворуч, щоб вийти з кімнати.");
                        });
                        pause.play();
                    }
            );
        });
    }
}