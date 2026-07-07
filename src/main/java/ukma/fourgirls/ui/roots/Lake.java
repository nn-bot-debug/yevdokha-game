package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.GameContext;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.ui.CharacterView;
import ukma.fourgirls.ui.puzzles.SeventhPuzzle;
import ukma.fourgirls.ui.puzzles.SixthPuzzle;

import java.util.HashMap;
import java.util.Map;

public class Lake extends Place {

        private static final String LAKE = "/images/canvas/Lake.png";
        private static final String LAKE_MAVKY = "/images/canvas/Lake-Mavky.png";
        private static final String LAKE_DINNER = "/images/canvas/mavky-dinner-scene.png";
    private static final String LAKE_LUKYAN = "/images/canvas/with_lukyan.png";

        private final Rectangle blackOverlay;
        private CharacterView actorView;
        private CharacterView mavkaView;
        private final Map<String, Runnable> actions;
        private final GameSession session;

        public Lake(GameContext context) {
            super(LAKE, context);
            this.session = context.getSession();
            if (getClass().getResource("/css/settings.css") != null) {
                this.getRoot().getStylesheets().add(getClass().getResource("/css/settings.css").toExternalForm());
            }
            blackOverlay = new Rectangle();
            blackOverlay.widthProperty().bind(this.root.widthProperty());
            blackOverlay.heightProperty().bind(this.root.heightProperty());
            blackOverlay.setFill(Color.BLACK);
            blackOverlay.setOpacity(1.0);
            blackOverlay.setMouseTransparent(true);
            this.root.getChildren().add(blackOverlay);

            actorView = new CharacterView((StackPane) this.getRoot());
            mavkaView = new CharacterView((StackPane) this.getRoot());

            actions = new HashMap<>();
        }

        @Override
        public void onEnter() {
            context.getCamera().setPanningEnabled(true);
            this.setBackground(LAKE);

            actions.clear();

            actions.put("hideActor", () -> {
                if (actorView != null) actorView.hide();
                if (mavkaView != null) mavkaView.hide();
            });

            actions.put("showScaredYevdokha", () -> {
                if (mavkaView != null) mavkaView.hide();
                if (actorView != null) {
                    actorView.setPositionSide(true);
                    actorView.setCharacterSprite("/images/characters/scaredYevdokhaFull.png");
                }
            });

            actions.put("showMavka", () -> {
                if (actorView != null) actorView.hide();
                if (mavkaView != null) {
                    mavkaView.setPositionSide(false);
                    mavkaView.setCharacterSprite("/images/characters/Mavka.png");
                }
            });

            actions.put("showScaryMavka", () -> {
                if (actorView != null) actorView.hide();
                if (mavkaView != null) {
                    mavkaView.setPositionSide(false);
                    mavkaView.setCharacterSprite("/images/characters/scary-mavka.png");
                }
            });

            actions.put("dinner-scene", () ->{
                this.setBackground(LAKE_DINNER);
                StoryRunner.playScene(context, "/story/chapter3.json", "mavka-dinner-scene", (StackPane) this.getRoot(), actions, null);
            });

            actions.put("play-melodia-sound", () -> {
                context.getAudio().playBackgroundMusic("/music/Lake-scene-melody.mp3");
            });

            actions.put("start", () ->{
                this.setBackground(LAKE_MAVKY);});

            actions.put("enable-lake-eye-button", () -> {
                Button eyeButton = new Button();
                eyeButton.getStyleClass().add("eye-feature-button");
                StackPane.setAlignment(eyeButton, Pos.TOP_RIGHT);
                StackPane.setMargin(eyeButton, new javafx.geometry.Insets(20, 20, 0, 0));

                eyeButton.setOnAction(event -> {
                    ((StackPane) this.getRoot()).getChildren().remove(eyeButton);
                    StoryRunner.playScene(context, "/story/chapter3.json", "mavky-meeting-scene", (StackPane) this.getRoot(), actions, null);
                });
                ((StackPane) this.getRoot()).getChildren().add(eyeButton);
                eyeButton.toFront();
            });

            actions.put("start_pendant_puzzle", () -> {
                ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof SixthPuzzle);

                SixthPuzzle puzzle = new SixthPuzzle(context, (isWin) -> {
                    ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof SixthPuzzle);

                    System.out.println("Головоломку кулона завершено! Результат успіху: " + isWin);

                    StoryRunner.playScene(context, "/story/chapter3.json", "mavka-dinner-scene", (StackPane) this.getRoot(), actions, null);
                });

                ((StackPane) this.getRoot()).getChildren().add(puzzle);
                StackPane.setAlignment(puzzle, Pos.CENTER);
                puzzle.toFront();
            });

            actions.put("trigger_bad_end_part2", () -> {
                StoryRunner.playScene(context, "/story/theend.json", "bad-end-part2-water", (StackPane) this.getRoot(), actions, null);
            });

            actions.put("showFather", () -> {
                if (mavkaView != null) mavkaView.hide();
                if (actorView != null) {
                    actorView.setPositionSide(false);
                    actorView.setCharacterSprite("/images/characters/lukyan.png");
                }
            });

            actions.put("set_bg_lake_clean", () -> {
                this.setBackground(LAKE);
            });

            actions.put("set_bg_with_lukyan", () -> {
                this.setBackground(LAKE_LUKYAN);
            });

            actions.put("set_bg_mavka_dinner", () -> {
                this.setBackground(LAKE_DINNER);
            });

            actions.put("play_video_ending_1", () -> {
                playEndingVideo("/video/end_1.mp4");
            });

            actions.put("play_video_ending_2", () -> {
                playEndingVideo("/video/end_2.mp4");
            });

            actions.put("start_scale_puzzle", () -> {
                ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof SeventhPuzzle);

                SeventhPuzzle scalePuzzle = new SeventhPuzzle(context, (karmaReward) -> {
                    ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof SeventhPuzzle);
                    System.out.println("Головоломку ваг завершено! Отримано карми: " + karmaReward);

                    int finalKarma = session.getKarmaBalance();
                    System.out.println("Фінальна кількість подихів лісу: " + finalKarma);

                    if (finalKarma < 7) {
                        boolean putDrawingOnScale = session.getInventoryItems().stream()
                                .noneMatch(item -> item.getName().equals("Малюнок"));

                        if (putDrawingOnScale) {
                            StoryRunner.playScene(context, "/story/theend.json", "bad-end-part1-with-drawing", (StackPane) this.getRoot(), actions, null);
                        } else {
                            StoryRunner.playScene(context, "/story/theend.json", "bad-end-part2-water", (StackPane) this.getRoot(), actions, null);
                        }
                    } else {
                        StoryRunner.playScene(context, "/story/theend.json", "good-ending", (StackPane) this.getRoot(), actions, null);
                    }
                });

                ((StackPane) this.getRoot()).getChildren().add(scalePuzzle);
                StackPane.setAlignment(scalePuzzle, Pos.CENTER);
                scalePuzzle.toFront();
            });
            actions.put("play_laugh", () -> {
                context.getAudio().buttonSound("/music/mavka_smih.mp3");
            });
            StoryRunner.playScene(context, "/story/chapter3.json", "lake-meeting-scene", (StackPane) this.getRoot(), actions, null);
            playFadeIn();
        }

    private void playFadeIn() {
            blackOverlay.setOpacity(1.0);
            blackOverlay.toFront();
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.0), blackOverlay);
            fadeIn.setFromValue(1.0);
            fadeIn.setToValue(0.0);

            fadeIn.setOnFinished(e -> blackOverlay.setMouseTransparent(true));
            fadeIn.play();
        }

    private void playEndingVideo(String videoPath) {
        try {
            var resource = getClass().getResource(videoPath);
            if (resource == null) {
                System.err.println("Помилка: Файл відео не знайдено за шляхом " + videoPath);
                return;
            }
            String source = resource.toExternalForm();

            javafx.scene.media.Media media = new javafx.scene.media.Media(source);
            javafx.scene.media.MediaPlayer mediaPlayer = new javafx.scene.media.MediaPlayer(media);
            javafx.scene.media.MediaView mediaView = new javafx.scene.media.MediaView(mediaPlayer);

            StackPane rootPane = (StackPane) this.getRoot();

            Rectangle videoBackground = new Rectangle();
            videoBackground.widthProperty().bind(rootPane.widthProperty());
            videoBackground.heightProperty().bind(rootPane.heightProperty());
            videoBackground.setFill(Color.BLACK);
            videoBackground.setOpacity(1.0);

            mediaView.fitWidthProperty().bind(rootPane.widthProperty());
            mediaView.fitHeightProperty().bind(rootPane.heightProperty());
            mediaView.setPreserveRatio(true);

            context.getAudio().fadeOutBackgroundMusic(1.0);

            rootPane.getChildren().addAll(videoBackground, mediaView);
            videoBackground.toFront();
            mediaView.toFront();

            mediaPlayer.play();

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                rootPane.getChildren().removeAll(videoBackground, mediaView);

                System.out.println("Відео завершилось. Повертаємо гравця в меню.");
                context.getScene().resetSession();
                context.getScene().switchToMainMenu();
            });

        } catch (Exception e) {
            System.err.println("Критична помилка ініціалізації відеоплеєра JavaFX:");
            e.printStackTrace();
        }
    }
}