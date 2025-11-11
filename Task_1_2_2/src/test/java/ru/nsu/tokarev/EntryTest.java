package ru.nsu.tokarev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.nsu.tokarev.HashTable.Entry;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {
    
    private Entry<String, Integer> entry;
    private Entry<String, Integer> nextEntry;
    
    @BeforeEach
    void setUp() {
        nextEntry = new Entry<>("next", 200, "next".hashCode(), null);
        entry = new Entry<>("key", 100, "key".hashCode(), nextEntry);
    }
    
    @Test
    void testEntryCreation() {
        Entry<String, Integer> newEntry = new Entry<>("test", 42, "test".hashCode(), null);
        
        assertEquals("test", newEntry.getKey());
        assertEquals(42, newEntry.getValue());
        assertEquals("test".hashCode(), newEntry.getHash());
        assertNull(newEntry.getNext());
    }
    
    @Test
    void testEntryCreationWithNullKey() {
        Entry<String, Integer> nullKeyEntry = new Entry<>(null, 42, 0, null);
        
        assertNull(nullKeyEntry.getKey());
        assertEquals(42, nullKeyEntry.getValue());
        assertEquals(0, nullKeyEntry.getHash());
        assertNull(nullKeyEntry.getNext());
    }
    
    @Test
    void testEntryCreationWithNullValue() {
        Entry<String, Integer> nullValueEntry = new Entry<>("key", null, "key".hashCode(), null);
        
        assertEquals("key", nullValueEntry.getKey());
        assertNull(nullValueEntry.getValue());
        assertEquals("key".hashCode(), nullValueEntry.getHash());
        assertNull(nullValueEntry.getNext());
    }
    
    @Test
    void testGetters() {
        assertEquals("key", entry.getKey());
        assertEquals(100, entry.getValue());
        assertEquals("key".hashCode(), entry.getHash());
        assertEquals(nextEntry, entry.getNext());
    }
    
    @Test
    void testSetValue() {
        assertEquals(100, entry.getValue());
        
        entry.setValue(500);
        assertEquals(500, entry.getValue());
        
        entry.setValue(null);
        assertNull(entry.getValue());
    }
    
    @Test
    void testSetNext() {
        Entry<String, Integer> newNext = new Entry<>("new", 300, "new".hashCode(), null);
        
        assertEquals(nextEntry, entry.getNext());
        
        entry.setNext(newNext);
        assertEquals(newNext, entry.getNext());
        
        entry.setNext(null);
        assertNull(entry.getNext());
    }
    
    @Test
    void testKeyImmutability() {
        String originalKey = entry.getKey();
        assertEquals("key", originalKey);

        assertEquals("key", entry.getKey());
    }
    
    @Test
    void testHashImmutability() {
        int originalHash = entry.getHash();
        assertEquals("key".hashCode(), originalHash);
        
        // Значение хеша должно оставаться неизменным
        assertEquals("key".hashCode(), entry.getHash());
    }
    
    @Test
    void testEntryChain() {
        Entry<String, Integer> third = new Entry<>("third", 300, "third".hashCode(), null);
        Entry<String, Integer> second = new Entry<>("second", 200, "second".hashCode(), third);
        Entry<String, Integer> first = new Entry<>("first", 100, "first".hashCode(), second);

        assertEquals("first", first.getKey());
        assertEquals(100, first.getValue());
        assertEquals(second, first.getNext());
        
        assertEquals("second", first.getNext().getKey());
        assertEquals(200, first.getNext().getValue());
        assertEquals(third, first.getNext().getNext());
        
        assertEquals("third", first.getNext().getNext().getKey());
        assertEquals(300, first.getNext().getNext().getValue());
        assertNull(first.getNext().getNext().getNext());
    }
    
    @Test
    void testDifferentDataTypes() {
        Entry<Integer, String> intEntry = new Entry<>(42, "value", 42, null);
        assertEquals(42, intEntry.getKey());
        assertEquals("value", intEntry.getValue());

        Entry<Double, Boolean> doubleEntry = new Entry<>(3.14, true, Double.valueOf(3.14).hashCode(), null);
        assertEquals(3.14, doubleEntry.getKey());
        assertTrue(doubleEntry.getValue());

        Object keyObj = new Object();
        Object valueObj = new Object();
        Entry<Object, Object> objectEntry = new Entry<>(keyObj, valueObj, keyObj.hashCode(), null);
        assertEquals(keyObj, objectEntry.getKey());
        assertEquals(valueObj, objectEntry.getValue());
    }
    
    @Test
    void testChainModification() {
        Entry<String, Integer> fourth = new Entry<>("fourth", 400, "fourth".hashCode(), null);
        Entry<String, Integer> third = new Entry<>("third", 300, "third".hashCode(), fourth);
        Entry<String, Integer> second = new Entry<>("second", 200, "second".hashCode(), third);
        Entry<String, Integer> first = new Entry<>("first", 100, "first".hashCode(), second);

        second.setValue(999);
        assertEquals(999, first.getNext().getValue());

        second.setNext(null);
        assertNull(first.getNext().getNext());

        Entry<String, Integer> newEntry = new Entry<>("new", 555, "new".hashCode(), null);
        second.setNext(newEntry);
        assertEquals(newEntry, first.getNext().getNext());
        assertEquals("new", first.getNext().getNext().getKey());
    }
    
    @Test
    void testChainWithNulls() {
        Entry<String, Integer> nullKeyEntry = new Entry<>(null, 100, 0, null);
        Entry<String, Integer> nullValueEntry = new Entry<>("key", null, "key".hashCode(), nullKeyEntry);
        
        assertEquals("key", nullValueEntry.getKey());
        assertNull(nullValueEntry.getValue());
        assertEquals(nullKeyEntry, nullValueEntry.getNext());
        
        assertNull(nullValueEntry.getNext().getKey());
        assertEquals(100, nullValueEntry.getNext().getValue());
        assertNull(nullValueEntry.getNext().getNext());
    }
}
