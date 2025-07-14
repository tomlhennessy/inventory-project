package com.metana.inventory;

/**
 * Handles processing of orders using available inventory.
 * Determines how to fulfill an order from one or multiple warehouses.
 */
public class OrderProcessor {
  private InventoryManager inventoryManager;
  private RouteOptimizer routeOptimizer;

  public orderProcessor(InventoryManager inventoryManager, RouteOptimizer routeOptimizer) {
    this.inventoryManager = inventoryManager;
    this.routeOptimizer = routeOptimizer;
  }

  // Process an order: allocate items from warehouses and initiate delivery
  public void processOrder(Order order) {
    // TODO: Implement logic to fulfill the order
    // e.g., check each item in the order, find a warehouse with stock, allocate it, and update inventory.
    // This might involve splitting the order across warehouses if needed.
    // After allocating items, use routeOptimizer to plan delivery routes.
  }
}
