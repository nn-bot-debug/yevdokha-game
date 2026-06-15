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

    private boolean isLeft = true;
    private double bottomOffset = 0;

    public CharacterView(StackPane container) {
        this.container = container;
        this.spriteView = new ImageView();

        spriteView.setPreserveRatio(true);
        spriteView.setSmooth(true);
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

            if (imagePath.contains("ant-ryadovuy") || imagePath.contains("ant-brother")) {
                spriteView.setFitWidth(480);
                spriteView.setFitHeight(0);

                this.bottomOffset = 95;
            } else if (imagePath.contains("Lisovuk")) {
                spriteView.setFitHeight(880);
                spriteView.setFitWidth(0);
                this.bottomOffset = -50;
            } else {
                spriteView.setFitHeight(640);
                spriteView.setFitWidth(0);

                this.bottomOffset = 0;
            }
            if (!container.getChildren().contains(spriteView)) {
                container.getChildren().add(spriteView);
            }
            spriteView.toFront();
            this.updatePosition();

        } catch (Exception e) {
            System.err.println("Не вдалося завантажити портрет: " + imagePath);
        }
    }

    /**
     * Встановлює сторону екрана, на якій відображається персонаж.
     * @param isLeft true — зліва (як зазвичай), false — справа
     */
    public void setPositionSide(boolean isLeft) {
        this.isLeft = isLeft;
        this.updatePosition();
    }

    /**
     * 🔥 Внутрішній метод для чистого розрахунку марджинів та відступів
     */
    private void updatePosition() {
        if (spriteView.getImage() == null) return;

        if (isLeft) {
            StackPane.setAlignment(spriteView, Pos.BOTTOM_LEFT);
            StackPane.setMargin(spriteView, new Insets(0, 0, 0, 50));
        } else {
            StackPane.setAlignment(spriteView, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(spriteView, new Insets(0, 50, bottomOffset, 0));
        }
    }

    /**
     * Повністю очищує екран від портрета та рамки
     */
    public void hide() {
        container.getChildren().remove(spriteView);
        spriteView.setImage(null);
        this.bottomOffset = 0;
    }
}
