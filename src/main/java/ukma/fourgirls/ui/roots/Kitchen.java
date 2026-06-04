package ukma.fourgirls.ui.roots;

import ukma.fourgirls.ui.NavigationPanel;
import ukma.fourgirls.core.SceneManager;

public class Kitchen extends Place {
    private static final String IMAGE_PATH = "/images/kitchen.png";

    public Kitchen() {
        super(IMAGE_PATH);
        setupNavigation("Kitchen");
    }
}