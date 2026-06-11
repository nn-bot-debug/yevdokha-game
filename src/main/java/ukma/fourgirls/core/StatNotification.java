package ukma.fourgirls.core;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Objects;

public final class StatNotification {

    private static final String CSS_PATH = Objects.requireNonNull(
            StatNotification.class.getResource("/css/stats.css")
    ).toExternalForm();

    private StatNotification() {}

    public static void show(StackPane container, int currentKarma, int addedPoints) {
        var notificationPane = new HBox();
        notificationPane.setAlignment(Pos.CENTER_LEFT);
        notificationPane.setPadding(new Insets(12, 20, 12, 20));

        notificationPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        notificationPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationPane.getStyleClass().add("stat-notification-pane");
        notificationPane.getStylesheets().add(CSS_PATH);

        StackPane.setAlignment(notificationPane, Pos.TOP_RIGHT);
        StackPane.setMargin(notificationPane, new Insets(30, 30, 0, 0));

        var textLabel = new Label();

        String sign = addedPoints > 0 ? "+" : "";
        textLabel.setText("Доля змінилась: " + sign + addedPoints + " карма");

        if (addedPoints > 0) {
            textLabel.getStyleClass().add("stat-text-plus");
        } else {
            textLabel.getStyleClass().add("stat-text-minus");
        }

        textLabel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationPane.getChildren().add(textLabel);
        notificationPane.setOpacity(0);
        notificationPane.setTranslateX(350);

        container.getChildren().add(notificationPane);

        var moveIn = new TranslateTransition(Duration.millis(400), notificationPane);
        moveIn.setToX(0);

        var fadeIn = new FadeTransition(Duration.millis(400), notificationPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        var showAnim = new ParallelTransition(moveIn, fadeIn);

        var fadeOut = new FadeTransition(Duration.millis(500), notificationPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(3.0));

        fadeOut.setOnFinished(_ -> container.getChildren().remove(notificationPane));

        showAnim.setOnFinished(_ -> fadeOut.play());
        showAnim.play();
    }
}