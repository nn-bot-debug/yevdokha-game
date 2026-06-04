package ukma.fourgirls.core;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.state.InventoryState;

public class InventoryManager {

    /**
     * @param itemNode Візуальний об'єкт у кімнаті
     * @param itemToPickUp Об'єкт даних, який потрапить в інвентар
     * @param roomRoot Контейнер кімнати (для показу сповіщення)
     * @param notificationText Текст після підняття
     * @param onSuccess Логіка, яка виконається після підняття
     */
    public static void setupPickupAction(Node itemNode, Item itemToPickUp, StackPane roomRoot, String notificationText, Runnable onSuccess) {
        if (itemNode == null) {
            System.err.println("Помилка: itemNode є null. Предмет ще не створено на екрані!");
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