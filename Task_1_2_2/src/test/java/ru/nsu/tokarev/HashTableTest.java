package ru.nsu.tokarev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.nsu.tokarev.HashTable.HashTable;
import ru.nsu.tokarev.HashTable.Entry;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable<String, Integer> hashTable;
    private HashTable<Integer, String> integerKeyTable;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable<>();
        integerKeyTable = new HashTable<>();
    }

    @Test
    void testEmptyHashTableCreation() {
        assertTrue(hashTable.isEmpty());
        assertEquals(0, hashTable.size());
        assertNull(hashTable.get("nonexistent"));
        assertFalse(hashTable.containsKey("key"));
    }

    @Test
    void testHashTableWithInitialCapacity() {
        HashTable<String, String> customTable = new HashTable<>(32);
        assertTrue(customTable.isEmpty());
        assertEquals(0, customTable.size());
    }

    @Test
    void testInvalidInitialCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new HashTable<String, String>(-1));
        assertThrows(IllegalArgumentException.class, () -> new HashTable<String, String>(0));
    }

    @Test
    void testPutOperation() {
        assertNull(hashTable.put("key1", 1));
        assertEquals(1, hashTable.size());
        assertFalse(hashTable.isEmpty());

        assertNull(hashTable.put("key2", 2));
        assertEquals(2, hashTable.size());

        // Обновление существующего ключа
        Integer oldValue = hashTable.put("key1", 10);
        assertEquals(1, oldValue);
        assertEquals(2, hashTable.size()); // размер не изменился
        assertEquals(10, hashTable.get("key1"));
    }

    @Test
    void testGetOperation() {
        hashTable.put("key1", 100);
        hashTable.put("key2", 200);

        assertEquals(100, hashTable.get("key1"));
        assertEquals(200, hashTable.get("key2"));
        assertNull(hashTable.get("nonexistent"));
    }

    @Test
    void testUpdateOperation() {
        hashTable.put("key1", 1);

        Integer oldValue = hashTable.update("key1", 10);
        assertEquals(1, oldValue);
        assertEquals(10, hashTable.get("key1"));

        // Обновление несуществующего ключа
        assertNull(hashTable.update("newkey", 5));
        assertEquals(5, hashTable.get("newkey"));
        assertEquals(2, hashTable.size());
    }

    @Test
    void testRemoveOperation() {
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key3", 3);

        Integer removed = hashTable.remove("key2");
        assertEquals(2, removed);
        assertEquals(2, hashTable.size());
        assertNull(hashTable.get("key2"));
        assertFalse(hashTable.containsKey("key2"));

        assertNull(hashTable.remove("nonexistent"));
        assertEquals(2, hashTable.size());
    }

    @Test
    void testContainsKeyOperation() {
        hashTable.put("key1", 1);
        hashTable.put("key2", null); // null значение

        assertTrue(hashTable.containsKey("key1"));
        assertTrue(hashTable.containsKey("key2"));
        assertFalse(hashTable.containsKey("nonexistent"));
    }

    @Test
    void testNullKeysAndValues() {
        // null ключ
        hashTable.put(null, 100);
        assertEquals(100, hashTable.get(null));
        assertTrue(hashTable.containsKey(null));

        hashTable.put("key1", null);
        assertNull(hashTable.get("key1"));
        assertTrue(hashTable.containsKey("key1"));

        hashTable.put(null, null);
        assertNull(hashTable.get(null));
        assertTrue(hashTable.containsKey(null));

        assertEquals(2, hashTable.size());
    }

    @Test
    void testAutomaticResize() {
        HashTable<Integer, String> smallTable = new HashTable<>(4);

        for (int i = 0; i < 10; i++) {
            smallTable.put(i, "value" + i);
        }

        assertEquals(10, smallTable.size());

        for (int i = 0; i < 10; i++) {
            assertEquals("value" + i, smallTable.get(i));
            assertTrue(smallTable.containsKey(i));
        }
    }

    @Test
    void testIteration() {
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key3", 3);

        int count = 0;
        for (Entry<String, Integer> entry : hashTable) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    void testIterator() {
        hashTable.put("a", 1);
        hashTable.put("b", 2);

        Iterator<Entry<String, Integer>> iterator = hashTable.iterator();

        assertTrue(iterator.hasNext());
        Entry<String, Integer> first = iterator.next();
        assertNotNull(first);

        assertTrue(iterator.hasNext());
        Entry<String, Integer> second = iterator.next();
        assertNotNull(second);

        assertFalse(iterator.hasNext());

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testConcurrentModificationException() {
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key3", 3);

        assertThrows(ConcurrentModificationException.class, () -> {
            for (Entry<String, Integer> entry : hashTable) {
                if (entry.getKey().equals("key2")) {
                    hashTable.put("newkey", 10); // Модификация во время итерации
                }
            }
        });
    }

    @Test
    void testConcurrentModificationExceptionOnRemove() {
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);

        Iterator<Entry<String, Integer>> iterator = hashTable.iterator();
        iterator.next();

        hashTable.remove("key1"); // Модификация после создания итератора

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void testEqualsWithSameTables() {
        HashTable<String, Integer> table1 = new HashTable<>();
        HashTable<String, Integer> table2 = new HashTable<>();

        table1.put("key1", 1);
        table1.put("key2", 2);

        table2.put("key2", 2);
        table2.put("key1", 1);

        assertTrue(table1.equals(table2));
        assertTrue(table2.equals(table1));
    }

    @Test
    void testEqualsWithDifferentTables() {
        HashTable<String, Integer> table1 = new HashTable<>();
        HashTable<String, Integer> table2 = new HashTable<>();

        table1.put("key1", 1);
        table2.put("key1", 2);

        assertFalse(table1.equals(table2));

        table2.put("key2", 2);
        assertFalse(table1.equals(table2));
    }

    @Test
    void testEqualsWithNullAndOtherTypes() {
        hashTable.put("key1", 1);

        assertFalse(hashTable.equals(null));
        assertFalse(hashTable.equals("string"));
        assertFalse(hashTable.equals(42));

        assertTrue(hashTable.equals(hashTable)); // сравнение с самим собой
    }

    @Test
    void testEqualsWithNullValues() {
        HashTable<String, Integer> table1 = new HashTable<>();
        HashTable<String, Integer> table2 = new HashTable<>();

        table1.put("key1", null);
        table1.put(null, 1);

        table2.put(null, 1);
        table2.put("key1", null);

        assertTrue(table1.equals(table2));
    }

    @Test
    void testToString() {
        assertTrue(hashTable.toString().equals("{}"));

        hashTable.put("key1", 1);
        String result = hashTable.toString();
        assertTrue(result.contains("key1=1"));
        assertTrue(result.startsWith("{"));
        assertTrue(result.endsWith("}"));

        hashTable.put("key2", 2);
        result = hashTable.toString();
        assertTrue(result.contains("key1=1"));
        assertTrue(result.contains("key2=2"));
        assertTrue(result.contains(","));
    }

    @Test
    void testToStringWithNulls() {
        hashTable.put(null, 1);
        hashTable.put("key", null);

        String result = hashTable.toString();
        assertTrue(result.contains("null=1"));
        assertTrue(result.contains("key=null"));
    }

    @Test
    void testCollisions() {
        HashTable<String, Integer> smallTable = new HashTable<>(2);

        smallTable.put("key1", 1);
        smallTable.put("key2", 2);
        smallTable.put("key3", 3);
        smallTable.put("key4", 4);

        assertEquals(4, smallTable.size());
        assertEquals(1, smallTable.get("key1"));
        assertEquals(2, smallTable.get("key2"));
        assertEquals(3, smallTable.get("key3"));
        assertEquals(4, smallTable.get("key4"));

        assertEquals(2, smallTable.remove("key2"));
        assertNull(smallTable.get("key2"));
        assertFalse(smallTable.containsKey("key2"));
        assertEquals(3, smallTable.size());

        assertEquals(1, smallTable.get("key1"));
        assertEquals(3, smallTable.get("key3"));
        assertEquals(4, smallTable.get("key4"));
    }

    @Test
    void testLargeDataSet() {
        HashTable<Integer, String> largeTable = new HashTable<>();

        for (int i = 0; i < 1000; i++) {
            largeTable.put(i, "value" + i);
        }

        assertEquals(1000, largeTable.size());

        for (int i = 0; i < 1000; i++) {
            assertEquals("value" + i, largeTable.get(i));
            assertTrue(largeTable.containsKey(i));
        }

        for (int i = 0; i < 1000; i += 2) {
            assertEquals("value" + i, largeTable.remove(i));
        }

        assertEquals(500, largeTable.size());

        for (int i = 0; i < 1000; i++) {
            if (i % 2 == 0) {
                assertNull(largeTable.get(i));
                assertFalse(largeTable.containsKey(i));
            } else {
                assertEquals("value" + i, largeTable.get(i));
                assertTrue(largeTable.containsKey(i));
            }
        }
    }
}
