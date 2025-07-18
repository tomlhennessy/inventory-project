# Week 1: Inventory Management Module (Completed)

## Objective:
Establish a robust system to manage stock across multiple warehouses using efficient data structures, aligning with the assignment's requirement for expiry-aware allocation and foundational DSA application.

## Implemented Features:
- `Warehouse` class
  - Manages product inventory via `Map<String, PriorityQueue<ProductBatch>>`, where each product maps to a **min-heap of batches sorted by expiry date**.
  - Supports:
    • `addStock()` with validations (expiry, duplicates, negative quantity).
    • `getAvailableStock()` with auto-purging of expired batches.
    • `removeStock()` supporting partial batch consumption and expiry-skipping.

- `ProductBatch` class
  - Holds batch metadata (`productId`, `batchId`, `quantity`, `expiryDate`) and implements `Comparable` for min-heap ordering by expiry.

- `InventoryManager` class
  - Tracks all warehouses, supports adding and retrieving by ID.

- `Main.java`
  - Tests creation of warehouses, stock addition, and order processing (via `OrderProcessor`).

- `OrderProcessor` (initial version)
  - Iterates over order items and uses a **greedy earliest-expiry allocation strategy** accross warehouses (partial fulfillment supported).
  - Handles expired stock and prints shortfall warnings.

## DSA Justification:
- `HashMap`: O(1) average for product-to-batch lookup
- `PriorityQueue`: O(log k) insert, O(1) min access; perfect for expiry-priority retrieval.
- **Validation & Expiry logic**: Done inline to avoid allocating invalid stock.

## Testing Done:
Simulated expiry scenario with two warehouses (`W1`, `W2`), two batches of the same product (`P001`), and a single order spanning both warehouses. Confirmed correct expiry-first selection and quantity tracking.

> Next steps (Week 2): Extend order fulfillment to optimize for **warehouse proximity** to customers using distance-based heuristics, while continuing to build on this expiry-based stock model.
