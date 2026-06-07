package ukma.fourgirls.logic;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import ukma.fourgirls.core.InventoryManager;
import ukma.fourgirls.core.NotificationManager;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.core.StatNotification;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.GameState;
import ukma.fourgirls.state.InventoryState;
import ukma.fourgirls.ui.roots.ChildRoom;
import ukma.fourgirls.ui.roots.MomRoom;

import java.util.Objects;

public class StoryController {
    private static Image yevdokhaPortrait = new Image(Objects.requireNonNull(StoryController.class.getResourceAsStream("/images/sadyevdokha.png")));

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
                .addDialogue("Євдоха", yevdokhaPortrait, "Останнім часом мама зовсім засмучена, сподіваюсь мій малюнок підійме їй настрій.")
                .execute(() -> startChildRoomGameplay(childRoom, roomRoot))
                .play();
    }

    private static void startChildRoomGameplay(ChildRoom childRoom, StackPane roomRoot) {
        childRoom.activateGameplay();
        NotificationManager.showNotification(roomRoot, "Завдання: Підніміть малюнок зі столу\nПідказка: щоб підняти річ, натисніть на неї ЛКМ)");

        Item yevdokhaDrawing = new Item("Малюнок", "/images/drawing.png");
        Node drawing = childRoom.getInteractiveDrawing();

        InventoryManager.setupPickupAction(
                drawing,
                yevdokhaDrawing,
                roomRoot,
                "Ви підняли малюнок! Підібрані речі ви можете побачити в інвентарі.",
                () -> onDrawingPickedUp(childRoom, roomRoot)
        );
    }

    private static void onDrawingPickedUp(ChildRoom childRoom, StackPane roomRoot) {
        childRoom.showInventoryUI();

        GameState.unlockLocation("MomRoom");
        childRoom.enableNavigation();

        StorySequence.create(roomRoot)
                .addPause(5.0)
                .addDialogue("Євдоха", yevdokhaPortrait, "Зазвичай мама в такий час вже прокинулась. Піду до неї в кімнату. ")
                .addPause(2.0)
                .execute(() -> NotificationManager.showNotification(roomRoot, "Підказка: Використайте панель навігації праворуч, щоб вийти з кімнати."))
                .play();
    }

    public static void startMomRoomCutscene(MomRoom momRoom, StackPane roomRoot) {
        StorySequence.create(roomRoot)
                .addDialogue("Побачене ніяк не засмутило дівчинку, навпаки, ніби вона все життя тільки цього і чекала, і от нарешті це сталося.")
                .execute(momRoom::showMomView)
                .addAnimation(momRoom.getPart1Animation())
                .execute(momRoom::showDrawingView)
                .addDialogue("Євдоха", yevdokhaPortrait, "Мабуть мама перебрала із чарами. Вона сама казала, це може бути шкідливо... ")
                .addDialogue("Євдоха", yevdokhaPortrait, "І кому ж мені тепер показати малюнок... ")
                .addAnimation(momRoom.getPart2Animation())
                .execute(() -> {
                    momRoom.removeBlackOverlay();

                    GameState.setKarmaListener((currentKarma, addedPoints) -> {
                        StatNotification.show(roomRoot, currentKarma, addedPoints);
                    });

                    ukma.fourgirls.core.ChoiceManager.Option[] options = {
                            new ukma.fourgirls.core.ChoiceManager.Option("Покласти біля мами", () -> {
                                ukma.fourgirls.state.InventoryState.removeItem("Малюнок");
                                GameState.changeKarma(-1);
                                momRoom.hideDrawingView();
                                playScaryMomSequence(momRoom, roomRoot);
                            }),
                            new ukma.fourgirls.core.ChoiceManager.Option("Заховати в кишеню", () -> {
                                GameState.changeKarma(1);
                                momRoom.hideDrawingView();
                                afterMomRoomChoice(momRoom, roomRoot,
                                        "Дівчинка згорнула папірець і сховала його глибоко в кишеню.",
                                        "Ні... Вона все одно не подивиться. Немає сенсу."
                                );
                            })
                    };
                    ukma.fourgirls.core.ChoiceManager.show(roomRoot, "Що робити із малюнком?", options);
                })
                .play();
    }

    private static void playScaryMomSequence(MomRoom momRoom, StackPane roomRoot) {
        Image scaredYevdokha = null;
        Image scaryMomPortrait = null;
        try {
            scaredYevdokha = new Image(Objects.requireNonNull(StoryController.class.getResourceAsStream("/images/yevdokha_scared.png")));
            scaryMomPortrait = new Image(Objects.requireNonNull(StoryController.class.getResourceAsStream("/images/mom_scary_portrait.png")));
        } catch (Exception e) {
            System.err.println("Зображення портретів не знайдені!");
        }

        StorySequence.create(roomRoot)
                .addDialogue(
                        "Дівчинка підійшла до ліжка, де лежала мати. Навіть після смерті виглядала вона сумною та нещасною.",
                        "Намагаючись підійняти її черстві та холодні руки, дівчинка відчула ніби серце в матері ще й досі б'ється."
                )
                .execute(momRoom::showScaryMom)
                .addDialogue("Мати", scaryMomPortrait, "НЕ ЙДИ ДО ЛІСУ!")
                .addDialogue("Євдоха", yevdokhaPortrait, "Ах!")
                .execute(() -> {
                    momRoom.hideScaryMom();
                    momRoom.finalizeCutscene();
                })
                .addDialogue(
                        "Це було останнє попередження.",
                        "Євдоха затримала подих, немов боялася, що мати знову прокинеться. Але цього не сталося.",
                        "Тож маленька просто поклала малюнок біля матері."
                )
                .addDialogue("Євдоха", yevdokhaPortrait, "У животі так бурчить... Треба піти на кухню і пошукати щось поїсти.")
                .execute(() -> {
                    GameState.unlockLocation("Kitchen");
                    momRoom.finalizeCutscene();
                })
                .play();
    }

    private static void afterMomRoomChoice(MomRoom momRoom, StackPane roomRoot, String... dialogueLines) {
        StorySequence.create(roomRoot)
                .addDialogue(dialogueLines)
                .addDialogue("Євдоха", yevdokhaPortrait, "У животі так бурчить... Треба піти на кухню і пошукати щось поїсти.")
                .execute(() -> {
                    GameState.unlockLocation("Kitchen");
                    momRoom.finalizeCutscene();
                    NotificationManager.showNotification(roomRoot, "Нове завдання: Знайдіть їжу на кухні");
                })
                .play();
    }
}