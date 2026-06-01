package ukma.fourgirls.ui.roots;

import ukma.fourgirls.NavigationPanel;
import ukma.fourgirls.SceneManager;

public class Kitchen extends Place {
    private static final String IMAGE_PATH = "/images/kitchen.png";

    public Kitchen() {
        super(IMAGE_PATH);
        NavigationPanel navPanel = new NavigationPanel();
        navPanel.addNavigationTarget("Кімната матері", () ->
                SceneManager.getInstance().switchToCachedRoom("MomRoom", () -> new MomRoom().getRoot())
        );
        navPanel.addNavigationTarget("Дитяча кімната", () ->
                SceneManager.getInstance().switchToCachedRoom("ChildRoom", () -> new ChildRoom().getRoot())
        );
        navPanel.addNavigationTarget("Вийти на вулицю", () -> {});
        navPanel.attachTo(this.root);
    }
}