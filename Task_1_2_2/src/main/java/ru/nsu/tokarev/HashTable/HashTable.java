package ru.nsu.tokarev.HashTable;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashTable<K, V> implements Iterable<Entry<K, V>> {
    private class HashTableIterator implements Iterator<Entry<K, V>> {
        private int expectedModCount;
        private int currentBucket;
        private Entry<K, V> nextEntry;

        HashTableIterator() {
            this.expectedModCount = modCount;
            this.currentBucket = 0;
            this.nextEntry = null;

            while (currentBucket < buckets.length) {
                if (buckets[currentBucket] != null) {
                    nextEntry = buckets[currentBucket];
                    break;
                }
                currentBucket++;
            }
        }

        public boolean hasNext() {
            return nextEntry != null;
        }

        public Entry<K, V> next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<K, V> entryToReturn = nextEntry;

            if (nextEntry.getNext() != null) {
                nextEntry = nextEntry.getNext();
            } else {
                currentBucket++;
                nextEntry = null;
                while (currentBucket < buckets.length) {
                    if (buckets[currentBucket] != null) {
                        nextEntry = buckets[currentBucket];
                        break;
                    }
                    currentBucket++;
                }
            }

            return entryToReturn;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;
    private int modCount;

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than zero.");
        }
        this.buckets = new Entry[initialCapacity];
        this.capacity = initialCapacity;
        this.size = 0;
        this.modCount = 0;
    }

    public V put(K key, V value) {
        int hash = (key == null) ? 0 : key.hashCode();
        int bucketIndex = Math.abs(hash) % capacity;

        Entry<K, V> current = buckets[bucketIndex];
        while(current != null) {
            if (current.getHash() == hash && (current.getKey() == key || (key != null
                    && key.equals(current.getKey())))) {
                V oldValue = current.getValue();
                current.setValue(value);
                return oldValue;
            }
            current = current.getNext();
        }

        Entry<K, V> newEntry = new Entry<>(key, value, hash, buckets[bucketIndex]);
        buckets[bucketIndex] = newEntry;
        size++;
        modCount++;

        if ((float)size / capacity >= LOAD_FACTOR) {
            resize();
        }

        return null;
    }

    public V get(K key) {
        int hash = (key == null) ? 0 : key.hashCode();
        int bucketIndex = Math.abs(hash) % capacity;

        Entry<K, V> current = buckets[bucketIndex];
        while(current != null) {
            if (current.getHash() == hash && (current.getKey() == key || (key != null
                    && key.equals(current.getKey())))) {
                return current.getValue();
            }
            current = current.getNext();
        }

        return null;
    }

    public V remove(K key) {
        int hash = (key == null) ? 0 : key.hashCode();
        int bucketIndex = Math.abs(hash) % capacity;

        Entry<K, V> current = buckets[bucketIndex];
        Entry<K, V> previous = null;

        while(current != null) {
            if (current.getHash() == hash && (current.getKey() == key || (key != null && key.equals(current.getKey())))) {
                if (previous == null) {
                    buckets[bucketIndex] = current.getNext();
                } else {
                    previous.setNext(current.getNext());
                }
                size--;
                modCount++;
                return current.getValue();
            }
            previous = current;
            current = current.getNext();
        }

        return null;
    }

    public V update(K key, V value) {
        return put(key, value);
    }

    public boolean containsKey(K key) {
        int hash = (key == null) ? 0 : key.hashCode();
        int bucketIndex = Math.abs(hash) % capacity;

        Entry<K, V> current = buckets[bucketIndex];
        while(current != null) {
            if (current.getHash() == hash && (current.getKey() == key || (key != null
                    && key.equals(current.getKey())))) {
                return true;
            }
            current = current.getNext();
        }

        return false;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;

        int newCapacity = capacity * 2;
        buckets = new Entry[newCapacity];
        capacity = newCapacity;
        size = 0;

        modCount++;

        for (Entry<K, V> entry : oldBuckets) {
            while (entry != null) {
                Entry<K, V> next = entry.getNext();
                int newIndex = Math.abs(entry.getHash()) % newCapacity;
                entry.setNext(buckets[newIndex]);

                buckets[newIndex] = entry;
                size++;
                entry = next;
            }
        }
    }

    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new HashTableIterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        Iterator<Entry<K, V>> it = iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            
            sb.append(entry.getKey() == this ? "(this Map)" : entry.getKey());
            sb.append("=");
            sb.append(entry.getValue() == this ? "(this Map)" : entry.getValue());
            
            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        
        if (ob == null || !(ob instanceof HashTable)) {
            return false;
        }

        HashTable<K, V> other = (HashTable<K, V>) ob;

        if (this.size != other.size()) {
            return false;
        }

        try {
            for (Entry<K, V> entry : this) {
                K key = entry.getKey();
                V value = entry.getValue();

                Object otherValue = other.get(key);

                if (value == null) {
                    if (otherValue != null || !other.containsKey(key)) {
                        return false;
                    }
                } else {
                    if (!value.equals(otherValue)) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }

        return true;
    }
}