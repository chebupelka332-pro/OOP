package ru.nsu.tokarev.Pizzeria;


interface IWorkerFactory {
    IWorker createBaker(int id, int bakingTimeMs, OrderQueue orderQueue, Warehouse warehouse);
    IWorker createCourier(int id, int trunkSize, int deliveryTimeMs, Warehouse warehouse);
}

