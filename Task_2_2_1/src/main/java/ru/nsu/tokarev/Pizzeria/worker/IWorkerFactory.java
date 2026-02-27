package ru.nsu.tokarev.Pizzeria.worker;

import ru.nsu.tokarev.Pizzeria.queue.OrderQueue;
import ru.nsu.tokarev.Pizzeria.warehouse.IWarehouse;


public interface IWorkerFactory {
    IWorker createBaker(int id, int bakingTimeMs, OrderQueue orderQueue, IWarehouse warehouse);
    IWorker createCourier(int id, int trunkSize, int deliveryTimeMs, IWarehouse warehouse);
}

