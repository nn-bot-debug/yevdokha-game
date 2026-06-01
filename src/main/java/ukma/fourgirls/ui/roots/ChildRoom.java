package ukma.fourgirls.ui.roots;

import ukma.fourgirls.NavigationPanel;
import ukma.fourgirls.SceneManager;


public class ChildRoom extends Place {
    private static final String IMAGE_PATH = "/images/Yevdokha_room.png";

    public ChildRoom() {
        super(IMAGE_PATH);
        NavigationPanel navPanel = new NavigationPanel();
        navPanel.addNavigationTarget("Кімната матері", () ->
                SceneManager.getInstance().switchToCachedRoom("MomRoom", () -> new MomRoom().getRoot())
        );
        navPanel.addNavigationTarget("Кухня", () ->
                SceneManager.getInstance().switchToCachedRoom("Kitchen", () -> new Kitchen().getRoot())
        );
        navPanel.addNavigationTarget("Вийти на вулицю", () -> {});
        navPanel.attachTo(this.root);
    }
}