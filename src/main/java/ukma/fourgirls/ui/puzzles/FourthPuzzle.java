package ukma.fourgirls.ui.puzzles;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class FourthPuzzle extends StackPane {

    private Consumer<Integer> onPuzzleSolved;
    private static final int SIZE = 10;
    private int lives = 3;

    private static final String CSS_PATH = Objects.requireNonNull(
            FourthPuzzle.class.getResource("/css/grid_puzzle.css")
    ).toExternalForm();

    private final boolean[][] solution = new boolean[SIZE][SIZE]; // Справжній малюнок
    private final boolean[][] playerGrid = new boolean[SIZE][SIZE]; // Те, що зафарбував гравець
    private final Button[][] buttons = new Button[SIZE][SIZE];

    private Text titleText;
    private Text livesText;
    private GridPane gridPane;

    public FourthPuzzle() {
        setupUI();
        generateLevel();
    }

    private void setupUI() {
        this.setOnMouseClicked(Event::consume);
        this.setOnMousePressed(Event::consume);
        this.setOnMouseReleased(Event::consume);

        this.getStylesheets().add(CSS_PATH);
        this.getStyleClass().add("grid-puzzle-overlay");
        this.setAlignment(Pos.CENTER);

        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);

        titleText = new Text("Японський кросворд. Розгадайте малюнок коріння, щоб звільнити шлях.\nЦифри означають довжину блоків зафарбованих клітинок підряд.");
        titleText.getStyleClass().add("grid-title");
        titleText.setTextAlignment(TextAlignment.CENTER);

        livesText = new Text("Життя: ❤️❤️❤️");
        livesText.getStyleClass().add("grid-lives");

        HBox titleWrapper = new HBox(titleText);
        titleWrapper.setAlignment(Pos.CENTER);
        titleWrapper.getStyleClass().add("grid-title-container");
        titleWrapper.setMaxWidth(Region.USE_PREF_SIZE);

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(4);
        gridPane.setVgap(4);

        mainContainer.getChildren().addAll(titleWrapper, livesText, gridPane);
        this.getChildren().add(mainContainer);
    }

    private void updateLivesDisplay() {
        StringBuilder sb = new StringBuilder("Життя: ");
        for (int i = 0; i < 3; i++) {
            if (i < lives) sb.append("❤️");
            else sb.append("🖤");
        }
        livesText.setText(sb.toString());
    }

    private void generateLevel() {
        gridPane.getChildren().clear();

        int[][] mapLayout = {
                {0, 0, 1, 0, 0, 1, 1, 0, 0, 0},
                {1, 0, 0, 0, 1, 0, 0, 0, 1, 0},
                {0, 1, 1, 0, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 1},
                {1, 1, 0, 1, 1, 1, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 1, 1, 1, 1, 0},
                {1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                solution[r][c] = (mapLayout[r][c] == 1);
                playerGrid[r][c] = false;
            }
        }

        for (int r = 0; r <= SIZE; r++) {
            for (int c = 0; c <= SIZE; c++) {
                if (r == 0 && c == 0) {
                    gridPane.add(new Region(), c, r);
                } else if (r == 0) {
                    String colHintText = getNonogramHintForCol(c - 1);
                    Label hint = new Label(colHintText);
                    hint.getStyleClass().add("grid-hint-label");
                    hint.setStyle("-fx-alignment: bottom-center; -fx-text-alignment: center;"); // Щоб красиво вертикально лягало
                    gridPane.add(hint, c, r);
                } else if (c == 0) {
                    String rowHintText = getNonogramHintForRow(r - 1);
                    Label hint = new Label(rowHintText);
                    hint.getStyleClass().add("grid-hint-label");
                    gridPane.add(hint, c, r);
                } else {
                    int finalR = r - 1;
                    int finalC = c - 1;
                    Button cell = new Button();
                    cell.getStyleClass().add("grid-cell");

                    cell.setOnAction(e -> handleCellClick(finalR, finalC, cell));
                    buttons[finalR][finalC] = cell;
                    gridPane.add(cell, c, r);
                }
            }
        }
    }

    private String getNonogramHintForRow(int r) {
        List<Integer> blocks = new ArrayList<>();
        int count = 0;
        for (int c = 0; c < SIZE; c++) {
            if (solution[r][c]) {
                count++;
            } else if (count > 0) {
                blocks.add(count);
                count = 0;
            }
        }
        if (count > 0) blocks.add(count);

        if (blocks.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            sb.append(blocks.get(i));
            if (i < blocks.size() - 1) sb.append(" ");
        }
        return sb.toString();
    }

    private String getNonogramHintForCol(int c) {
        List<Integer> blocks = new ArrayList<>();
        int count = 0;
        for (int r = 0; r < SIZE; r++) {
            if (solution[r][c]) {
                count++;
            } else if (count > 0) {
                blocks.add(count);
                count = 0;
            }
        }
        if (count > 0) blocks.add(count);

        if (blocks.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            sb.append(blocks.get(i));
            if (i < blocks.size() - 1) sb.append("\n"); // Вертикальний перенос цифр!
        }
        return sb.toString();
    }

    private void handleCellClick(int r, int c, Button cell) {
        if (lives <= 0) return;

        if (playerGrid[r][c]) {
            playerGrid[r][c] = false;
            cell.getStyleClass().remove("grid-cell-safe");
            return;
        }

        if (solution[r][c]) {
            cell.getStyleClass().add("grid-cell-safe");
            playerGrid[r][c] = true;
            checkWinCondition();
        } else {
            cell.getStyleClass().add("grid-cell-exploded");
            lives--;
            updateLivesDisplay();
            triggerExplosionShake(cell);
        }
    }

    private void triggerExplosionShake(Button faultyCell) {
        this.setDisable(true);
        if (lives > 0) {
            titleText.setText("Хибний вибір! Коріння чинить опір.");
        } else {
            titleText.setText("Життя вичерпано! Спробуйте пройти кросворд заново.");
        }

        TranslateTransition shake = new TranslateTransition(Duration.millis(50), gridPane);
        shake.setFromX(0);
        shake.setByX(8);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.setOnFinished(e -> {
            gridPane.setTranslateX(0);
            this.setDisable(false);

            if (lives > 0) {
                titleText.setText("Японський кросворд. Розгадайте малюнок коріння, щоб звільнить шлях.");
                faultyCell.getStyleClass().remove("grid-cell-exploded");
            } else {
                PauseTransition failPause = new PauseTransition(Duration.seconds(2));
                failPause.setOnFinished(ev -> handleGameOver());
                failPause.play();
            }
        });
        shake.play();
    }

    private void handleGameOver() {
        this.lives = 3;
        updateLivesDisplay();
        titleText.setText("Японський кросворд. Розгадайте малюнок коріння, щоб звільнить шлях.");

        // Повністю очищуємо ігрове поле для нової спроби
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                playerGrid[r][c] = false;
                buttons[r][c].getStyleClass().removeAll("grid-cell-safe", "grid-cell-exploded");
            }
        }
    }

    private void checkWinCondition() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (solution[r][c] != playerGrid[r][c]) {
                    return;
                }
            }
        }

        this.setMouseTransparent(true);
        titleText.setText("Малюнок розгадано! Маленька мурашка врятована!");
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> handleSuccess());
        pause.play();
    }

    public void setOnPuzzleSolved(Consumer<Integer> onPuzzleSolved) {
        this.onPuzzleSolved = onPuzzleSolved;
    }

    private void handleSuccess() {
        var parent = this.getParent();
        if (parent instanceof StackPane stackPane) {
            stackPane.getChildren().remove(this);
        }
        if (onPuzzleSolved != null) {
            onPuzzleSolved.accept(Math.max(0, lives));
        }
    }
}