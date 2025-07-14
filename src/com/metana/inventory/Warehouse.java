package com.metana.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a warehouse location that stores inventory.
 * Each warehouse has a name/location and keeps track of item stock levels.
 */

public class Warehouse {
  private String warehouseId;           // unique ID or name for the warehouse
  private String location;              // e.g., city or region name
  private Map<String, Integer> stock;   // inventory stock: itemId -> quantity available

  public Warehouse(String warehouseId, String location) {
    this.warehouseId = warehouseId;
    this.location = location;
    this.stock = new HashMap<>();
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

  public String getLocation() {
    return location;
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
