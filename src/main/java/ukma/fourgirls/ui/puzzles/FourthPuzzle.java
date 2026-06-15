package ukma.fourgirls.ui.puzzles;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class FourthPuzzle extends StackPane {

    private static final int SIZE = 10;
    private static final int MAX_LIVES = 3;

    private static final String CSS_PATH = Objects.requireNonNull(
            FourthPuzzle.class.getResource("/css/grid_puzzle.css")
    ).toExternalForm();

    private static final int[][] MAP = {
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    private final boolean[][] solution = new boolean[SIZE][SIZE];
    private final boolean[][] playerGrid = new boolean[SIZE][SIZE];
    private final Button[][]  cells = new Button[SIZE][SIZE];
    private int lives = MAX_LIVES;
    private boolean gameOver = false;

    private Text titleText;
    private Text livesText;
    private GridPane gridPane;

    private Consumer<Integer> onPuzzleSolved;

    public FourthPuzzle() {
        buildSolution();
        buildUI();
    }

    private void buildSolution() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                solution[r][c] = (MAP[r][c] == 1);
    }

    private void buildUI() {
        this.setOnMouseClicked(Event::consume);
        this.setOnMousePressed(Event::consume);
        this.setOnMouseReleased(Event::consume);

        this.getStylesheets().add(CSS_PATH);
        this.getStyleClass().add("grid-puzzle-overlay");
        this.setAlignment(Pos.CENTER);

        titleText = new Text("Японський кросворд\nЗафарбуйте клітинки, щоб намалювати ламану лінію");
        titleText.getStyleClass().add("grid-title");
        titleText.setTextAlignment(TextAlignment.CENTER);

        HBox titleBox = new HBox(titleText);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getStyleClass().add("grid-title-container");

        livesText = new Text();
        livesText.getStyleClass().add("grid-lives");
        refreshLives();

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(3);
        gridPane.setVgap(3);
        buildGrid();

        Label legend = new Label("✅ — правильна клітинка   ❌ — помилка");
        legend.getStyleClass().add("grid-legend");

        VBox card = new VBox(18, titleBox, livesText, gridPane, legend);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("grid-card");

        this.getChildren().add(card);
    }

    private void buildGrid() {
        gridPane.getChildren().clear();
        gridPane.add(new Region(), 0, 0);

        for (int c = 0; c < SIZE; c++) {
            Label hint = hintLabel(colHint(c));
            hint.getStyleClass().add("grid-hint-col");
            gridPane.add(hint, c + 1, 0);
        }

        for (int r = 0; r < SIZE; r++) {
            Label hint = hintLabel(rowHint(r));
            hint.getStyleClass().add("grid-hint-row");
            gridPane.add(hint, 0, r + 1);

            for (int c = 0; c < SIZE; c++) {
                Button btn = new Button();
                btn.getStyleClass().add("grid-cell");
                final int fr = r, fc = c;
                btn.setOnAction(e -> onCellClick(fr, fc, btn));
                cells[r][c] = btn;
                gridPane.add(btn, c + 1, r + 1);
            }
        }
    }

    private static Label hintLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("grid-hint-label");
        return l;
    }

    private String rowHint(int r) {
        return blocksToString(blocks(r, true), " ");
    }

    private String colHint(int c) {
        return blocksToString(blocks(c, false), "\n");
    }

    private List<Integer> blocks(int idx, boolean isRow) {
        List<Integer> result = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            boolean filled = isRow ? solution[idx][i] : solution[i][idx];
            if (filled) {
                count++;
            } else if (count > 0) {
                result.add(count);
                count = 0;
            }
        }
        if (count > 0) result.add(count);
        if (result.isEmpty()) result.add(0);
        return result;
    }

    private static String blocksToString(List<Integer> blocks, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(blocks.get(i));
        }
        return sb.toString();
    }

    private void onCellClick(int r, int c, Button btn) {
        if (gameOver) return;
        if (playerGrid[r][c]) return;

        playerGrid[r][c] = true;

        if (solution[r][c]) {
            btn.getStyleClass().add("grid-cell-filled");
            checkWin();
        } else {
            lives--;
            refreshLives();
            btn.getStyleClass().add("grid-cell-wrong");
            btn.setDisable(true);
            shakeAndMaybeEnd();
        }
    }

    private void checkWin() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (solution[r][c] && !playerGrid[r][c]) return;

        gameOver = true;
        this.setMouseTransparent(true);
        titleText.setText("Ламану лінію намальовано! Шлях відкрито!");

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> finish());
        pause.play();
    }

    private void shakeAndMaybeEnd() {
        this.setDisable(true);

        boolean isGameOver = (lives <= 0);

        if (isGameOver) {
            gameOver = true;
            titleText.setText("Життя вичерпано! Шлях залишається закритим…");
        } else {
            titleText.setText("Хибний вибір! Залишилось " + lives + (lives == 1 ? " життя." : " життів."));
        }

        TranslateTransition shake = new TranslateTransition(Duration.millis(45), gridPane);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.setOnFinished(e -> {
            gridPane.setTranslateX(0);

            if (isGameOver) {
                // невелика пауза — потім завершуємо
                PauseTransition wait = new PauseTransition(Duration.seconds(2));
                wait.setOnFinished(ev -> finish());
                wait.play();
            } else {
                this.setDisable(false);
                titleText.setText("Японський кросворд\nЗафарбуйте клітинки, щоб намалювати ламану лінію");
            }
        });
        shake.play();
    }

    private void finish() {
        // розрахунок нагороди:
        // 0 життів → -1 подих лісу
        // 1 життя  →  0 подихів лісу
        // 2 життя  → +1 подих лісу
        // (3 життя → +1, якщо колись знадобиться)
        int reward = lives - 1;   // 0→-1, 1→0, 2→1, 3→2

        var parent = this.getParent();
        if (parent instanceof StackPane sp) sp.getChildren().remove(this);
        if (onPuzzleSolved != null) onPuzzleSolved.accept(reward);
    }

    private void refreshLives() {
        StringBuilder sb = new StringBuilder("Життя: ");
        for (int i = 0; i < MAX_LIVES; i++)
            sb.append(i < lives ? "❤️" : "🖤");
        livesText.setText(sb.toString());
    }

    public void setOnPuzzleSolved(Consumer<Integer> callback) {
        this.onPuzzleSolved = callback;
    }
}
