# Multi‑Warehouse Inventory & Delivery Optimization (Java)

A DS&A‑focused backend that manages per‑batch, expiry‑aware inventory across multiple warehouses, fulfills customer orders greedily by distance (while consuming FEFO batches), and computes a route with a simple TSP heuristic and a delivery time‑window check.

## 1) Overview
This project implements the assignment’s four pillars:
- **Inventory Management**: per‑warehouse product inventory, tracked at batch level with expiry dates; FEFO selection.
- **Order Processing**: allocate an order by greedily choosing nearest warehouses while each warehouse consumes batches by earliest expiry.
- **Route Optimization**: build a route W0 → used warehouses → customer → W0 with a nearest‑neighbor heuristic and a simple time‑window check.
- **Reporting** (basic scaffolding): placeholders for daily/low‑stock/route summaries; console output demonstrates allocations & route metrics.

The code is modular (separate classes), leans on core DS&A (hash maps, heaps, greedy selection, simple graph distance), and includes validation & edge‑case handling around expiry and quantities.

## 2) Project structure
- src/com/metana/inventory/
- ├─ Main.java                // demo runner
- ├─ InventoryManager.java    // registry of warehouses (add/get/list)
- ├─ Warehouse.java           // per-warehouse FEFO inventory + coords
- ├─ ProductBatch.java        // batch value object, Comparable by expiry
- ├─ Order.java               // order details: items, coords, time window
- ├─ OrderProcessor.java      // greedy allocation + route planning call
- └─ RouteOptimizer.java      // nearest-neighbor route + window check

## 3) Data structures & algorithms
**Inventory**:
- `HashMap<String, PriorityQueue<ProductBatch>>` per warehouse
- `PriorityQueue` enforces FEFO: `O(log k)` insert, `O(1)` peek, `O(log k)` extract; `k` = batches of a product.
- Expired batches are purged lazily (when checking/consuming).

**Order selection (greedy)**:
- For each product: collect candidate warehouses with stock, sort by distance to the customer (`O(N log N)`) and allocate from nearest first.
- This is a greedy heuristic: simple, fast, and aligns with minimizing distance without solving set cover optimally.

**Routing (TSP heuristic)**:
- Nearest‑neighbor: start at `W0`, repeatedly go to nearest unvisited location, include customer, return to `W0`; `O(m²)` on `m` points (small per order).
- Time window: travel speed 1 unit/min; if total route minutes exceed the window, we print a warning.

## 4) Complexity (big‑O)
&emsp;&emsp;&emsp;**Operation** &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;**Time** &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;**Space**
- Add stock (per batch) &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;O(1) HashMap + O(log k) heap &emsp;&emsp;&emsp;&emsp;&emsp;O(k) per product
- Get available stock (sum non‑expired) &emsp;&emsp;&emsp;&emsp;&emsp;O(k) (worst)
- Remove stock (consume FEFO batches) &emsp;&emsp;&emsp;&emsp;up to O(log k) per batch pop
- Select warehouses for one product &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;	Build candidates O(N), sort O(N log N)
- Allocate across warehouses (one product) &emsp;&emsp;&emsp;≤ O(N) calls into warehouse ops
- Route (nearest‑neighbor) &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;O(m²) (m = W0 + used WH + C) &emsp;&emsp;&emsp;&emsp;O(m²) (if matrix)

With N ≤ 100 and modest k per product, this satisfies the assignment’s efficiency goals.

## 5) Edge cases & validation
- **Expired stock**: never allocated. We purge expired batches at allocation/check time.
- **Duplicate batch IDs** (per warehouse): rejected.
- **Non‑positive quantities**: rejected.
- **Insufficient stock**: order prints a clear message and aborts allocation for the missing item.
- **Tight time window**: route still prints but includes a warning if the window is exceeded.

## 6) Assumptions
- **Travel speed**: 1 distance unit = 1 minute.
- **Coordinates**: integer (x,y) for warehouses and customer.
- **W0 (depot)**: fixed at index 0 in the route point list; you can treat W0 as (0,0) or make it configurable later.
- **Prices for reports**: constant (e.g., P001 = $5, P002 = $7) when you enable revenue reporting.

## 7) How to run (Java 17+)
From the `src` folder (adjust if you compile elsewhere):
```yaml
javac com/metana/inventory/*.java
java com.metana.inventory.Main
```

`Main` currently:
- builds two warehouses,
- adds sample stock,
- creates a sample order with `(x,y)` and a time window,
- processes the order and prints allocations + route.

## 8) Sample demo (console)
```yaml
Example:

Added stock: P001, batch B001(qty=20, exp=2025-12-31)
Added stock: P001, batch B002(qty=50, exp=2025-06-30)

Order O001:
- W2: P001, 50 (B002)
- W1: P001, 20 (B001)
Route: [0, 2, 1, 3, 0], Distance: 45.32
```

Your outputs will match your specific coordinates and which warehouses ended up supplying each item.

## 9) Testing plan (≥15 cases)

- **Normal**:
1. Add valid stock; check confirmation.
2. Single‑warehouse order, fully fulfillable.
3. Multi‑warehouse order where one product is split.
4. Multiple products from the same nearest warehouse.

- **Edge**:
5. Add expired stock → rejected.
6. Duplicate batchId → rejected.
7. Negative or zero quantity → rejected.
8. Order with insufficient total stock → prints failure and aborts allocation for that item.
9. Order where earliest batch is expired but a later batch exists → uses later batch.
10. Tight time window (distance > window) → route prints with warning.

- **Boundary**:
11. Product exactly equals requested quantity (depletes batch to zero).
12. Single warehouse in system.
13. One product, one batch, single order.
14. Large quantities across several batches (exercise removeStock loop).
15. Many warehouses (e.g., 50) with small quantities to test greedy selection and performance.

## 10) Design trade‑offs
- **Greedy warehouse selection** (nearest first) is simple and fast, but not globally optimal in every case (it approximates minimizing distance; exact optimization would be combinatorial). Given `N ≤ 100`, greedy is appropriate and easy to justify.
- **Nearest‑neighbor route** is a common TSP heuristic: `O(m²)`, quick to implement, good enough for small `m` (few warehouses per order). Could add Held–Karp DP when `m ≤ 10` as an extension.

## 11) Future work / extensibility
- **Command parser** to accept `ADD_STOCK`, `PLACE_ORDER`, `GENERATE_REPORT` ...
- **ReportGenerator finalize**:
  1. Daily summary (orders, sold units, revenue).
  2. Low‑stock (<50 units across all warehouses).
  3. Route report (per‑order distances).
- **Global index of products** to speed up “warehouses that stock Pxxx”.
- **Better routing heuristic** (e.g., 2‑opt improvement pass).
- **Multiple vehicles / capacity** (turns into VRP).
- **Persist data** (files or DB) and **add unit tests**.
