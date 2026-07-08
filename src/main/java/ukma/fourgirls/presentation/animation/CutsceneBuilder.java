package ukma.fourgirls.presentation.animation;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ukma.fourgirls.GameContext;

import java.util.ArrayList;
import java.util.List;

public class CutsceneBuilder {
    private final StackPane root;
    private final GameContext context;
    private final List<Runnable> actions = new ArrayList<>();
    private int currentIndex = 0;

    private CutsceneBuilder(StackPane root, GameContext context) {
        this.root = root;
        this.context = context;
    }

    public static CutsceneBuilder create(StackPane root, GameContext context) {
        return new CutsceneBuilder(root, context);
    }

    public CutsceneBuilder addDialogue(String... lines) {
        actions.add(() -> context.getDialogue().play(root, lines, this::next));
        return this;
    }

    public CutsceneBuilder addDialogue(String characterName, Image portrait, String... lines) {
        actions.add(() -> context.getDialogue().play(root, characterName, portrait, lines, this::next));
        return this;
    }

    public CutsceneBuilder addAnimation(Animation animation) {
        actions.add(() -> {
            animation.setOnFinished(e -> next());
            animation.play();
        });
        return this;
    }

    public CutsceneBuilder execute(Runnable customCode) {
        actions.add(() -> {
            customCode.run();
            next();
        });
        return this;
    }

    public CutsceneBuilder addPause(double seconds) {
        actions.add(() -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
            pause.setOnFinished(e -> next());
            pause.play();
        });
        return this;
    }

    public void play() {
        currentIndex = 0;
        next();
    }

    private void next() {
        if (currentIndex < actions.size()) {
            Runnable action = actions.get(currentIndex);
            currentIndex++;
            action.run();
        }
    }
}