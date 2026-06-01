package ukma.fourgirls.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class NotificationManager {

    private static Font font;

    static {
        try {
            font = Font.loadFont(NotificationManager.class.getResourceAsStream("/Creepster-Regular.ttf"), 22);
        } catch (Exception e) {
            font = Font.font("System", javafx.scene.text.FontWeight.BOLD, 20);
        }
    }

    /**
     * Показує сповіщення поверх усього екрана
     * @param container StackPane поточного екрана (наприклад, root кімнати чи меню)
     * @param text Текст повідомлення (наприклад: "Двері зачинено...", "Знайдено ключ!")
     */
    public static void showNotification(StackPane container, String text) {

        StackPane notificationPane = new StackPane();
        notificationPane.setMinWidth(Region.USE_PREF_SIZE);
        notificationPane.setMinHeight(Region.USE_PREF_SIZE);
        notificationPane.setMaxWidth(Region.USE_PREF_SIZE);
        notificationPane.setMaxHeight(Region.USE_PREF_SIZE);

        // Вирівнюємо знизу по центру (можна Pos.CENTER, якщо треба строго посередині)
        StackPane.setAlignment(notificationPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(notificationPane, new Insets(0, 0, 350, 0));

        // Стилізуємо плашку в колір кнопок вашої команди
        notificationPane.setStyle(
                "-fx-background-color: rgba(25, 35, 35, 0.9);" +
                        "-fx-border-color: #3c5050;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 10, 0.0, 0, 4);"
        );

        Label label = new Label(text);
        label.setFont(font);
        label.setStyle("-fx-text-fill: #9ba89e;");

        label.setPadding(new Insets(12, 30, 12, 30));
        label.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationPane.getChildren().add(label);

        notificationPane.setOpacity(0);

        // Додаємо на екран поверх усього
        container.getChildren().add(notificationPane);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notificationPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Налаштовуємо паузу
        PauseTransition stay = new PauseTransition(Duration.seconds(2.5));

        // Налаштовуємо анімацію зникнення (Fade Out)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notificationPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Запускаємо все по черзі
        SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);

        // Коли анімація повністю завершиться — видаляємо плашку з пам'яті
        sequence.setOnFinished(e -> container.getChildren().remove(notificationPane));

        sequence.play();
    }
}
