package com.metana.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Handles processing of orders using available inventory.
 * Determines how to fulfill an order from one or multiple warehouses.
 */
public class OrderProcessor {
  private InventoryManager inventoryManager;
  private RouteOptimizer routeOptimizer;

  public OrderProcessor(InventoryManager inventoryManager, RouteOptimizer routeOptimizer) {
    this.inventoryManager = inventoryManager;
    this.routeOptimizer = routeOptimizer;
  }

  // Process an order: allocate items from warehouses and initiate delivery
  public void processOrder(Order order) {
    // Up front availability check
    for (Map.Entry<String,Integer> e : order.getItems().entrySet()) {
      if (inventoryManager.getTotalStock(e.getKey()) < e.getValue()) {
        System.out.println("Not enough stock for Order " + order.getOrderId());
        return;
      }
    }
    // Allocate greedily by nearest warehouse, while each warehouse consumes FEFO batches
    List<Warehouse> warehousesUsed = new ArrayList<>();

    // Iterate over each item in the order (product ID -> quantity needed)
    for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
      String productId = entry.getKey();
      int needed = entry.getValue();

      // collect candidates that actually have stock
      List<Warehouse> candidates = new ArrayList<>();
      for (Warehouse w : inventoryManager.getWarehouses()) {
        if (w.getAvailableStock(productId) > 0) candidates.add(w);
      }
      // sort by Euclidean distance to customer
      candidates.sort(Comparator.comparingDouble(
        w -> distance(w.getX(), w.getY(), order.getCustomerX(), order.getCustomerY())
      ));

      for (Warehouse w : candidates) {
        if (needed <= 0) break;
        int avail = w.getAvailableStock(productId);
        if (avail <= 0) continue;
        int toTake = Math.min(needed, avail);

        // FEFO happens inside this call, and we get back exact batches used
        List<Warehouse.BatchAllocation> used = w.removeStock(productId, toTake);
        if (!used.isEmpty() && !warehousesUsed.contains(w)) { warehousesUsed.add(w); }

        // Print per-batch allocations
        for (Warehouse.BatchAllocation ba : used) {
          System.out.println("- " + w.getWarehouseId() + ": " + ba.productId + ", " + ba.quantityTaken + " (" + ba.batchId + ")");
        }

        // reduce remaining need
        int actuallyTaken = used.stream().mapToInt(b -> b.quantityTaken).sum();
        needed -= actuallyTaken;
      }

      if (needed > 0) {
        System.out.println("Order " + order.getOrderId() + ": Could not fully fulfill item " + productId + " (short by " + needed + " units).");
        return;
      }
    }
        // Plan the delivery route from the used warehouses to the order's destination
    if (!warehousesUsed.isEmpty()) {
      routeOptimizer.planRoute(order.getDestination(), warehousesUsed);
    }
  }

  private double distance(int x1, int y1, int x2, int y2) {
    double dx = x1 - x2, dy = y1 = y2;
    return Math.sqrt(dx*dx + dy*dy);
  }
}
