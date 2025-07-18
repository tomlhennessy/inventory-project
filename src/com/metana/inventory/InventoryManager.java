package com.metana.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages inventory across multiple warehouses.
 * Provides methods to register warehouses and query or update stock.
 */
public class InventoryManager {
  private List<Warehouse> warehouses;

  public InventoryManager() {
    this.warehouses = new ArrayList<>();
  }

  // Register a new warehouse within the system
  public void addWarehouse(Warehouse warehouse) {
    warehouses.add(warehouse);
  }

  // find a warehouse by ID (returns null if not found)
  public Warehouse getWarehouseById(String warehouseId) {
    for (Warehouse w : warehouses) {
      if (w.getWarehouseId().equals(warehouseId)) {
        return w;
      }
    }
    return null;
  }

  // get list of warehouses
  public List<Warehouse> getWarehouses() {
    return warehouses;
  }


  // (other inventory manegement methods like transferStock, totalStock, etc. can be added later)
}
