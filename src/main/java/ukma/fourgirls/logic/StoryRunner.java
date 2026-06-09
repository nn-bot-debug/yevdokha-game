package ukma.fourgirls.logic;

import com.google.gson.Gson;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import ukma.fourgirls.core.ChoiceManager;
import ukma.fourgirls.domain.story.ChoiceOption;
import ukma.fourgirls.domain.story.StoryData;
import ukma.fourgirls.domain.story.StoryEvent;

import javafx.animation.Animation;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryRunner {

    public static void playScene(String jsonFilePath, String sceneId, StackPane roomRoot, Map<String, Runnable> actions, Map<String, Animation> animations) {

        InputStream inputStream = StoryRunner.class.getResourceAsStream(jsonFilePath);
        if (inputStream == null) {
            System.err.println("Помилка: Файл " + jsonFilePath + " не знайдено!");
            return;
        }

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        Gson gson = new Gson();
        StoryData storyData = gson.fromJson(reader, StoryData.class);

        List<StoryEvent> events = storyData.scenes.get(sceneId);
        if (events == null) {
            System.err.println("Помилка: Сцену " + sceneId + " не знайдено у файлі!");
            return;
        }

        StorySequence sequence = StorySequence.create(roomRoot);

        for (StoryEvent event : events) {
            switch (event.type) {

                case "dialogue":
                    if (event.character != null && event.portrait != null) {
                        Image portrait = new Image(Objects.requireNonNull(StoryRunner.class.getResourceAsStream(event.portrait)));
                        sequence.addDialogue(event.character, portrait, event.text.toArray(new String[0]));
                    } else {
                        sequence.addDialogue(event.text.toArray(new String[0]));
                    }
                    break;

                case "action":
                    if (actions.containsKey(event.actionId)) {
                        sequence.execute(actions.get(event.actionId));
                    } else {
                        System.err.println("Увага: Дію " + event.actionId + " не знайдено в коді Java!");
                    }
                    break;

                case "animation":
                    if (animations != null && animations.containsKey(event.actionId)) {
                        sequence.addAnimation(animations.get(event.actionId));
                    }
                    break;

                case "choice":
                    sequence.execute(() -> {
                        ChoiceManager.Option[] choiceOptions = new ChoiceManager.Option[event.options.size()];

                        for (int i = 0; i < event.options.size(); i++) {
                            ChoiceOption opt = event.options.get(i);
                            choiceOptions[i] = new ChoiceManager.Option(opt.text, actions.get(opt.actionId));
                        }

                        ChoiceManager.show(roomRoot, event.prompt, choiceOptions);
                    });
                    break;
            }
        }

        sequence.play();
    }
}