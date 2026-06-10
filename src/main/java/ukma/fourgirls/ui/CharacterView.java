package ukma.fourgirls.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class CharacterView {
    private final StackPane container;
    private final ImageView spriteView;
    private final StackPane frame;

    public CharacterView(StackPane container) {
        this.container = container;
        this.spriteView = new ImageView();
        this.frame = new StackPane(spriteView);

        frame.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/css/dialogue.css")
        ).toExternalForm());
        frame.getStyleClass().add("dialog-actor-frame");

        spriteView.setFitHeight(500);
        spriteView.setPreserveRatio(true);

        frame.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        this.setPositionSide(true);
    }

    /**
     * Показує персонажа та рамку на екрані
     */
    public void setCharacterSprite(String imagePath) {
        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            spriteView.setImage(img);

            if (!container.getChildren().contains(frame)) {
                container.getChildren().add(frame);
                frame.toFront();
            }
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити портрет: " + imagePath);
        }
    }

    /**
     * Встановлює сторону екрана, на якій відображається персонаж.
     * @param isLeft true — зліва (як зазвичай), false — справа
     */
    public void setPositionSide(boolean isLeft) {
        if (isLeft) {
            StackPane.setAlignment(frame, Pos.BOTTOM_LEFT);
            StackPane.setMargin(frame, new Insets(0, 0, 210, 160));
        } else {
            StackPane.setAlignment(frame, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(frame, new Insets(0, 160, 210, 0));
        }
    }

    /**
     * Повністю очищує екран від портрета та рамки
     */
    public void hide() {
        container.getChildren().remove(frame);
        spriteView.setImage(null);
    }
}
