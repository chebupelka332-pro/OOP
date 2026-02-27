package ru.nsu.tokarev.Pizzeria.worker;

import ru.nsu.tokarev.Pizzeria.queue.OrderQueue;
import ru.nsu.tokarev.Pizzeria.warehouse.IWarehouse;


public class DefaultWorkerFactory implements IWorkerFactory {
    @Override
    public IWorker createBaker(int id, int bakingTimeMs, OrderQueue orderQueue, IWarehouse warehouse) {
        return new Baker(id, bakingTimeMs, orderQueue, warehouse);
    }

    @Override
    public IWorker createCourier(int id, int trunkSize, int deliveryTimeMs, IWarehouse warehouse) {
        return new Courier(id, trunkSize, deliveryTimeMs, warehouse);
    }
}
