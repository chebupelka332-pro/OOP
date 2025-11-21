package ru.nsu.tokarev;

import ru.nsu.tokarev.HashTable.HashTable;
import ru.nsu.tokarev.HashTable.Entry;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

class Main {
    public static void main(String[] args) {
        basicExample();

        differentTypesExample();

        iterationExample();

        comparisonExample();

        resizeExample();
    }

    private static void basicExample() {
        System.out.println("1. Basic operations:");

        HashTable<String, Number> hashTable = new HashTable<>();

        hashTable.put("one", 1);
        hashTable.put("two", 2);
        hashTable.put("pi", 3.14159);

        System.out.println("After adding: " + hashTable);
        System.out.println("Size: " + hashTable.size());

        hashTable.update("one", 1.0);
        System.out.println("After update('one', 1.0): " + hashTable.get("one"));

        System.out.println("Value 'pi': " + hashTable.get("pi"));
        System.out.println("Value 'nonexistent': " + hashTable.get("nonexistent"));

        System.out.println("Contains 'two': " + hashTable.containsKey("two"));
        System.out.println("Contains 'three': " + hashTable.containsKey("three"));

        Number removed = hashTable.remove("two");
        System.out.println("Removed 'two': " + removed);
        System.out.println("After removal: " + hashTable);

        System.out.println();
    }

    private static void differentTypesExample() {
        System.out.println("2. Working with different data types:");

        HashTable<Integer, String> students = new HashTable<>();
        students.put(101, "Alexey Ivanov");
        students.put(102, "Maria Petrova");
        students.put(103, "Dmitry Sidorov");

        System.out.println("Students: " + students);

        HashTable<String, Object> settings = new HashTable<>();
        settings.put("theme", "dark");
        settings.put("fontSize", 14);
        settings.put("autoSave", true);
        settings.put("lastLoginTime", System.currentTimeMillis());

        System.out.println("Settings: " + settings);

        HashTable<String, Integer> wordCount = new HashTable<>();
        String[] words = {"java", "hash", "table", "java", "example", "hash", "java"};

        for (String word : words) {
            Integer count = wordCount.get(word);
            wordCount.put(word, (count == null) ? 1 : count + 1);
        }

        System.out.println("Word count: " + wordCount);

        System.out.println();
    }

    private static void iterationExample() {
        System.out.println("3. Iteration and ConcurrentModificationException:");

        HashTable<String, Integer> scores = new HashTable<>();
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        scores.put("Diana", 88);

        System.out.println("Exam results:");
        for (Entry<String, Integer> entry : scores) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " points");
        }

        System.out.println("\nIteration through Iterator:");
        Iterator<Entry<String, Integer>> iterator = scores.iterator();
        while (iterator.hasNext()) {
            Entry<String, Integer> entry = iterator.next();
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("\nConcurrentModificationException demonstration:");
        try {
            for (Entry<String, Integer> entry : scores) {
                System.out.println("Processing: " + entry.getKey());
                if (entry.getKey().equals("Bob")) {
                    scores.put("Eve", 90);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Caught exception: " + e.getClass().getSimpleName());
        }

        System.out.println();
    }

    private static void comparisonExample() {
        System.out.println("4. Hash table comparison:");

        HashTable<String, String> map1 = new HashTable<>();
        map1.put("name", "HashTable");
        map1.put("type", "DataStructure");
        map1.put("complexity", "O(1)");

        HashTable<String, String> map2 = new HashTable<>();
        map2.put("complexity", "O(1)");
        map2.put("name", "HashTable");
        map2.put("type", "DataStructure");

        HashTable<String, String> map3 = new HashTable<>();
        map3.put("name", "HashMap");
        map3.put("type", "DataStructure");
        map3.put("complexity", "O(1)");

        System.out.println("Table 1: " + map1);
        System.out.println("Table 2: " + map2);
        System.out.println("Table 3: " + map3);

        System.out.println("map1.equals(map2): " + map1.equals(map2));
        System.out.println("map1.equals(map3): " + map1.equals(map3));
        System.out.println("map1.equals(null): " + map1.equals(null));
        System.out.println("map1.equals(\"string\"): " + map1.equals("string"));

        System.out.println();
    }

    private static void resizeExample() {
        System.out.println("5. Automatic resizing:");

        HashTable<Integer, String> bigMap = new HashTable<>(4); // Small initial capacity

        System.out.println("Adding elements to demonstrate resize...");

        for (int i = 0; i < 15; i++) {
            bigMap.put(i, "value" + i);
            if (i % 5 == 4) {
                System.out.println("After adding " + (i + 1) + " elements: size = " + bigMap.size());
            }
        }

        System.out.println("Final table contains " + bigMap.size() + " elements");

        System.out.println("Sample elements:");
        int count = 0;
        for (Entry<Integer, String> entry : bigMap) {
            if (count < 5) {
                System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
                count++;
            } else {
                System.out.println("  ... and " + (bigMap.size() - 5) + " more elements");
                break;
            }
        }

        System.out.println("Checking availability of all elements:");
        boolean allPresent = true;
        for (int i = 0; i < 15; i++) {
            if (!bigMap.containsKey(i)) {
                allPresent = false;
                System.out.println("Element " + i + " not found!");
            }
        }
        System.out.println("All elements available: " + allPresent);
    }
}
