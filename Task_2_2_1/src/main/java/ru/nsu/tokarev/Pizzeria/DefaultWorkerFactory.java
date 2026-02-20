package ru.nsu.tokarev.Pizzeria;


class DefaultWorkerFactory implements IWorkerFactory {
    @Override
    public IWorker createBaker(int id, int bakingTimeMs, OrderQueue orderQueue, Warehouse warehouse) {
        return new Baker(id, bakingTimeMs, orderQueue, warehouse);
    }

    @Override
    public IWorker createCourier(int id, int trunkSize, int deliveryTimeMs, Warehouse warehouse) {
        return new Courier(id, trunkSize, deliveryTimeMs, warehouse);
    }
}
