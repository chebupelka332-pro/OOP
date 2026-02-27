package ru.nsu.tokarev.Pizzeria;

import java.util.List;


public class PizzeriaConfig {
    public int workingTimeMs;
    public int warehouseCapacity;
    public int orderIntervalMs;

    public List<BakerConfig> bakers;
    public List<CourierConfig> couriers;

    public static class BakerConfig {
        public int id;
        public int bakingTimeMs;
    }

    public static class CourierConfig {
        public int id;
        public int trunkSize;
        public int deliveryTimeMs;
    }
}
