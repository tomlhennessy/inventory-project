package com.metana.inventory;

import java.util.List;
import java.util.Map;

public class Order {
  private String orderId;
  private Map<String, Integer> items;   // productId -> qty
  private String destination;

  private int customerX, customerY;     // coordinates
  private int windowStart, windowEnd;   // hours

  private List<Integer> deliveryRoute;  // indices printed by route optimizer
  private double routeDistance;

  // Full constructor (recommended)
  public Order(String orderId,
               Map<String,Integer> items,
               String destination,
               int customerX, int customerY,
               int windowStart, int windowEnd) {
    this.orderId = orderId;
    this.items = items;
    this.destination = destination;
    this.customerX = customerX;
    this.customerY = customerY;
    this.windowStart = windowStart;
    this.windowEnd = windowEnd;
  }

  // Legacy simple constructor (kept for convenience; sets defaults)
  public Order(String orderId, Map<String,Integer> items, String destination) {
    this(orderId, items, destination, 0, 0, 9, 17);
  }

  public String getOrderId() { return orderId; }
  public Map<String, Integer> getItems() { return items; }
  public String getDestination() { return destination; }
  public int getCustomerX() { return customerX; }
  public int getCustomerY() { return customerY; }
  public int getWindowStart() { return windowStart; }
  public int getWindowEnd() { return windowEnd; }

  public List<Integer> getDeliveryRoute() { return deliveryRoute; }
  public void setDeliveryRoute(List<Integer> deliveryRoute) { this.deliveryRoute = deliveryRoute; }

  public double getRouteDistance() { return routeDistance; }
  public void setRouteDistance(double routeDistance) { this.routeDistance = routeDistance; }
}
