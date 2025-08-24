package com.metana.inventory;

import java.util.ArrayList;
import java.util.List;

public class RouteOptimizer {

  // coords: [W0, W1..Wk, Customer]
  public RouteResult computeRoute(List<int[]> coords,
                                  int warehouseCount,
                                  int customerIndex,
                                  int windowStart,
                                  int windowEnd) {
    int n = coords.size();
    boolean[] visited = new boolean[n];
    List<Integer> route = new ArrayList<>();
    int current = 0; // start at W0
    route.add(current);
    visited[current] = true;

    // visit all warehouses via nearest neighbour
    for (int visitCount = 0; visitCount < warehouseCount; visitCount++) {
      double nearestDist = Double.MAX_VALUE;
      int nearestIndex = -1;
      for (int j = 1; j <= warehouseCount; j++) {
        if (!visited[j]) {
          double d = distance(coords.get(current), coords.get(j));
          if (d < nearestDist) { nearestDist = d; nearestIndex = j; }
        }
      }
      if (nearestIndex == -1) break; // none left
      visited[nearestIndex] = true;
      route.add(nearestIndex);
      current = nearestIndex;
    }

    // go to customer
    if (!visited[customerIndex]) {
      route.add(customerIndex);
      current = customerIndex;
      visited[customerIndex] = true;
    }
    // return to W0
    if (current != 0) route.add(0);

    double totalDistance = 0.0;
    for (int i = 0; i < route.size() - 1; i++) {
      int a = route.get(i), b = route.get(i + 1);
      totalDistance += distance(coords.get(a), coords.get(b));
    }

    int windowMinutes = Math.max(0, (windowEnd - windowStart)) * 60; // 1 unit = 1 minute
    boolean onTime = (totalDistance <= windowMinutes);
    return new RouteResult(route, totalDistance, onTime);
  }

  private double distance(int[] a, int[] b) {
    double dx = a[0] - b[0], dy = a[1] - b[1];
    return Math.sqrt(dx*dx + dy*dy);
  }

  public static class RouteResult {
    public final List<Integer> route;
    public final double totalDistance;
    public final boolean withinTimeWindow;
    public RouteResult(List<Integer> route, double totalDistance, boolean onTime) {
      this.route = route;
      this.totalDistance = totalDistance;
      this.withinTimeWindow = onTime;
    }
  }
}
