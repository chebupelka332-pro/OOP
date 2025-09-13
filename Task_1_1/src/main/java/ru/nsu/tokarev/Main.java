package ru.nsu.tokarev;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Heap Sort Demo ---");
        int[] arr = {5, 4, 3, 2, 1};
        System.out.println("Start array: " + Arrays.toString(arr));

        HeapSort.sort(arr);

        System.out.println("Sorted array: " + Arrays.toString(arr));
    }
}