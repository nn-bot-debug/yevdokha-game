package ukma.fourgirls;

import javafx.stage.Stage;
import ukma.fourgirls.application.LocationRegistry;
import ukma.fourgirls.application.service.AudioEngine;
import ukma.fourgirls.application.service.InventoryService;
import ukma.fourgirls.infrastructure.asset.JavaFxAudioEngine;
import ukma.fourgirls.presentation.component.DialogueManager;
import ukma.fourgirls.presentation.controller.GameFlowController;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.ui.CameraController;

public class GameContext {
    private final AudioEngine audioEngine;
    private final GameFlowController sceneManager;
    private final DialogueManager dialogueManager;
    private final GameSession session;
    private final LocationRegistry locationRegistry;
    private final CameraController cameraController;
    private final InventoryService inventoryService;

    public GameContext(Stage primaryStage) {
        this.session = new GameSession();
        this.audioEngine = new JavaFxAudioEngine();
        this.dialogueManager = new DialogueManager();
        this.sceneManager = new GameFlowController(primaryStage, this);
        this.locationRegistry = new LocationRegistry(this);
        this.cameraController = new CameraController(this);
        this.inventoryService = new InventoryService(this.getSession());
    }

    public AudioEngine getAudio() { return audioEngine; }
    public GameFlowController getScene() { return sceneManager; }
    public DialogueManager getDialogue() { return dialogueManager; }
    public GameSession getSession() { return session; }
    public LocationRegistry getLocations() { return locationRegistry; }
    public CameraController getCamera() { return cameraController; }
    public InventoryService getInventory() { return inventoryService; }
}