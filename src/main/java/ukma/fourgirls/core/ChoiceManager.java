package ukma.fourgirls.core;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.Objects;

public final class ChoiceManager {

    private static final String CSS_PATH = Objects.requireNonNull(
            ChoiceManager.class.getResource("/css/choices.css")
    ).toExternalForm();

    private ChoiceManager() {}

    public record Option(String text, Runnable action) {}

    /**
     * Displays a modal selection window with an arbitrary number of options.
     *
     * @param container The room's StackPane
     * @param question The question text
     * @param options List of options
     */
    public static void show(StackPane container, String question, Option... options) {

        Platform.runLater(() -> {
            var choiceBox = new VBox(20);
            choiceBox.setAlignment(Pos.CENTER);
            choiceBox.setMinWidth(400);
            choiceBox.setMaxWidth(450);
            choiceBox.setMinHeight(VBox.USE_PREF_SIZE);
            choiceBox.setMaxHeight(VBox.USE_PREF_SIZE);
            choiceBox.getStyleClass().add("choice-box");

            choiceBox.getStylesheets().add(CSS_PATH);

            var promptText = new Label(question);
            promptText.getStyleClass().add("choice-prompt");
            promptText.setWrapText(true);
            choiceBox.getChildren().add(promptText);

            for (var option : options) {
                var btn = new Button(option.text());
                btn.getStyleClass().add("choice-button");

                btn.setOnAction(e -> {
                    container.getChildren().removeAll(choiceBox);
                    if (option.action() != null) {
                        option.action().run();
                    }
                });
                choiceBox.getChildren().add(btn);
            }

            StackPane.setAlignment(choiceBox, Pos.CENTER);
            container.getChildren().addAll(choiceBox);
        });
    }
}