package ru.nsu.tokarev.Pizzeria.worker;

import ru.nsu.tokarev.Pizzeria.queue.Order;
import ru.nsu.tokarev.Pizzeria.queue.OrderState;
import ru.nsu.tokarev.Pizzeria.warehouse.IWarehouse;

import java.util.List;


public class Courier implements IWorker {
    private final int id;
    private final int trunkSize;
    private final int deliveryTime;
    private final IWarehouse warehouse;

    Courier(int id, int trunkSize, int deliveryTime, IWarehouse warehouse) {
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
    public String toString() {
        return "Courier #" + id;
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
                System.out.println(this + " delivering orders: " + batchIds(batch));

                Thread.sleep(deliveryTime);

                for (Order order : batch) {
                    order.setState(OrderState.DELIVERED);
                }
                System.out.println(this + " delivered orders: " + batchIds(batch));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(this + " stopped.");
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
