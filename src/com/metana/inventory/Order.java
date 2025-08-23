package com.metana.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a customer order containing requested items.
 * Holds an order ID, the requested items (with quantities), and a destination for delivery.
 */

public class Order {
  private int customerX, customerY;     // Customer coordinates
  private int windowStart, windowEnd;   // Delivery time window (in hours, e.g. 9 and 17)

  // Constructor
  public Order(String orderId, Map<String,Integer> items, String destination, int customerX, int customerY, int windowStart, int windowEnd) {
    this.orderId = orderId;
    this.customerX = customerX;
    this.customerY = customerY;
    this.windowStart = windowStart;
    this.windowEnd = windowEnd;
    this.items = items;
    this.destination = destination;
  }

  public int getCustomerX() { return customerX; }
  public int getCustomerY() { return customerY; }
  public int getWindowStart() { return windowStart; }
  public int getWindowEnd() { return windowEnd; }
}
