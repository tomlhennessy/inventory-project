package com.metana.inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
  private List<Warehouse> warehouses;

  public InventoryManager() {
    this.warehouses = new ArrayList<>();
  }

  public void addWarehouse(Warehouse warehouse) {
    warehouses.add(warehouse);
  }

  public Warehouse getWarehouseById(String warehouseId) {
    for (Warehouse w : warehouses) {
      if (w.getWarehouseId().equals(warehouseId)) {
        return w;
      }
    }
    return null;
  }

  public List<Warehouse> getWarehouses() {
    return warehouses;
  }

  // Sum non-expired stock across all warehouses
  public long getTotalStock(String productId) {
    long sum = 0;
    for (Warehouse w : warehouses) {
      sum += w.getAvailableStock(productId);
    }
    return sum;
  }

  // Depot W0 (0,0) as per assignment
  public int[] getW0Coordinates() {
    return new int[]{0, 0};
  }
}
