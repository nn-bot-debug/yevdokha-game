package ukma.fourgirls.ui.puzzles;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ukma.fourgirls.core.GameContext;
import ukma.fourgirls.state.GameSession;

import java.util.Objects;

public abstract class Puzzle extends StackPane {

    protected final GameContext context;
    protected final GameSession session;
    protected VBox resultBox;
    protected Text resultTitle;
    protected Button resultButton;


    public Puzzle(GameContext context) {
        this.context = context;
        this.session = context.getSession();
    }

    protected void showInstructionOverlay(String title, String description, Runnable onStart) {
        VBox tutorialBox = new VBox(20);
        tutorialBox.setAlignment(Pos.CENTER);
        tutorialBox.setMaxWidth(550);
        tutorialBox.setMaxHeight(450);
        tutorialBox.setPadding(new Insets(30, 40, 30, 40));
        tutorialBox.getStyleClass().add("settings-dialog");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("settings-title");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("settings-label");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 15px; -fx-text-alignment: center; -fx-line-spacing: 5;");

        Button acceptButton = new Button("ПОЧАТИ");
        acceptButton.getStyleClass().add("settings-button");
        acceptButton.setPrefWidth(160);
        acceptButton.setStyle("-fx-cursor: hand;");

        acceptButton.setOnAction(e -> {
            context.getAudio().buttonSound("/music/button-click-sound.wav");
            this.getChildren().remove(tutorialBox);

            if (onStart != null) {
                onStart.run();
            }
        });

        tutorialBox.getChildren().addAll(titleLabel, descriptionLabel, acceptButton);
        this.getChildren().add(tutorialBox);
    }

    protected void setupBackground(String imagePath, double blurRadius) {
        try {
            Image bgImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            ImageView backgroundView = new ImageView(bgImg);
            backgroundView.setFitWidth(1920);
            backgroundView.setFitHeight(1080);
            backgroundView.setPreserveRatio(false);

            if (blurRadius > 0) {
                backgroundView.setEffect(new GaussianBlur(blurRadius));
            }
            this.getChildren().add(backgroundView);
        } catch (Exception e) {
            System.err.println("Failed to load the background.");
            this.setStyle("-fx-background-color: #0b1310;");
        }

        Pane dimOverlay = new Pane();
        dimOverlay.setStyle("-fx-background-color: rgba(10, 12, 15, 0.45);");
        this.getChildren().add(dimOverlay);
    }

    protected VBox createResultBox() {
        resultBox = new VBox(20);
        resultBox.getStyleClass().add("puzzle-result-box");
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setVisible(false);
        resultBox.managedProperty().bind(resultBox.visibleProperty());

        resultTitle = new Text();
        resultTitle.setFont(Font.font("Verdana", 24));

        resultButton = new Button();
        resultButton.getStyleClass().add("puzzle-action-button");

        resultBox.getChildren().addAll(resultTitle, resultButton);
        return resultBox;
    }

    protected void showResultOverlay(String message, boolean isWin, String btnText, Runnable onAction) {
        resultTitle.setText(message);

        resultTitle.getStyleClass().removeAll("puzzle-text-win", "puzzle-text-lose");
        resultTitle.getStyleClass().add(isWin ? "puzzle-text-win" : "puzzle-text-lose");

        resultButton.setText(btnText);
        resultButton.setOnAction(e -> {
            if (onAction != null) {
                onAction.run();
            }
        });

        resultBox.setVisible(true);
        resultBox.toFront();
    }

    protected void setupKarmaListener(double topMargin) {
        if (session != null) {
            session.setKarmaListener((currentKarma, addedPoints) -> {
                ukma.fourgirls.core.StatNotification.show(this, currentKarma, addedPoints);

                if (!this.getChildren().isEmpty()) {
                    var notification = this.getChildren().getLast();
                    StackPane.setAlignment(notification, Pos.TOP_CENTER);
                    StackPane.setMargin(notification, new Insets(topMargin, 0, 0, 0));
                    notification.setTranslateX(0);
                    notification.toFront();
                }
            });
        }
    }

}
