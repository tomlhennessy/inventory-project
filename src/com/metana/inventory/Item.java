package com.metana.inventory;

/**
 * Represents an item or product in the inventory.
 * Contains item details such as unique ID, name, and quantity.
 */

public class Item {
  private String itemId;    // unique identifier for the item (e.g., SKU)
  private String name;      // human-readable name of the item
  private int quantity;     // quantity of this item (stock count)

  // Constructor
  public Item(String itemId, String name, int quantity) {
    this.itemId = itemId;
    this.name = name;
    this.quantity = quantity;
  }

  // Getters and setters (to be implemented as needed)
  public String getItemId() {
    return itemId;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  // toString() method for easy printing
  @Override
  public String toString() {
    return "Item[" + "id=" + itemId + ", name=" + name + ", qty=" + quantity + "]";
  }

}
