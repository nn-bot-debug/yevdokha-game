package ukma.fourgirls.ui.roots;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.state.GameState;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.NavigationPanel;

import java.util.Objects;

public abstract class Place {
    protected final StackPane root;
    protected final StackPane roomContentLayer;
    protected final ImageView roomView;
    private Font font;
    protected final Inventory inventory;

    public Place(String imagePath) {
        StackPane rootPane = new StackPane();
        rootPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm());

        try {
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 22);
        } catch (Exception e) {
            font = Font.font("Arial", 24);
        }

        this.roomView = setupRoomImage(imagePath);

        this.roomContentLayer = new StackPane(roomView);
        this.roomContentLayer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(roomContentLayer);
        roomView.fitHeightProperty().bind(rootPane.heightProperty());
        roomContentLayer.maxHeightProperty().bind(rootPane.heightProperty());

        scrollPane.setPannable(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Button backButton = createBackButton();
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(14, 0, 0, 14));

        rootPane.getChildren().addAll(scrollPane, backButton);

        this.inventory = new Inventory();
        this.inventory.attachTo(rootPane);
        this.inventory.setVisible(GameState.isInventoryUnlocked());

        CameraController.enableMousePanning(rootPane, scrollPane);
        javafx.application.Platform.runLater(() -> scrollPane.setHvalue(0.5));

        this.root = rootPane;
    }


    public void setBackground(String imagePath) {
        try {
            var stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                this.roomView.setImage(new Image(stream));
            } else {
                System.err.println("Помилка: Файл фону не знайдено за шляхом: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити новий фон: " + e.getMessage());
        }
    }

    public void onEnter() {}

    public Parent getRoot() {
        return root;
    }

    private Button createBackButton() {
        Button backButton = new Button("Back to Menu");
        backButton.setFont(font);
        backButton.getStyleClass().add("back-button");

        backButton.setOnAction(e -> {
            if (ukma.fourgirls.state.GameState.isCutsceneActive) {
                AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
                ukma.fourgirls.core.NotificationManager.showNotification(
                        this.root,
                        "Ви не можете вийти в меню під час розмови чи важливої події!"
                );
                return;
            }

            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            String currentRoomId = this.getClass().getSimpleName();

            ukma.fourgirls.core.SaveManager.saveGame(currentRoomId, "");

            SceneManager.getInstance().switchToMainMenu();
        });
        return backButton;
    }

    private ImageView setupRoomImage(String imagePath) {
        Image roomImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(roomImage);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    protected void setupNavigation(String currentRoomName) {
        NavigationPanel navPanel = new NavigationPanel();

        if (!"MomRoom".equals(currentRoomName) && GameState.isUnlocked("MomRoom")) {
            navPanel.addNavigationTarget("Кімната матері", () -> {
                AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
                SceneManager.getInstance().switchToCachedRoom("MomRoom", MomRoom::new);
            });
        }

        if (!"Kitchen".equals(currentRoomName) && GameState.isUnlocked("Kitchen")) {
            navPanel.addNavigationTarget("Кухня", () -> {
                AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
                SceneManager.getInstance().switchToCachedRoom("Kitchen", Kitchen::new);
            });
        }

        if (!"ChildRoom".equals(currentRoomName) && GameState.isUnlocked("ChildRoom")) {
            navPanel.addNavigationTarget("Дитяча кімната", () -> {
                AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
                SceneManager.getInstance().switchToCachedRoom("ChildRoom", ChildRoom::new);
            });
        }

        if (!"Corridor".equals(currentRoomName) && GameState.isUnlocked("Corridor")) {
            navPanel.addNavigationTarget("Коридор", () -> {
                AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
                SceneManager.getInstance().switchToCachedRoom("Corridor", Corridor::new);
            });
        }

        if (!"Yard".equals(currentRoomName) && ukma.fourgirls.state.GameState.isUnlocked("Yard")) {
            navPanel.addNavigationTarget("Подвір'я", () -> {
                ukma.fourgirls.core.AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
                ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("Yard", Yard::new);
            });
        }

        navPanel.attachTo(this.root);
    }

    public void showInventoryUI() {
        GameState.unlockInventory();
        this.inventory.setVisible(true);
    }
}