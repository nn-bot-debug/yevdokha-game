package ukma.fourgirls.ui.puzzles;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PuzzleTimer extends Text {
    private Timeline timeline;
    private int secondsLeft;
    private final String prefix;
    private final Runnable onTimeUp;

    public PuzzleTimer(int initialSeconds, String prefix, int fontSize, Runnable onTimeUp) {
        this.secondsLeft = initialSeconds;
        this.prefix = prefix != null ? prefix : "";
        this.onTimeUp = onTimeUp;

        this.setFill(Color.web("#f87171"));
        try {
            Font timerFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Creepster-Regular.ttf"), fontSize);
            this.setFont(timerFont != null ? timerFont : Font.font("Verdana", fontSize - 6));
        } catch (Exception e) {
            this.setFont(Font.font("Verdana", fontSize - 6));
        }

        updateDisplay();
    }

    public void start() {
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            updateDisplay();

            if (secondsLeft <= 0) {
                stop();
                if (onTimeUp != null) onTimeUp.run();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public void setTimeColor(Color color) {
        this.setFill(color);
    }

    public void resetTime(int newSeconds) {
        this.secondsLeft = newSeconds;
        updateDisplay();
    }

    private void updateDisplay() {
        int minutes = Math.max(0, secondsLeft / 60);
        int seconds = Math.max(0, secondsLeft % 60);
        this.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
    }
}