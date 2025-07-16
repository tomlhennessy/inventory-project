package com.metana.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Represents a warehouse location that stores inventory.
 * Each warehouse has a name/location and keeps track of item stock levels.
 */

public class Warehouse {
  private String warehouseId;           // unique ID or name for the warehouse
  private Map<String, PriorityQueue<ProductBatch>> inventoryByProduct;   // inventory stock: itemId -> min-heap of its batches

  public Warehouse(String warehouseId) {
    this.warehouseId = warehouseId;
    this.inventoryByProduct = new HashMap<>();
  }

  // Add or update stock for an item in this warehouse
  public void addItemStock(String itemId, int quantity) {
    // If item already exists, increment quantity : otherwise add new entry.
    stock.put(itemId, stock.getOrDefault(itemId, 0) + quantity);
  }

  // Get available quantity of a given item in this warehouse
  public int getStock(String itemId) {
    return stock.getOrDefault(itemId, 0);
  }

  // Getters for warehouse properties
  public String getWarehouseId() {
    return warehouseId;
  }

  // Method to remove stock when an item is shipped out
  public boolean removeItemStock(String itemId, int quantity) {
    if (stock.containsKey(itemId) && stock.get(itemId) >= quantity) {
      stock.put(itemId, stock.get(itemId) - quantity);
      return true;
    }
    return false;
  }
}
