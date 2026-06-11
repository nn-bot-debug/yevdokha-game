package ukma.fourgirls.core;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

public class DialogueManager {

    private static final double ROOT_MAX_WIDTH = 1200;
    private static final double ROOT_HEIGHT = 180;
    private static final double BG_BOX_HEIGHT = 140;
    private static final double PORTRAIT_SIZE = 180;
    private static final Insets PORTRAIT_MARGIN = new Insets(0, 0, 0, 160);

    private String[] currentLines;
    private Runnable currentOnFinished;
    private int currentLineIndex = 0;

    private StackPane currentContainer;
    private StackPane dialogueRootPane;
    private StackPane bgBox;
    private Label textLabel;
    private Label nameLabel;
    private ImageView portraitView;
    private VBox textContainer;
    private HBox contentBox;

    private static class Holder {
        private static final DialogueManager INSTANCE = new DialogueManager();
    }

    public static DialogueManager getInstance() {
        return Holder.INSTANCE;
    }

    private DialogueManager() {
        initUI();
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
                configureNarratorStyle();
            } else {
                configureCharacterStyle(characterName, portrait);
            }

            textLabel.setText(currentLines[0]);

            if (!currentContainer.getChildren().contains(dialogueRootPane)) {
                currentContainer.getChildren().add(dialogueRootPane);
            }
            dialogueRootPane.toFront();
        });
    }

   private void configureNarratorStyle() {
        setNodeVisible(portraitView, false);
        setNodeVisible(nameLabel, false);

        dialogueRootPane.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(dialogueRootPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dialogueRootPane, new Insets(0, 0, 40, 0));

        if (!bgBox.getStyleClass().contains("dialogue-box")) {
            bgBox.getStyleClass().add("dialogue-box");
        }
        bgBox.setStyle("");

        textContainer.setAlignment(Pos.CENTER);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setTextAlignment(TextAlignment.CENTER);

        textLabel.setStyle("-fx-text-fill: white;");

        StackPane.setMargin(contentBox, Insets.EMPTY);
    }

    private void configureCharacterStyle(String characterName, Image portrait) {
        dialogueRootPane.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(dialogueRootPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dialogueRootPane, new Insets(0, 0, 40, 0)); // Відступ плашки знизу

        if (!bgBox.getStyleClass().contains("dialogue-box")) {
            bgBox.getStyleClass().add("dialogue-box");
        }
        bgBox.setStyle("");

        textContainer.setAlignment(Pos.CENTER_LEFT);
        textLabel.setAlignment(Pos.TOP_LEFT);
        textLabel.setTextAlignment(TextAlignment.LEFT);
        textLabel.setStyle("-fx-text-fill: white;");

        if (portrait != null) {
            portraitView.setImage(portrait);
            setNodeVisible(portraitView, true);
            StackPane.setMargin(contentBox, PORTRAIT_MARGIN);
        } else {
            setNodeVisible(portraitView, false);
            StackPane.setMargin(contentBox, Insets.EMPTY);
        }

        if (characterName != null && !characterName.isEmpty()) {
            nameLabel.setText(characterName);
            setNodeVisible(nameLabel, true);
        } else {
            setNodeVisible(nameLabel, false);
        }
    }

    private void initUI() {
        dialogueRootPane = new StackPane();
        dialogueRootPane.setMaxWidth(ROOT_MAX_WIDTH);
        dialogueRootPane.setMinHeight(ROOT_HEIGHT);
        dialogueRootPane.setMaxHeight(ROOT_HEIGHT);

        bgBox = new StackPane();
        bgBox.setMinHeight(BG_BOX_HEIGHT);
        bgBox.setMaxHeight(BG_BOX_HEIGHT);
        bgBox.getStyleClass().add("dialogue-box");
        bgBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/dialogue.css")).toExternalForm());
        StackPane.setAlignment(bgBox, Pos.BOTTOM_CENTER);
        portraitView = new ImageView();
        portraitView.setFitWidth(PORTRAIT_SIZE);
        portraitView.setFitHeight(PORTRAIT_SIZE);
        portraitView.setPreserveRatio(true);
        StackPane.setAlignment(portraitView, Pos.BOTTOM_LEFT);
        StackPane.setMargin(portraitView, new Insets(0, 0, 0, 10));

        nameLabel = new Label();
        nameLabel.getStyleClass().add("dialogue-name");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        textLabel = new Label();
        textLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        textLabel.getStyleClass().add("dialogue-text");
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(Double.MAX_VALUE);

        textContainer = new VBox(5);
        textContainer.getChildren().addAll(nameLabel, textLabel);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        contentBox = new HBox();
        contentBox.setPadding(new Insets(15, 40, 15, 20));
        contentBox.getChildren().add(textContainer);

        Label hintLabel = new Label("▶ Клікніть для продовження");
        hintLabel.getStyleClass().add("dialogue-hint");
        StackPane.setAlignment(hintLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(hintLabel, new Insets(0, 15, 10, 0));

        bgBox.getChildren().addAll(contentBox, hintLabel);
        dialogueRootPane.getChildren().addAll(bgBox, portraitView);

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

    private void setNodeVisible(Node node, boolean isVisible) {
        node.setVisible(isVisible);
        node.setManaged(isVisible);
    }
}