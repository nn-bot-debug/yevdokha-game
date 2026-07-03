package ukma.fourgirls.core;

import javafx.stage.Stage;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.ui.CameraController;

public class GameContext {
    private final AudioManager audioManager;
    private final SceneManager sceneManager;
    private final DialogueManager dialogueManager;
    private final GameSession session;
    private final LocationRegistry locationRegistry;
    private final CameraController cameraController;

    public GameContext(Stage primaryStage) {
        this.session = new GameSession();
        this.audioManager = new AudioManager();
        this.dialogueManager = new DialogueManager();
        this.sceneManager = new SceneManager(primaryStage, this);
        this.locationRegistry = new LocationRegistry(this);
        this.cameraController = new CameraController(this);
    }

    public AudioManager getAudio() { return audioManager; }
    public SceneManager getScene() { return sceneManager; }
    public DialogueManager getDialogue() { return dialogueManager; }
    public GameSession getSession() { return session; }
    public LocationRegistry getLocations() { return locationRegistry; }
    public CameraController getCamera() { return cameraController; }
}