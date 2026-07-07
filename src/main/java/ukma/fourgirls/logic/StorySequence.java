package ukma.fourgirls.logic;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ukma.fourgirls.GameContext;

import java.util.ArrayList;
import java.util.List;

public class StorySequence {
    private final StackPane root;
    private final GameContext context;
    private final List<Runnable> actions = new ArrayList<>();
    private int currentIndex = 0;

    private StorySequence(StackPane root,  GameContext context) {
        this.root = root;
        this.context = context;
    }

    public static StorySequence create(StackPane root, GameContext context) {
        return new StorySequence(root, context);
    }

    public StorySequence addDialogue(String... lines) {
        actions.add(() -> context.getDialogue().play(root, lines, this::next));
        return this;
    }

    public StorySequence addDialogue(String characterName, Image portrait, String... lines) {
        actions.add(() -> context.getDialogue().play(root, characterName, portrait, lines, this::next));
        return this;
    }

    public StorySequence addAnimation(Animation animation) {
        actions.add(() -> {
            animation.setOnFinished(e -> next());
            animation.play();
        });
        return this;
    }

    public StorySequence execute(Runnable customCode) {
        actions.add(() -> {
            customCode.run();
            next();
        });
        return this;
    }

    public StorySequence addPause(double seconds) {
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