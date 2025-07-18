package com.metana.inventory;

import java.util.ArrayList;
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
    // Keep track of which warehouses will supply items for this order
    List<Warehouse> warehousesUsed = new ArrayList<>();

    // Iterate over each item in the order (product ID -> quantity needed)
    for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
      String productId = entry.getKey();
      int quantityNeeded = entry.getValue();

      // Try to fulfill this item's quantity using the available warehouses
      for (Warehouse warehouse : inventoryManager.getWarehouses()) {
        // check how much stock this warehouse has for the product
        int available = warehouse.getAvailableStock(productId);
        if (available <= 0) {
          continue; // skip warehouses that have none of this product
        }

        if (available >= quantityNeeded) {
          // This warehouse can fill the entire required quantity
          warehouse.removeStock(productId, quantityNeeded); // allocate all from this warehouse
          warehousesUsed.add(warehouse);
          quantityNeeded = 0; // order's need for this item
          break; // exit the warehouse loop for this item
        } else {
          // warehouse can only fulfill part of the requirement
          warehouse.removeStock(productId, available); // take all available stock from this warehouse
          warehousesUsed.add(warehouse);
          quantityNeeded -= available; // reduce the remaining quantity needed
          // continue to the next warehouse fo fulfill the rest
        }
      }

      // After checking all warehouses, see if we still need more of this item
      if (quantityNeeded > 0) {
        // Not enough stock overall to fulfill the item completely
        System.out.println("Order: " + order.getOrderId() + ": Could not fully fulfill item " + productId + "(short by " + quantityNeeded + " units).");
        // might mark as backordered or take other actions in a real system
      }
    }

    // Plan the delivery route from the used warehouses to the order's destination
    if (!warehousesUsed.isEmpty()) {
      routeOptimizer.planRoute(order.getDestination(), warehousesUsed);
    }

  }
}
