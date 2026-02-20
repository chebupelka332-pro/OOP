package ru.nsu.tokarev.Pizzeria;

import java.util.List;


class PizzeriaConfig {
    int workingTimeMs;

    int warehouseCapacity;

    int orderIntervalMs;

    List<BakerConfig> bakers;
    List<CourierConfig> couriers;

    static class BakerConfig {
        int id;
        int bakingTimeMs;
    }

    static class CourierConfig {
        int id;
        int trunkSize;
        int deliveryTimeMs;
    }
}

