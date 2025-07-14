package com.metana.inventory;

import java.util.Map;

/**
 * Represents a customer order containing requested items.
 * Holds an order ID, the requested items (with quantities), and a destination for delivery.
 */

public class Order {
  private String orderId;
  private Map<String, Integer> items;     // items in the order: itemId -> quantity requested
  private String destination;             // delivery destination (e.g., customer address or city)

  public Order(String orderId, Map<String, Integer> items, String destination) {
    this.orderId = orderId;
    this.items = items;
    this.destination = destination;
  }

  // Getters
  public String getOrderId() {
    return orderId;
  }

  public Map<String, Integer> getItems() {
    return items;
  }

  public String getDestination() {
    return destination;
  }
}
