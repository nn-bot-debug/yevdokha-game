package ukma.fourgirls.domain.story;

import java.util.List;

public class StoryEvent {
    public String type;
    public String character;
    public String portrait;
    public List<String> text;
    public String actionId;
    public String prompt;
    public List<ChoiceOption> options;
}