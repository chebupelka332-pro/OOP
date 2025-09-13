package ru.nsu.tokarev;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class HeapSortTest {

    @Test
    void testEmptyArray() {
        int[] arr = {};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testSingleElementArray() {
        int[] arr = {1};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{1}, arr);
    }

    @Test
    void testAlreadySortedArray() {
        int[] arr = {1, 2, 3, 4, 5};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testReverseSortedArray() {
        int[] arr = {5, 4, 3, 2, 1};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testArrayWithDuplicates() {
        int[] arr = {4, 1, 3, 4, 2, 1};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 4}, arr);
    }

    @Test
    void testRandomArray() {
        int[] arr = {45, 12, 89, 3, 76, 51, 9, 23};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{3, 9, 12, 23, 45, 51, 76, 89}, arr);
    }
}