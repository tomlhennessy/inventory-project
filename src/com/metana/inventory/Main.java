package com.metana.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class - entry point of the application.
 * Initializes the system and starts the inventory management process.
 */

public class Main {
  public static void main(String[] args) {
    // TODO: In a future iteration, initialize InventoryManager, warehouses, etc.
    // For now, just print a welcome message to verify the setup.
    System.out.println("Welcome to the Multi-Warehouse Inventory System");

    InventoryManager inv = new InventoryManager();
    RouteOptimizer rOpt = new RouteOptimizer();
    OrderProcessor op = new OrderProcessor(inv, rOpt);

    // Create and register warehouses
    Warehouse w1 = new Warehouse("W1");
    Warehouse w2 = new Warehouse("W2");
    inv.addWarehouse(w1);
    inv.addWarehouse(w2);

    // Add some stock to each warehouse
    w1.addStock("P001", 20, "B001", LocalDate.parse("2025-12-31"));
    w2.addStock("POO1", 50, "B002", LocalDate.parse("2025-06-30"));

    // Create an order for 70 units of P001
    Map<String, Integer> items = new HashMap<>();
    items.put("P001", 70);
    Order order = new Order("0001", items, "CustomerLocation");

    // Process the order
    op.processOrder(order);

  }
}
