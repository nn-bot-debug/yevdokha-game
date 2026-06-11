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
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.Objects;

public final class NotificationManager {

    private static final Font FONT = Font.font("System", FontWeight.BOLD, 20);

    private static final String CSS_PATH = Objects.requireNonNull(
            NotificationManager.class.getResource("/css/notifications.css")
    ).toExternalForm();

    private NotificationManager() {}

    /**
     * Displays a notification over the entire screen.
     * * @param container The StackPane of the current screen
     * @param text The notification text
     */
    public static void showNotification(StackPane container, String text) {
        var notificationPane = new StackPane();
        notificationPane.setMinWidth(Region.USE_PREF_SIZE);
        notificationPane.setMinHeight(Region.USE_PREF_SIZE);
        notificationPane.setMaxWidth(Region.USE_PREF_SIZE);
        notificationPane.setMaxHeight(Region.USE_PREF_SIZE);

        notificationPane.getStyleClass().add("notification-pane");
        notificationPane.getStylesheets().add(CSS_PATH);

        StackPane.setAlignment(notificationPane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(notificationPane, new Insets(0, 0, 350, 0));

        var label = new Label(text);
        label.setFont(FONT);
        label.getStyleClass().add("notification-text");
        label.setPadding(new Insets(12, 30, 12, 30));
        label.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationPane.getChildren().add(label);
        notificationPane.setOpacity(0);

        container.getChildren().add(notificationPane);

        var fadeIn = new FadeTransition(Duration.millis(400), notificationPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        var stay = new PauseTransition(Duration.seconds(2.5));

        var fadeOut = new FadeTransition(Duration.millis(500), notificationPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        var sequence = new SequentialTransition(fadeIn, stay, fadeOut);

        sequence.setOnFinished(_ -> container.getChildren().remove(notificationPane));

        sequence.play();
    }
}