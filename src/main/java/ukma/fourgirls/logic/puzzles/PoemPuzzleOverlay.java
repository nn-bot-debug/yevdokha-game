package ukma.fourgirls.logic.puzzles;

import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

public class PoemPuzzleOverlay extends StackPane {

    private Runnable onPuzzleSolved;

    private static final String UKR_LOW = "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя";
    private static final String UKR_UPP = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
    private static final int ALPHABET_SIZE = UKR_LOW.length();

    private static final String CSS_PATH = Objects.requireNonNull(
            PoemPuzzleOverlay.class.getResource("/css/puzzle.css")
    ).toExternalForm();

    private static final String[] SCRAMBLED_POEM = {
            "Залиш спокій справи, ходімо до лісу,",
            "Де вітер знімають ранкову завісу,",
            "Де сосни співає в зеленім гіллі,",
            "І усі чекає на м'якій землі."
    };

    private static final String[] ORIGINAL_POEM = {
            "Залиш усі справи, ходімо до лісу,",
            "Де сосни знімають ранкову завісу,",
            "Де вітер співає в зеленім гіллі,",
            "І спокій чекає на м'якій землі."
    };

    private static final int INITIAL_SHIFT = 6;
    private int currentShift = INITIAL_SHIFT;
    private boolean isPhase2 = false;
    private Label selectedWordLabel = null;

    private VBox poemContainer;
    private HBox controlPanel;
    private Text titleText;
    private HBox titleWrapper;

    private final Text line1 = new Text();
    private final Text line2 = new Text();
    private final Text line3 = new Text();
    private final Text line4 = new Text();

    public PoemPuzzleOverlay() {
        setupUI();
        updatePoemDisplay();
    }

    private void setupUI() {
        this.setOnMouseClicked(Event::consume);
        this.setOnMousePressed(Event::consume);
        this.setOnMouseReleased(Event::consume);

        this.getStylesheets().add(CSS_PATH);
        this.getStyleClass().add("puzzle-overlay");
        this.setAlignment(Pos.CENTER);

        VBox mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);

        titleText = new Text("Знайдений папірус зашифровано.\nЗсуньте символи, щоб прочитати:");
        titleText.getStyleClass().add("puzzle-title");
        titleText.setTextAlignment(TextAlignment.CENTER);

        titleWrapper = new HBox(titleText);
        titleWrapper.setAlignment(Pos.CENTER);
        titleWrapper.getStyleClass().add("title-container");
        titleWrapper.setMaxWidth(Region.USE_PREF_SIZE);

        line1.getStyleClass().add("poem-line-text");
        line2.getStyleClass().add("poem-line-text");
        line3.getStyleClass().add("poem-line-text");
        line4.getStyleClass().add("poem-line-text");

        poemContainer = new VBox(12, line1, line2, line3, line4);
        poemContainer.setAlignment(Pos.CENTER);
        poemContainer.getStyleClass().add("poem-container");
        poemContainer.setMaxWidth(750);

        Button btnLeft = new Button("◀ Зсунути на 2 вліво");
        Button btnRight = new Button("Зсунути на 2 вправо ▶");
        Button btnReset = new Button("↻ Почати заново");

        btnLeft.getStyleClass().addAll("puzzle-btn", "btn-shift");
        btnRight.getStyleClass().addAll("puzzle-btn", "btn-shift");
        btnReset.getStyleClass().addAll("puzzle-btn", "btn-reset");

        btnLeft.setOnAction(e -> {
            currentShift = (currentShift - 2 + ALPHABET_SIZE) % ALPHABET_SIZE;
            updatePoemDisplay();
            checkPhase1WinCondition();
        });

        btnRight.setOnAction(e -> {
            currentShift = (currentShift + 2) % ALPHABET_SIZE;
            updatePoemDisplay();
            checkPhase1WinCondition();
        });

        btnReset.setOnAction(e -> {
            currentShift = INITIAL_SHIFT;
            updatePoemDisplay();
        });

        controlPanel = new HBox(15, btnLeft, btnRight, btnReset);
        controlPanel.setAlignment(Pos.CENTER);

        mainContainer.getChildren().addAll(titleWrapper, poemContainer, controlPanel);
        this.getChildren().add(mainContainer);
    }

    private void updatePoemDisplay() {
        line1.setText(applyCaesarCipher(SCRAMBLED_POEM[0], currentShift));
        line2.setText(applyCaesarCipher(SCRAMBLED_POEM[1], currentShift));
        line3.setText(applyCaesarCipher(SCRAMBLED_POEM[2], currentShift));
        line4.setText(applyCaesarCipher(SCRAMBLED_POEM[3], currentShift));
    }

    private String applyCaesarCipher(String text, int shift) {
        if (shift == 0) return text;

        StringBuilder result = new StringBuilder();
        for (char character : text.toCharArray()) {
            int lowIdx = UKR_LOW.indexOf(character);
            if (lowIdx != -1) {
                int newIdx = (lowIdx + shift) % ALPHABET_SIZE;
                result.append(UKR_LOW.charAt(newIdx));
                continue;
            }

            int uppIdx = UKR_UPP.indexOf(character);
            if (uppIdx != -1) {
                int newIdx = (uppIdx + shift) % ALPHABET_SIZE;
                result.append(UKR_UPP.charAt(newIdx));
                continue;
            }
            result.append(character);
        }
        return result.toString();
    }

    private void checkPhase1WinCondition() {
        if (currentShift == 0 && !isPhase2) {
            isPhase2 = true;

            controlPanel.setVisible(false);
            controlPanel.setManaged(false);

            titleText.setText("Текст розшифровано!\nНатискайте на слова, щоб поміняти їх місцями.");

            startPhase2UI();
        }
    }

    private void startPhase2UI() {
        poemContainer.getChildren().clear();

        for (String s : SCRAMBLED_POEM) {
            HBox lineBox = new HBox(6);
            lineBox.setAlignment(Pos.CENTER);

            String[] words = s.split(" ");
            for (String word : words) {
                Label wordLabel = new Label(word);
                wordLabel.getStyleClass().add("poem-word-label");

                wordLabel.setOnMouseClicked(e -> handleWordSwap(wordLabel));
                lineBox.getChildren().add(wordLabel);
            }
            poemContainer.getChildren().add(lineBox);
        }
    }

    private void handleWordSwap(Label clickedLabel) {
        if (selectedWordLabel == null) {
            selectedWordLabel = clickedLabel;
            selectedWordLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25);");
        } else if (selectedWordLabel == clickedLabel) {
            selectedWordLabel.setStyle("");
            selectedWordLabel = null;
        } else {
            String tempText = selectedWordLabel.getText();
            selectedWordLabel.setText(clickedLabel.getText());
            clickedLabel.setText(tempText);

            selectedWordLabel.setStyle("");
            selectedWordLabel = null;

            checkPhase2WinCondition();
        }
    }

    private void checkPhase2WinCondition() {
        boolean isWin = true;

        for (int i = 0; i < poemContainer.getChildren().size(); i++) {
            HBox lineBox = (HBox) poemContainer.getChildren().get(i);
            StringBuilder currentLineText = new StringBuilder();

            for (int j = 0; j < lineBox.getChildren().size(); j++) {
                Label lbl = (Label) lineBox.getChildren().get(j);
                currentLineText.append(lbl.getText());
                if (j < lineBox.getChildren().size() - 1) {
                    currentLineText.append(" ");
                }
            }

            if (!currentLineText.toString().equals(ORIGINAL_POEM[i])) {
                isWin = false;
                break;
            }
        }

        if (isWin) {
            poemContainer.setMouseTransparent(true);

            titleText.setText("Вірш успішно відновлено!");

            titleText.getStyleClass().add("puzzle-title-success");
            titleWrapper.getStyleClass().add("title-container-success");

            controlPanel.getChildren().clear();

            Button btnFinish = new Button("Прочитано");
            btnFinish.getStyleClass().addAll("puzzle-btn", "btn-finish");
            btnFinish.setOnAction(e -> handleSuccess());

            controlPanel.getChildren().add(btnFinish);

            controlPanel.setVisible(true);
            controlPanel.setManaged(true);
        }
    }

    public void setOnPuzzleSolved(Runnable onPuzzleSolved) {
        this.onPuzzleSolved = onPuzzleSolved;
    }

    private void handleSuccess() {
        var parent = this.getParent();
        if (parent instanceof StackPane stackPane) {
            stackPane.getChildren().remove(this);
        }
        if (onPuzzleSolved != null) {
            onPuzzleSolved.run();
        }
    }
}