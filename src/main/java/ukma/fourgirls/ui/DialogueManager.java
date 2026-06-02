package ukma.fourgirls.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class DialogueManager {

    private final StackPane container;
    private final String[] lines;
    private final Runnable onDialogueFinished;

    private int currentLineIndex = 0;

    private Pane dialoguePane;
    private Label textLabel;
    private Font font;

    /**
     * @param container StackPane кімнати, куди ми додаємо плашку діалогу
     * @param lines Масив реплік для озвучування
     * @param onDialogueFinished Що зробити, коли гравець проклікає весь діалог
     */
    public DialogueManager(StackPane container, String[] lines, Runnable onDialogueFinished) {
        this.container = container;
        this.lines = lines;
        this.onDialogueFinished = onDialogueFinished;

        try {
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 24);
        } catch (Exception e) {
            font = Font.font("Arial", 22);
        }

        Platform.runLater(this::createDialogueUI);
    }

    private void createDialogueUI() {
        // Створення плашки для тексту
        StackPane dialogueRootPane = new StackPane();

        // ТЕПЕР НАЛАШТОВУЄМО ПРАВИЛЬНУ ЗМІННУ (dialogueRootPane)
        dialogueRootPane.setMaxWidth(1200);
        dialogueRootPane.setMinHeight(140);
        dialogueRootPane.setMaxHeight(140);
        dialogBoxStyle(dialogueRootPane); // передаємо сюди новий StackPane

        StackPane.setAlignment(dialogueRootPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dialogueRootPane, new Insets(0, 0, 50, 0));

        // Текст репліки
        textLabel = new Label(lines[currentLineIndex]);
        textLabel.setFont(font);
        textLabel.setStyle("-fx-text-fill: #9ba89e;");
        textLabel.setWrapText(true);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        textLabel.setMaxWidth(Double.MAX_VALUE);

        // Вирівнюємо текст строго по центру
        StackPane.setAlignment(textLabel, Pos.CENTER);

        // Підказка в лівому нижньому кутку
        Label hintLabel = new Label("[ Клікніть, щоб продовжити... ]");
        hintLabel.setFont(Font.font("Arial", 12));
        hintLabel.setStyle("-fx-text-fill: #5a665e;");

        StackPane.setAlignment(hintLabel, Pos.BOTTOM_LEFT);

        dialogueRootPane.getChildren().addAll(textLabel, hintLabel);

        dialogueRootPane.setOnMouseClicked(e -> nextLine());

        this.dialoguePane = dialogueRootPane;
        container.getChildren().add(dialogueRootPane);
    }

    private void nextLine() {
        currentLineIndex++;

        if (currentLineIndex < lines.length) {
            textLabel.setText(lines[currentLineIndex]);
        } else {
            container.getChildren().remove(dialoguePane);
            if (onDialogueFinished != null)
                onDialogueFinished.run();
        }
    }


    private void dialogBoxStyle(Pane pane) {
        pane.setPadding(new Insets(15, 40, 15, 40));
        pane.setStyle(
                "-fx-background-color: rgba(15, 22, 22, 0.75);" +
                        "-fx-border-color: #3c5050;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-radius: 6px;" + // збільшили до 6px для м'якості кутів
                        "-fx-border-radius: 6px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0.0, 0, 10);"
        );
    }
}
