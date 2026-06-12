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

    public CharacterView(StackPane container) {
        this.container = container;
        this.spriteView = new ImageView();

        spriteView.setFitHeight(640);
        spriteView.setPreserveRatio(true);
        spriteView.setManaged(true);

        this.setPositionSide(true);
    }

    /**
     * Показує персонажа та рамку на екрані
     */
    public void setCharacterSprite(String imagePath) {
        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            spriteView.setImage(img);

            if (!container.getChildren().contains(spriteView)) {
                container.getChildren().add(spriteView);
            }
            spriteView.toFront();

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
            StackPane.setAlignment(spriteView, Pos.BOTTOM_LEFT);
            StackPane.setMargin(spriteView, new Insets(0, 0, 0, 50));
        } else {
            StackPane.setAlignment(spriteView, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(spriteView, new Insets(0, 50, 0, 0));
        }
    }

    /**
     * Повністю очищує екран від портрета та рамки
     */
    public void hide() {
        container.getChildren().remove(spriteView);
        spriteView.setImage(null);
    }
}
