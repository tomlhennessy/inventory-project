package com.metana.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {
  private final InventoryManager inventory;
  private final List<Order> completedOrders;

  // Price map (adjust as needed)
  private static final Map<String, Double> priceMap = Map.of(
      "P001", 5.0,
      "P002", 7.0
  );

  public ReportGenerator(InventoryManager inv, List<Order> completedOrders) {
    this.inventory = inv;
    this.completedOrders = completedOrders;
  }

  public void generateDailyReport() {
    System.out.println("Daily Report:");
    System.out.println("-------------");
    int orderCount = completedOrders.size();

    Map<String, Integer> totalSold = new HashMap<>();
    double totalRevenue = 0.0;

    for (Order order : completedOrders) {
      for (Map.Entry<String,Integer> e : order.getItems().entrySet()) {
        String pid = e.getKey();
        int qty = e.getValue();
        totalSold.merge(pid, qty, Integer::sum);
        totalRevenue += priceMap.getOrDefault(pid, 0.0) * qty;
      }
    }

    System.out.println("Total Orders: " + orderCount);
    System.out.printf("Total Revenue: $%.2f%n", totalRevenue);
    System.out.println("Products Sold:");
    if (totalSold.isEmpty()) {
      System.out.println("- None");
    } else {
      for (Map.Entry<String, Integer> entry : totalSold.entrySet()) {
        System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " units");
      }
    }
  }

  public void generateLowStockReport() {
    System.out.println("Low-Stock Report (<50 units total):");
    Map<String, Integer> totals = new HashMap<>();

    for (Warehouse wh : inventory.getWarehouses()) {
      Map<String, Integer> snapshot = wh.getAllAvailableStock();
      for (Map.Entry<String,Integer> e : snapshot.entrySet()) {
        totals.merge(e.getKey(), e.getValue(), Integer::sum);
      }
    }

    List<String> lows = new ArrayList<>();
    for (Map.Entry<String,Integer> e : totals.entrySet()) {
      if (e.getValue() < 50) {
        lows.add(e.getKey() + ": " + e.getValue() + " units");
      }
    }

    if (lows.isEmpty()) {
      System.out.println("None");
    } else {
      lows.sort(String::compareTo);
      for (String s : lows) System.out.println("- " + s);
    }
  }

  public void generateRouteReport() {
    System.out.println("Route Report:");
    if (completedOrders.isEmpty()) {
      System.out.println("No orders processed yet.");
      return;
    }
    double totalDistance = 0.0;
    for (Order order : completedOrders) {
      double dist = order.getRouteDistance();
      totalDistance += dist;
      System.out.printf("- Order %s: %.2f%n", order.getOrderId(), dist);
    }
    System.out.printf("Total Distance Travelled: %.2f%n", totalDistance);
  }
}
