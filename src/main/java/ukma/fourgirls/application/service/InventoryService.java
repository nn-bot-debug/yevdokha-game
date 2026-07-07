package ukma.fourgirls.application.service;

import ukma.fourgirls.domain.model.Item;
import ukma.fourgirls.state.GameSession;

public class InventoryService {
    private final GameSession session;

    public InventoryService(GameSession session) {
        this.session = session;
    }

    public void pickUpItem(Item item) {
        session.addItem(item);
    }
}