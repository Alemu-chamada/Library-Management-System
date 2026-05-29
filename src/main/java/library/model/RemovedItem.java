package library.model;

import java.time.LocalDateTime;

/**
 * Keeps a record of deleted books or users for audit purposes.
 * Ensures we maintain a history of items even after they are removed.
 */
public class RemovedItem {

    private String itemType;
    private String itemId;
    private String itemName;
    private LocalDateTime removedAt;

    public RemovedItem(String itemType, String itemId,
                       String itemName, LocalDateTime removedAt) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.itemName = itemName;
        this.removedAt = removedAt;
    }

    public String getItemType() { return itemType; }
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public LocalDateTime getRemovedAt() { return removedAt; }

    @Override
    public String toString() {
        return String.format("Removed[%s %s '%s' on %s]",
                itemType, itemId, itemName, removedAt.toString());
    }
}
