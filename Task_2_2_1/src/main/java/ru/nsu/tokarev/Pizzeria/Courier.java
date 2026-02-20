package ru.nsu.tokarev.Pizzeria;

import java.util.List;


class Courier implements IWorker {
    private final int id;
    private final int trunkSize; // Maximum number of pizzas this courier can carry at once
    private final int deliveryTime;
    private final Warehouse warehouse;

    Courier(int id, int trunkSize, int deliveryTime, Warehouse warehouse) {
        this.id = id;
        this.trunkSize = trunkSize;
        this.deliveryTime = deliveryTime;
        this.warehouse = warehouse;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<Order> batch = warehouse.take(trunkSize);
                if (batch.isEmpty()) {
                    // Warehouse is closed and empty, nothing left to deliver
                    break;
                }

                for (Order order : batch) {
                    order.setState(OrderState.DELIVERING);
                }
                System.out.println("  Courier #" + id + " delivering orders: " + batchIds(batch));

                Thread.sleep(deliveryTime);

                for (Order order : batch) {
                    order.setState(OrderState.DELIVERED);
                }
                System.out.println("  Courier #" + id + " delivered orders: " + batchIds(batch));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("  Courier #" + id + " stopped.");
    }

    private String batchIds(List<Order> batch) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < batch.size(); i++) {
            sb.append(batch.get(i).getId());
            if (i < batch.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
