package ukma.fourgirls.core;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.Objects;

public class ChoiceManager {

    /**
     * Допоміжний клас, який зв'язує текст на кнопці з дією, яка має відбутися.
     */
    public static class Option {
        private final String text;
        private final Runnable action;

        public Option(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }
    }

    /**
     * Виводить модальне вікно вибору з довільною кількістю варіантів.
     * * @param container StackPane кімнати
     * @param question Текст питання
     * @param options Перелік варіантів
     */
    public static void show(StackPane container, String question, Option... options) {

        Platform.runLater(() -> {
            VBox choiceBox = new VBox(20);
            choiceBox.setAlignment(Pos.CENTER);
            choiceBox.setMinWidth(400);
            choiceBox.setMaxWidth(450);
            choiceBox.setMinHeight(VBox.USE_PREF_SIZE);
            choiceBox.setMaxHeight(VBox.USE_PREF_SIZE);
            choiceBox.getStyleClass().add("choice-box");

            choiceBox.getStylesheets().add(Objects.requireNonNull(ChoiceManager.class.getResource("/css/choices.css")).toExternalForm());

            Label promptText = new Label(question);
            promptText.getStyleClass().add("choice-prompt");
            promptText.setWrapText(true);
            choiceBox.getChildren().add(promptText);

            for (Option option : options) {
                Button btn = new Button(option.text);
                btn.getStyleClass().add("choice-button");

                btn.setOnAction(e -> {
                    container.getChildren().remove(choiceBox);
                    if (option.action != null) {
                        option.action.run();
                    }
                });
                choiceBox.getChildren().add(btn);
            }

            StackPane.setAlignment(choiceBox, Pos.CENTER);
            container.getChildren().add(choiceBox);
        });
    }
}
