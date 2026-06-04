package ukma.fourgirls.core;

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

import java.util.Objects;

public class NotificationManager {

    private static final Font font;

    static {
        font = Font.font("System", javafx.scene.text.FontWeight.BOLD, 20);
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

        notificationPane.getStyleClass().add("notification-pane");
        notificationPane.getStylesheets().add(Objects.requireNonNull(NotificationManager.class.getResource("/css/notifications.css")).toExternalForm());

        StackPane.setAlignment(notificationPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(notificationPane, new Insets(0, 0, 350, 0));

        Label label = new Label(text);
        label.setFont(font);
        label.getStyleClass().add("notification-text");
        label.setPadding(new Insets(12, 30, 12, 30));
        label.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationPane.getChildren().add(label);
        notificationPane.setOpacity(0);

        container.getChildren().add(notificationPane);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notificationPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition stay = new PauseTransition(Duration.seconds(2.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notificationPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
        sequence.setOnFinished(e -> container.getChildren().remove(notificationPane));

        sequence.play();
    }
}