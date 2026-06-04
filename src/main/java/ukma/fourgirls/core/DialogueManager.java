package ukma.fourgirls.core;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.util.Objects;

public class DialogueManager {

    private static DialogueManager instance;

    private StackPane currentContainer;
    private String[] currentLines;
    private Runnable currentOnFinished;
    private int currentLineIndex = 0;

    private StackPane dialogueRootPane;
    private Label textLabel;
    private Font font;

    private DialogueManager() {
        font = Font.font("System", javafx.scene.text.FontWeight.BOLD, 20);
        Platform.runLater(this::initUI);
    }

    public static DialogueManager getInstance() {
        if (instance == null) {
            instance = new DialogueManager();
        }
        return instance;
    }

    public void play(StackPane container, String[] lines, Runnable onDialogueFinished) {
        this.currentContainer = container;
        this.currentLines = lines;
        this.currentOnFinished = onDialogueFinished;
        this.currentLineIndex = 0;

        Platform.runLater(() -> {
            textLabel.setText(currentLines[0]);

            if (!currentContainer.getChildren().contains(dialogueRootPane)) {
                currentContainer.getChildren().add(dialogueRootPane);
            }
        });
    }

    private void initUI() {
        dialogueRootPane = new StackPane();
        dialogueRootPane.setMaxWidth(1200);
        dialogueRootPane.setMinHeight(140);
        dialogueRootPane.setMaxHeight(140);

        dialogueRootPane.getStyleClass().add("dialogue-box");
        dialogueRootPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/dialogue.css")).toExternalForm());

        StackPane.setAlignment(dialogueRootPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dialogueRootPane, new Insets(0, 0, 50, 0));

        textLabel = new Label();
        textLabel.setFont(font);
        textLabel.getStyleClass().add("dialogue-text");
        textLabel.setWrapText(true);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        textLabel.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(textLabel, Pos.CENTER);

        Label hintLabel = new Label("▶ Клікніть для продовження");
        hintLabel.getStyleClass().add("dialogue-hint");
        StackPane.setAlignment(hintLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(hintLabel, new Insets(0, 10, 0, 0));

        dialogueRootPane.getChildren().addAll(textLabel, hintLabel);

        dialogueRootPane.setOnMouseClicked(e -> nextLine());
    }

    private void nextLine() {
        currentLineIndex++;

        if (currentLineIndex < currentLines.length) {
            textLabel.setText(currentLines[currentLineIndex]);
        } else {
            currentContainer.getChildren().remove(dialogueRootPane);
            if (currentOnFinished != null) {
                currentOnFinished.run();
            }
        }
    }
}