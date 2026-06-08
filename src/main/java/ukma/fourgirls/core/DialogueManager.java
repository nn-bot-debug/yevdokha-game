package ukma.fourgirls.core;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private Label nameLabel;
    private ImageView portraitView;
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
        play(container, null, null, lines, onDialogueFinished);
    }

    public void play(StackPane container, String characterName, Image portrait, String[] lines, Runnable onDialogueFinished) {
        this.currentContainer = container;
        this.currentLines = lines;
        this.currentOnFinished = onDialogueFinished;
        this.currentLineIndex = 0;

        Platform.runLater(() -> {

            boolean isNarrator = (characterName == null || characterName.isEmpty()) && portrait == null;

            if (isNarrator) {
                // Для автора
                textLabel.setAlignment(Pos.CENTER);
                textLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            } else {
                // Для персонажа
                textLabel.setAlignment(Pos.TOP_LEFT);
                textLabel.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
            }

            if (portrait != null) {
                portraitView.setImage(portrait);
                portraitView.setVisible(true);
                portraitView.setManaged(true);
            } else {
                portraitView.setVisible(false);
                portraitView.setManaged(false);
            }

            if (characterName != null && !characterName.isEmpty()) {
                nameLabel.setText(characterName);
                nameLabel.setVisible(true);
                nameLabel.setManaged(true);
            } else {
                nameLabel.setVisible(false);
                nameLabel.setManaged(false);
            }

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

        // --- ІНІЦІАЛІЗАЦІЯ НОВИХ ЕЛЕМЕНТІВ ---
        portraitView = new ImageView();
        portraitView.setFitWidth(100);
        portraitView.setFitHeight(100);
        portraitView.setPreserveRatio(true);
        // Можна додати клас для рамки портрета: portraitView.getStyleClass().add("dialogue-portrait");

        nameLabel = new Label();
        nameLabel.getStyleClass().add("dialogue-name");
        nameLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 18));

        textLabel = new Label();
        textLabel.setFont(font);
        textLabel.getStyleClass().add("dialogue-text");
        textLabel.setWrapText(true);
        textLabel.setAlignment(Pos.TOP_LEFT);
        textLabel.setMaxWidth(Double.MAX_VALUE);

        VBox textContainer = new VBox(5);
        textContainer.getChildren().addAll(nameLabel, textLabel);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        HBox contentBox = new HBox(20);
        contentBox.setPadding(new Insets(15));
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.getChildren().addAll(portraitView, textContainer);

        Label hintLabel = new Label("▶ Клікніть для продовження");
        hintLabel.getStyleClass().add("dialogue-hint");
        StackPane.setAlignment(hintLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(hintLabel, new Insets(0, 10, 10, 0));

        dialogueRootPane.getChildren().addAll(contentBox, hintLabel);

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