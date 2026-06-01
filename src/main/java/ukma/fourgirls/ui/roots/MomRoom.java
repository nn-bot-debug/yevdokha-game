package ukma.fourgirls.ui.roots;

import ukma.fourgirls.NavigationPanel;
import ukma.fourgirls.SceneManager;

public class MomRoom extends Place {
    private static final String IMAGE_PATH = "/images/mother_room.png";

    public MomRoom() {
        super(IMAGE_PATH);
        NavigationPanel navPanel = new NavigationPanel();
        navPanel.addNavigationTarget("Дитяча кімната", () ->
                SceneManager.getInstance().switchToCachedRoom("ChildRoom", () -> new ChildRoom().getRoot())
        );
        navPanel.addNavigationTarget("Кухня", () ->
                SceneManager.getInstance().switchToCachedRoom("Kitchen", () -> new Kitchen().getRoot())
        );
        navPanel.addNavigationTarget("Вийти на вулицю", () -> {});
        navPanel.attachTo(this.root);
    }
}