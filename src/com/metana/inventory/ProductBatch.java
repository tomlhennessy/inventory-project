package com.metana.inventory;

import java.time.LocalDate;

public class ProductBatch implements Comparable<ProductBatch> {
  private String productId;
  private int quantity;
  private String batchId;
  private LocalDate expiryDate;

  public ProductBatch(String productId, int quantity, String batchId, LocalDate expiryDate) {
    this.productId = productId;
    this.quantity = quantity;
    this.batchId = batchId;
    this.expiryDate = expiryDate;
  }

  // Getters and setters
  public String getProductId() { return productId; }
  public int getQuantity() { return quantity; }
  public String getBatchId() { return batchId; }
  public LocalDate getExpiryDate() {return expiryDate; }

  @Override
  public int compareTo(ProductBatch other) {
    // Compare by expiry date (earlier date = "smaller")
    return this.expiryDate.compareTo(other.expiryDate);
  }

  @Override
  public String toString() {
    return String.format("Batch %s [product=%s, qty=%d, exp=%s]",
                                  batchId, productId, quantity, expiryDate);
  }

}
