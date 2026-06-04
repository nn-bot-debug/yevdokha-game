package ukma.fourgirls.logic;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import ukma.fourgirls.core.InventoryManager;
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

        StorySequence.create(roomRoot)
                .addDialogue(
                        "Дівчинка очей не зімкнула, все малювала аж до поки світати не почало.",
                        "Коли закінчила у неї було одне бажання - показати матері, щоб хоч трошки її підбадьорити."
                )
                .execute(() -> startChildRoomGameplay(childRoom, roomRoot))
                .play();
    }

    private static void startChildRoomGameplay(ChildRoom childRoom, StackPane roomRoot) {
        childRoom.activateGameplay();
        NotificationManager.showNotification(roomRoot, "Завдання: Підніміть малюнок зі столу.");

        Item yevdokhaDrawing = new Item("Малюнок", "/images/drawing_icon.png");
        Node drawing = childRoom.getInteractiveDrawing();

        InventoryManager.setupPickupAction(
                drawing,
                yevdokhaDrawing,
                roomRoot,
                "Ви підняли малюнок! Тепер покажіть його матері.",
                () -> onDrawingPickedUp(childRoom, roomRoot)
        );
    }

    private static void onDrawingPickedUp(ChildRoom childRoom, StackPane roomRoot) {
        GameState.unlockLocation("MomRoom");
        childRoom.enableNavigation();

        StorySequence.create(roomRoot)
                .addPause(5.0)
                .execute(() -> NotificationManager.showNotification(roomRoot, "Підказка: Використайте панель навігації праворуч, щоб вийти з кімнати."))
                .play();
    }
}