package ru.nsu.tokarev.Pizzeria;

import org.junit.jupiter.api.Test;
import ru.nsu.tokarev.Pizzeria.config.IConfigLoader;
import ru.nsu.tokarev.Pizzeria.queue.Order;
import ru.nsu.tokarev.Pizzeria.queue.OrderQueue;
import ru.nsu.tokarev.Pizzeria.warehouse.Warehouse;

import static org.junit.jupiter.api.Assertions.*;

class PizzeriaTest {
    @Test
    void testPizzeriaRunsToCompletion() throws Exception {
        Pizzeria pizzeria = new Pizzeria("pizzeria_test_config.json");
        assertDoesNotThrow(pizzeria::start);
    }

    @Test
    void testConfigLoaderThrowsForMissingResource() {
        IConfigLoader loader = IConfigLoader.createDefault();
        assertThrows(java.io.IOException.class, () -> loader.load("nonexistent.json"));
    }

    @Test
    void testOrderQueueClosedReturnsNull() throws InterruptedException {
        OrderQueue queue = new OrderQueue();
        queue.close();
        Order result = queue.take();
        assertNull(result);
    }

    @Test
    void testOrderIdsAreUnique() {
        Order a = new Order();
        Order b = new Order();
        assertNotEquals(a.getId(), b.getId());
        assertTrue(b.getId() > a.getId());
    }

    @Test
    void testWarehouseCapacityAndDrain() throws InterruptedException {
        Warehouse warehouse = new Warehouse(2);

        Order o1 = new Order();
        Order o2 = new Order();
        warehouse.put(o1);
        warehouse.put(o2);

        assertEquals(2, warehouse.size());

        warehouse.close();
        var batch = warehouse.take(2);
        assertEquals(2, batch.size());
        assertTrue(warehouse.isEmpty());
    }
}

