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

public class StatNotification {

    public static void show(StackPane container, int currentKarma, int addedPoints) {
        HBox notificationPane = new HBox();
        notificationPane.setAlignment(Pos.CENTER_LEFT);
        notificationPane.setPadding(new Insets(12, 20, 12, 20));

        notificationPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        notificationPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationPane.getStyleClass().add("stat-notification-pane");
        notificationPane.getStylesheets().add(Objects.requireNonNull(
                StatNotification.class.getResource("/css/stats.css")
        ).toExternalForm());

        StackPane.setAlignment(notificationPane, Pos.TOP_RIGHT);
        StackPane.setMargin(notificationPane, new Insets(30, 30, 0, 0));

        Label textLabel = new Label();
        if (addedPoints > 0) {
            textLabel.setText("Доля змінилась: " + addedPoints + " карма");
            textLabel.getStyleClass().add("stat-text-plus");
        } else {
            textLabel.setText("Доля змінилась: " +  addedPoints + " карма");
            textLabel.getStyleClass().add("stat-text-minus");
        }
        textLabel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationPane.getChildren().add(textLabel);
        notificationPane.setOpacity(0);
        notificationPane.setTranslateX(350);

        container.getChildren().add(notificationPane);

        TranslateTransition moveIn = new TranslateTransition(Duration.millis(400), notificationPane);
        moveIn.setToX(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notificationPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ParallelTransition showAnim =  new ParallelTransition(moveIn, fadeIn);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notificationPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(3.0));

        fadeOut.setOnFinished(e -> container.getChildren().remove(notificationPane));

        showAnim.setOnFinished(e -> fadeOut.play());
        showAnim.play();
    }
}
