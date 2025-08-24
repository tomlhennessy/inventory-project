package com.metana.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class OrderProcessor {
  private final InventoryManager inventoryManager;
  private final RouteOptimizer routeOptimizer; // now actually used

  public OrderProcessor(InventoryManager inventoryManager, RouteOptimizer routeOptimizer) {
    this.inventoryManager = inventoryManager;
    this.routeOptimizer = routeOptimizer;
  }

  public void processOrder(Order order) {
    // 1) upfront availability check
    for (Map.Entry<String,Integer> e : order.getItems().entrySet()) {
      if (inventoryManager.getTotalStock(e.getKey()) < e.getValue()) {
        System.out.println("Not enough stock for Order " + order.getOrderId());
        return;
      }
    }

    // 2) FEFO inside each warehouse; choose warehouses by proximity to customer
    List<Warehouse> warehousesUsed = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
      String productId = entry.getKey();
      int needed = entry.getValue();

      // candidates with stock
      List<Warehouse> candidates = new ArrayList<>();
      for (Warehouse w : inventoryManager.getWarehouses()) {
        if (w.getAvailableStock(productId) > 0) candidates.add(w);
      }
      // sort by distance to customer
      candidates.sort(Comparator.comparingDouble(
          w -> distance(w.getX(), w.getY(), order.getCustomerX(), order.getCustomerY())
      ));

      for (Warehouse w : candidates) {
        if (needed <= 0) break;
        int avail = w.getAvailableStock(productId);
        if (avail <= 0) continue;

        int toTake = Math.min(needed, avail);
        List<Warehouse.BatchAllocation> used = w.removeStock(productId, toTake);

        if (!used.isEmpty() && !warehousesUsed.contains(w)) warehousesUsed.add(w);

        // Print per-batch allocations
        for (Warehouse.BatchAllocation ba : used) {
          System.out.println("- " + w.getWarehouseId() + ": " +
              ba.productId + ", " + ba.quantityTaken + " (" + ba.batchId + ")");
        }

        int actuallyTaken = used.stream().mapToInt(b -> b.quantityTaken).sum();
        needed -= actuallyTaken;
      }

      if (needed > 0) {
        System.out.println("Order " + order.getOrderId()
            + ": Could not fully fulfill item " + productId
            + " (short by " + needed + " units).");
        return; // abort whole order for simplicity
      }
    }

    // 3) Plan route (W0 -> used warehouses (NN order) -> customer -> W0)
    if (!warehousesUsed.isEmpty()) {
      List<int[]> coords = new ArrayList<>();
      coords.add(inventoryManager.getW0Coordinates()); // index 0

      // stable ordering by ID so indices are deterministic
      warehousesUsed.sort(Comparator.comparing(Warehouse::getWarehouseId));
      for (Warehouse wh : warehousesUsed) {
        coords.add(new int[]{wh.getX(), wh.getY()});
      }
      coords.add(new int[]{order.getCustomerX(), order.getCustomerY()});
      int customerIdx = 1 + warehousesUsed.size();

      RouteOptimizer.RouteResult rr = routeOptimizer.computeRoute(
          coords, warehousesUsed.size(), customerIdx,
          order.getWindowStart(), order.getWindowEnd()
      );
      order.setDeliveryRoute(rr.route);
      order.setRouteDistance(rr.totalDistance);

      // Print route & distance
      System.out.print("Route: [");
      for (int i = 0; i < rr.route.size(); i++) {
        System.out.print(rr.route.get(i));
        if (i < rr.route.size() - 1) System.out.print(",");
      }
      System.out.printf("], Distance: %.2f", rr.totalDistance);
      if (!rr.withinTimeWindow) {
        System.out.print(" (Warning: delivery might miss time window)");
      }
      System.out.println();
    }
  }

  private double distance(int x1, int y1, int x2, int y2) {
    double dx = x1 - x2, dy = y1 - y2; // fixed: was y1 = y2 earlier
    return Math.sqrt(dx*dx + dy*dy);
  }
}
