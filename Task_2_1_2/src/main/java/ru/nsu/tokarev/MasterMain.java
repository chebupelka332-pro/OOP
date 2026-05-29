package ru.nsu.tokarev;

import ru.nsu.tokarev.FindCompositeNumber.Finders.DistributedFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.SingleThreadFinder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Запуск с числами явно:
 *   java -jar master.jar [port] [n1 n2 n3 ...]
 *
 * Запуск с генерацией N больших простых:
 *   java -jar master.jar [port] --generate N
 *
 * Запуск с генерацией N-1 простых + 1 составного в конце:
 *   java -jar master.jar [port] --generate-composite N
 *
 * Примеры:
 *   java -jar master.jar 12345 --generate 5000
 *   java -jar master.jar 12345 --generate-composite 5000
 */
public class MasterMain {
    public static void main(String[] args) {
        int port = 12345;
        int[] numbers;

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        if (args.length >= 2 && args[1].equals("--generate")) {
            int count = args.length >= 3 ? Integer.parseInt(args[2]) : 1000;
            numbers = generateLargePrimes(count);
            System.out.println("Generated " + count + " large prime numbers (expected result: false)");
        } else if (args.length >= 2 && args[1].equals("--generate-composite")) {
            int count = args.length >= 3 ? Integer.parseInt(args[2]) : 1000;
            numbers = generatePrimesWithComposite(count);
            System.out.println("Generated " + (count - 1) + " primes + 1 composite (expected result: true)");
        } else if (args.length >= 2) {
            numbers = new int[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                numbers[i - 1] = Integer.parseInt(args[i]);
            }
            System.out.print("Input: [");
            for (int i = 0; i < numbers.length; i++) {
                System.out.print(numbers[i]);
                if (i < numbers.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        } else {
            numbers = new int[]{6, 8, 7, 13, 5, 9, 4};
            System.out.println("Input (default demo): [6, 8, 7, 13, 5, 9, 4]");
        }

        System.out.println("Array size: " + numbers.length);

        try {
            DistributedFinder master = new DistributedFinder(port, 60000);
            int actualPort = master.getPort();
            System.out.println("Master listening on port " + actualPort);
            System.out.println("Start workers: java -jar worker.jar localhost " + actualPort);
            System.out.println("Waiting for workers...");

            long start = System.currentTimeMillis();
            boolean result = master.containsComposite(numbers, new AtomicBoolean(false));
            long elapsed = System.currentTimeMillis() - start;

            System.out.println("Result: " + result + "  (" + elapsed + " ms)");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static int[] generateLargePrimes(int count) {
        int[] primes = new int[count];
        int num = 1_000_000_007;
        int idx = 0;
        while (idx < count) {
            if (SingleThreadFinder.isPrime(num)) primes[idx++] = num;
            num += 2;
        }
        return primes;
    }

    private static int[] generatePrimesWithComposite(int count) {
        int[] arr = generateLargePrimes(count - 1);
        int[] result = new int[count];
        System.arraycopy(arr, 0, result, 0, count - 1);
        result[count - 1] = 1_000_000_008;
        return result;
    }
}
