package com.metana.inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Warehouse {
  private String warehouseId;
  private Map<String, PriorityQueue<ProductBatch>> inventoryByProduct;
  private Set<String> usedBatchIds = new HashSet<>();
  private int x;
  private int y;

  // Convenience ctor (defaults coords to 0,0)
  public Warehouse(String warehouseId) {
    this(warehouseId, 0, 0);
  }

  public Warehouse(String warehouseId, int x, int y) {
    this.warehouseId = warehouseId;
    this.inventoryByProduct = new HashMap<>();
    this.x = x;
    this.y = y;
  }

  public static class BatchAllocation {
    public final String productId;
    public final String batchId;
    public final int quantityTaken;
    public BatchAllocation(String productId, String batchId, int quantityTaken) {
      this.productId = productId;
      this.batchId = batchId;
      this.quantityTaken = quantityTaken;
    }
  }

  public void addStock(String productId, int quantity, String batchId, LocalDate expiryDate) {
    if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");

    LocalDate today = LocalDate.now();
    if (!expiryDate.isAfter(today)) {
      throw new IllegalArgumentException("Cannot add stock that's already expired.");
    }

    if (usedBatchIds.contains(batchId)) {
      throw new IllegalArgumentException("Duplicate batchId: " + batchId);
    }

    ProductBatch newBatch = new ProductBatch(productId, quantity, batchId, expiryDate);

    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null) {
      pq = new PriorityQueue<>();
      inventoryByProduct.put(productId, pq);
    }
    pq.add(newBatch);
    usedBatchIds.add(batchId);

    System.out.println("Added stock: " + productId + ", batch " + batchId +
            " (qty=" + quantity + ", exp=" + expiryDate + ")");
  }

  // FEFO removal with per-batch accounting
  public List<BatchAllocation> removeStock(String productId, int quantity) {
    List<BatchAllocation> allocations = new ArrayList<>();
    if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");

    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null || pq.isEmpty()) return allocations;

    LocalDate today = LocalDate.now();
    while (!pq.isEmpty() && !pq.peek().getExpiryDate().isAfter(today)) {
      pq.poll(); // purge expired
    }

    int remaining = quantity;
    while (remaining > 0 && !pq.isEmpty()) {
      ProductBatch batch = pq.peek(); // earliest expiry
      int take = Math.min(batch.getQuantity(), remaining);
      allocations.add(new BatchAllocation(productId, batch.getBatchId(), take));

      if (batch.getQuantity() > take) {
        batch.setQuantity(batch.getQuantity() - take);
      } else {
        pq.poll(); // fully consumed
      }
      remaining -= take;
    }
    return allocations; // size may be < quantity if insufficient stock
  }

  public void purgeExpiredStock(LocalDate currentDate) {
    for (Map.Entry<String, PriorityQueue<ProductBatch>> entry : inventoryByProduct.entrySet()) {
      PriorityQueue<ProductBatch> pq = entry.getValue();
      while (!pq.isEmpty()) {
        ProductBatch earliestBatch = pq.peek();
        if (!earliestBatch.getExpiryDate().isAfter(currentDate)) {
          pq.poll();
          System.out.println("Removed expired batch " + earliestBatch.getBatchId()
                  + " of product " + entry.getKey());
        } else {
          break;
        }
      }
    }
  }

  public int getAvailableStock(String productId) {
    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null || pq.isEmpty()) return 0;

    LocalDate today = LocalDate.now();
    while (!pq.isEmpty() &&
           (!pq.peek().getExpiryDate().isAfter(today))) {
      pq.poll();
    }

    int total = 0;
    for (ProductBatch batch : pq) total += batch.getQuantity();
    return total;
  }

  // For low-stock report: snapshot of available qty per product in this warehouse
  public Map<String, Integer> getAllAvailableStock() {
    Map<String, Integer> snapshot = new HashMap<>();
    for (String productId : inventoryByProduct.keySet()) {
      snapshot.put(productId, getAvailableStock(productId));
    }
    return snapshot;
  }

  public ProductBatch getNextExpiringBatch(String productId) {
    PriorityQueue<ProductBatch> pq = inventoryByProduct.get(productId);
    if (pq == null || pq.isEmpty()) return null;

    ProductBatch nextBatch = pq.peek();
    LocalDate today = LocalDate.now();
    if (!nextBatch.getExpiryDate().isAfter(today)) {
      pq.poll();
      return getNextExpiringBatch(productId);
    }
    return nextBatch;
  }

  public String getWarehouseId() { return warehouseId; }
  public int getX() { return x; }
  public int getY() { return y; }
}
