package com.metana.inventory;

import java.util.List;

/**
 * Generates reports based on inventory and order data.
 * For example, inventory levels per warehouse or order fulfilment statistics.
 */

public class ReportGenerator {
  // Generate a summary report of current inventory in all warehouses
  public void generateInventoryReport(List<Warehouse> warehouses) {
    // TODO: Implement report generation.
    // e.g., iterate through warehouses and print out item stock levels
  }

  // Generate a report for completed orders (e.g. success rate, pending orders, etc.)
  public void generateOrderReport(List<Order> orders) {
    // TODO: Implement report generation for orders.
    // e.g. summarize how many orders are fulfilled, average delivery time (if tracked), etc.
  }
}
