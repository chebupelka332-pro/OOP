package ru.nsu.tokarev.Pizzeria;

import ru.nsu.tokarev.Pizzeria.config.IConfigLoader;
import ru.nsu.tokarev.Pizzeria.queue.Order;
import ru.nsu.tokarev.Pizzeria.queue.OrderQueue;
import ru.nsu.tokarev.Pizzeria.warehouse.IWarehouse;
import ru.nsu.tokarev.Pizzeria.warehouse.Warehouse;
import ru.nsu.tokarev.Pizzeria.worker.DefaultWorkerFactory;
import ru.nsu.tokarev.Pizzeria.worker.IWorker;
import ru.nsu.tokarev.Pizzeria.worker.IWorkerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Pizzeria {

    private final PizzeriaConfig config;
    private final OrderQueue orderQueue;
    private final IWarehouse warehouse;
    private final IWorkerFactory workerFactory;
    private final List<Thread> bakerThreads = new ArrayList<>();
    private final List<Thread> courierThreads = new ArrayList<>();

    public Pizzeria(IConfigLoader loader, IWorkerFactory workerFactory, String configResource) throws IOException {
        this.config = loader.load(configResource);
        this.workerFactory = workerFactory;
        this.orderQueue = new OrderQueue();
        this.warehouse = new Warehouse(config.warehouseCapacity);
    }

    public Pizzeria(String configResource) throws IOException {
        this(IConfigLoader.createDefault(), new DefaultWorkerFactory(), configResource);
    }

    public void start() throws InterruptedException {
        startBakers();
        startCouriers();
        generateOrders();
        shutdown();
    }

    private void startBakers() {
        for (PizzeriaConfig.BakerConfig bc : config.bakers) {
            IWorker baker = workerFactory.createBaker(bc.id, bc.bakingTimeMs, orderQueue, warehouse);
            Thread t = new Thread(baker, "Baker-" + bc.id);
            bakerThreads.add(t);
            t.start();
        }
        System.out.println("Started " + bakerThreads.size() + " baker(s).");
    }

    private void startCouriers() {
        for (PizzeriaConfig.CourierConfig cc : config.couriers) {
            IWorker courier = workerFactory.createCourier(cc.id, cc.trunkSize, cc.deliveryTimeMs, warehouse);
            Thread t = new Thread(courier, "Courier-" + cc.id);
            courierThreads.add(t);
            t.start();
        }
        System.out.println("Started " + courierThreads.size() + " courier(s).");
    }

    private void generateOrders() throws InterruptedException {
        long endTime = System.currentTimeMillis() + config.workingTimeMs;
        System.out.println("Pizzeria is open for " + config.workingTimeMs + " ms.");

        while (System.currentTimeMillis() < endTime) {
            Order order = new Order();
            orderQueue.put(order);
            Thread.sleep(config.orderIntervalMs);
        }

        System.out.println("Pizzeria stopped accepting new orders.");
    }

    private void shutdown() throws InterruptedException {
        orderQueue.close();
        for (Thread t : bakerThreads) {
            t.join();
        }
        System.out.println("All bakers finished.");

        warehouse.close();
        for (Thread t : courierThreads) {
            t.join();
        }
        System.out.println("All couriers finished. Pizzeria closed.");
    }
}
