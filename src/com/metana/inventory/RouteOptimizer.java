package com.metana.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Resposible for optimizing delivery routes.
 * Given order details and warehouse locations, it can calculate the most efficient route.
 */
public class RouteOptimizer {
  // Compute route visiting all warehouses and the customer, return to W0.
  // Parameters:
  // - coords: List of (x, y) coordinates for [W0, W1, ..., Wn, Customer]
  // - warehouseCount: how many warehouses are included (W0 not included, W0 is index 0)
  // - customerIndex: index of customer in coords list
  // - windowStart, windowEnd: delivery window in hours (e.g. 9 and 17)
  public static RouteResult computeRoute(List<int[]> coords, int warehouseCount, int customerIndex, int windowStart, int windowEnd) {
    int n = coords.size(); // total points (W0 + warehouses + customer)
    boolean[] visited = new boolean[n];
    List<Integer> route = new ArrayList<>();
    int current = 0; // start at W0
    route.add(current);
    visited[current] = true;

    // visit all warehouses using nearest neighbour
    for (int visitCount = 0; visitCount < warehouseCount; visitCount++) {
      // find nearest unvisited warehouse
      double nearestDist = Double.MAX_VALUE;
      int nearestIndex = -1;
      for (int j = 1; j <= warehouseCount; j++) {
        if (!visited[j]) {
          double d = distance(coords.get(current), coords.get(j));
          if (d < nearestDist) {
            nearestDist = d;
            nearestIndex = j;
          }
        }
      }
      if (nearestIndex == -1); // no unvisited warehouse left
      visited[nearestIndex] = true;
      route.add(nearestIndex);
      current = nearestIndex;
    }

    // after visiting all warehouses, go to customer
    if (!visited[customerIndex]) {
      route.add(customerIndex);
      current = customerIndex;
      visited[customerIndex] = true;
    }
    // finally return to W0
    if (current != 0) {
      route.add(0);
    }

    // Calculate total distance of the route
    double totalDistance = 0.0;
    for (int i = 0; i < route.size() - 1; i++) {
      int a = route.get(i);
      int b = route.get(i + 1);
      totalDistance += distance(coords.get(a), coords.get(b));
    }
    // check time window (convert hours to minutes for window length)
    int windowMinutes = (windowEnd - windowStart) * 60;
    boolean onTime = (totalDistance <= windowMinutes);

    return new RouteResult(route, totalDistance, onTime);
  }

  private static double distance(int[] a, int[] b) {
    double dx = a[0] - b[0];
    double dy = a[1] - b[1];
    return Math.sqrt(dx*dx + dy*dy);
  }

  // Simple container for route result
  public static class RouteResult {
    public List<Integer> route;
    public double totalDistance;
    public boolean withinTimeWindow;
    public RouteResult(List<Integer> route, double totalDistance, boolean onTime) {
      this.route = route;
      this.totalDistance = totalDistance;
      this.withinTimeWindow = onTime;
    }
  }
}
