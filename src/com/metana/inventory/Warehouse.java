package com.metana.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Represents a warehouse location that stores inventory.
 * Each warehouse has a name/location and keeps track of item stock levels.
 */

public class Warehouse {
  private String warehouseId;           // unique ID or name for the warehouse
  private Map<String, PriorityQueue<ProductBatch>> inventoryByProduct;   // inventory stock: itemId -> min-heap of its batches
  private Set<String> usedBatchIds = new HashSet<>();
  private int x;
  private int y;

  public Warehouse(String warehouseId, int x, int y) {
    this.warehouseId = warehouseId;
    this.inventoryByProduct = new HashMap<>();
    this.x = x;
    this.y = y;
  }

  public void addStock(String productId, int quantity, String batchId, LocalDate expiryDate) {
    // 1.  Basic validation checks
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive.");
    }

    LocalDate today = LocalDate.now();
    if (expiryDate.isBefore(today)) {
      throw new IllegalArgumentException("Cannot add stock that's already expired.");
    }

    if (usedBatchIds.contains(batchId)) {
      throw new IllegalArgumentException("Duplicate batchId: " + batchId);
    }

    // 2. Create the new batch
    ProductBatch newBatch = new ProductBatch(productId, quantity, batchId, expiryDate);

    // 3. Add to product's priority queue in inventory
    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null) {
      // No queue for ths product yet, create one
      pq = new PriorityQueue<>();
      inventoryByProduct.put(productId, pq);
    }
    pq.add(newBatch);
    // Now the smallest expiry batch for this product will be at pq.peek();

    usedBatchIds.add(batchId);

    // 4. Confirmation for testing
    System.out.println("Added stock: " + productId + ", batch " + batchId + "(qty=" + quantity + ", exp=" + expiryDate + ")");
  }

  public void removeStock(String productId, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive.");
    }

    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null || pq.isEmpty()) {
      return; // no stock
    }

    LocalDate today = LocalDate.now();
    // purge any expired batches first
    while (!pq.isEmpty() && !pq.peek().getExpiryDate().isAfter(today)) {
      pq.poll();
    }
    // Remove from the earliest expiring batches
    while (quantity > 0 && !pq.isEmpty()) {
      ProductBatch batch = pq.peek();
      if (batch.getQuantity() > quantity) {
        // Decrement quantity in the batch
        batch.setQuantity(batch.getQuantity() - quantity);
        quantity = 0;
      } else {
        // remove this whole batch and continue
        quantity -= batch.getQuantity();
        pq.poll();
      }
    }
  }

  public void purgeExpiredStock(LocalDate currentDate) {
    for (Map.Entry<String, PriorityQueue<ProductBatch>> entry : inventoryByProduct.entrySet()) {
      PriorityQueue<ProductBatch> pq = entry.getValue();
      // Remove expired batches for this product
      while (!pq.isEmpty()) {
        ProductBatch earliestBatch = pq.peek();
        if (earliestBatch.getExpiryDate().isBefore(currentDate) || earliestBatch.getExpiryDate().isEqual(currentDate)) {
          // this batch is expired as of currentDate
          pq.poll(); // remove it from the queue
          System.out.println("Removed expired batch " + earliestBatch.getBatchId() + " of product " + entry.getKey());
        } else {
          // the earliest batch is not expired yet, so none others are expired
          break;
        }
      }
    }
  }

  public int getAvailableStock(String productId) {
    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null || pq.isEmpty()) {
      return 0;
    }
    // ensure no expired stock is lurking
    LocalDate today = LocalDate.now();
    // could call `purgeExpiredStock(today)` here, but that would check all products.
    // to be precise for this product only, we can use a similar loop as purge for this queue.
    while (!pq.isEmpty() &&
      (pq.peek().getExpiryDate().isBefore(today)
       || pq.peek().getExpiryDate().isEqual(today))) {
    pq.poll();
    }


    // sum quantities of remaining batches
    int totalQuantity = 0;
    for (ProductBatch batch : pq) {
      totalQuantity += batch.getQuantity();
    }
    return totalQuantity;
  }

  public ProductBatch getNextExpiringBatch(String productId) {
    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null || pq.isEmpty()) {
      return null;
    }
    // Ensure the next batch isn't expired as of now; if it is, purge it.
    ProductBatch nextBatch = pq.peek();
    LocalDate today = LocalDate.now();
    if (nextBatch.getExpiryDate().isBefore(today)) {
      // remove expired batch and get the next one
      pq.poll();
      return getNextExpiringBatch(productId); // recursive call after removal
    }
    return nextBatch;
  }

  // Getters for warehouse properties
  public String getWarehouseId() { return warehouseId; }
  public int getX() { return x; }
  public int getY() { return y; }
}
