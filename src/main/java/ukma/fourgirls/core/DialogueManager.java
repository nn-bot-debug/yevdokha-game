package ukma.fourgirls.core;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

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
        dialogBoxStyle(dialogueRootPane);

        StackPane.setAlignment(dialogueRootPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dialogueRootPane, new Insets(0, 0, 50, 0));

        textLabel = new Label();
        textLabel.setFont(font);
        textLabel.setStyle("-fx-text-fill: #9ba89e;");
        textLabel.setWrapText(true);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        textLabel.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(textLabel, Pos.CENTER);

        Label hintLabel = new Label("▶ Клікніть для продовження");
        hintLabel.setFont(Font.font("Arial", 12));
        hintLabel.setStyle("-fx-text-fill: #4b544f;");
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

    private void dialogBoxStyle(Pane pane) {
        pane.setPadding(new Insets(15, 40, 15, 40));
        pane.setStyle(
                "-fx-background-color: rgba(15, 22, 22, 0.75);" +
                        "-fx-border-color: #3c5050;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-border-radius: 6px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0.0, 0, 10);"
        );
    }
}