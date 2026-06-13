package ukma.fourgirls.ui.roots;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class Forest extends Place{
    private static final String NORMAL_FOREST = "/images/forest.png";
    private static final String MAGIC_FOREST = "/images/image-forest.png";
    private CharacterView actorView;
    private CharacterView lisovukView;

    public Forest() {
        super(NORMAL_FOREST);
    }

    @Override
    public void onEnter() {
        CameraController.setPanningEnabled(true);
        this.startForestMeetingCutscene();
    }

    private void startForestMeetingCutscene() {
        actorView = new CharacterView((StackPane) this.getRoot());
        lisovukView = new CharacterView((StackPane) this.getRoot());

        Map<String, Runnable> actions = new HashMap<>();

        actions.put("showSadYevdokha", () -> {
            if (lisovukView != null) lisovukView.hide();
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/Zasmuchena_evdoha.png");
            }
        });

        actions.put("showLisovuk", () -> {
            if (actorView != null) actorView.hide();
            if (lisovukView != null) {
                lisovukView.setPositionSide(false);
                lisovukView.setCharacterSprite("/images/Lisovuk.png");
            }
        });

        actions.put("hideActor", () -> {
            if (actorView != null) actorView.hide();
            if (lisovukView != null) lisovukView.hide();
        });

        actions.put("show_feature_tutorial", () -> {
            ukma.fourgirls.ui.views.TutorialOverlay tutorial = new ukma.fourgirls.ui.views.TutorialOverlay((StackPane) this.getRoot());
            ((StackPane) this.getRoot()).getChildren().add(tutorial.getRoot());
        });

        actions.put("enable_eye_feature_button", () -> {
            Button eyeButton = new Button();
            eyeButton.getStyleClass().add("eye-feature-button");

            StackPane.setAlignment(eyeButton, Pos.TOP_RIGHT);
            StackPane.setMargin(eyeButton, new javafx.geometry.Insets(20, 20, 0, 0));

            eyeButton.setOnAction(e -> {
                ((StackPane) this.getRoot()).getChildren().remove(eyeButton);

                this.setBackground(MAGIC_FOREST);

                StoryRunner.playScene("/story/chapter2.json", "forest_meeting", (StackPane) this.getRoot(), actions, null);
            });

            ((StackPane) this.getRoot()).getChildren().add(eyeButton);
        });

        StoryRunner.playScene("/story/chapter2.json", "forest_intro_scene", (StackPane) this.getRoot(), actions, null);
    }

    public void enableNavigation() {
        CameraController.setPanningEnabled(true);
        this.setupNavigation("Forest");
    }
}
