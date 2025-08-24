package com.metana.inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    System.out.println("Welcome to the Multi-Warehouse Inventory System");

    InventoryManager inv = new InventoryManager();
    RouteOptimizer rOpt = new RouteOptimizer();
    OrderProcessor op = new OrderProcessor(inv, rOpt);

    // Warehouses with coordinates (assignment example)
    Warehouse w1 = new Warehouse("W1", 5, 5);
    Warehouse w2 = new Warehouse("W2", 15, 15);
    inv.addWarehouse(w1);
    inv.addWarehouse(w2);

    // Stock (expiry-aware heaps)
    w1.addStock("P001", 100, "B001", LocalDate.parse("2025-12-31"));
    w2.addStock("P001", 50,  "B002", LocalDate.parse("2025-06-30")); // earlier expiry -> used first
    w1.addStock("P002", 200, "B003", LocalDate.parse("2025-12-31"));

    // Order (assignment example coords/time window)
    Map<String, Integer> items = new HashMap<>();
    items.put("P001", 70);
    items.put("P002", 100);
    Order order = new Order("O001", items, "CustomerCity", 10, 20, 9, 17);

    System.out.println("Order " + order.getOrderId() + ":");
    op.processOrder(order);

    // Reports
    List<Order> completed = new ArrayList<>();
    completed.add(order);
    ReportGenerator rg = new ReportGenerator(inv, completed);
    System.out.println();
    rg.generateDailyReport();
    System.out.println();
    rg.generateLowStockReport();
    System.out.println();
    rg.generateRouteReport();
  }
}

