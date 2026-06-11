package ukma.fourgirls.core;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.InventoryState;

public final class InventoryManager {

    private InventoryManager() {}

    /**
     * @param itemNode The visual object in the room
     * @param itemToPickUp The data object to be added to the inventory
     * @param roomRoot The room container (used for displaying notifications)
     * @param notificationText The text displayed after picking up the item
     * @param onSuccess The logic to be executed after a successful pick-up
     */
    public static void setupPickupAction(Node itemNode, Item itemToPickUp, StackPane roomRoot, String notificationText, Runnable onSuccess) {
        if (itemNode == null) {
            System.err.println("Error: itemNode is null. The item has not been created on the screen yet!");
            return;
        }
        itemNode.setOnMouseClicked(e -> {
            itemNode.setVisible(false);
            InventoryState.addItem(itemToPickUp);
            if (notificationText != null && !notificationText.isEmpty()) {
                NotificationManager.showNotification(roomRoot, notificationText);
            }
            if (onSuccess != null) {
                onSuccess.run();
            }
        });
    }
}