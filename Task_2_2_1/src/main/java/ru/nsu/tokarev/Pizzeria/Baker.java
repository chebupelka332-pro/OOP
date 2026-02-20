package ru.nsu.tokarev.Pizzeria;


class Baker implements IWorker {
    private final int id;
    private final int bakingTime;
    private final OrderQueue orderQueue;
    private final Warehouse warehouse;

    Baker(int id, int bakingTime, OrderQueue orderQueue, Warehouse warehouse) {
        this.id = id;
        this.bakingTime = bakingTime;
        this.orderQueue = orderQueue;
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
                Order order = orderQueue.take();

                // null means queue is closed and empty – no more work
                if (order == null) {
                    break;
                }
                order.setState(OrderState.BAKING);
                System.out.println("  Baker #" + id + " is baking order [" + order.getId() + "]");

                Thread.sleep(bakingTime);

                System.out.println("  Baker #" + id + " finished order [" + order.getId() + "], moving to warehouse");
                warehouse.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("  Baker #" + id + " stopped.");
    }
}
