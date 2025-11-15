package ru.nsu.tokarev;

import ru.nsu.tokarev.SubstringFinder.SubstringFinder;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            createTestFile();

            // Поиск простой подстроки
            List<Integer> results = SubstringFinder.find("test_file.txt", "test");
            System.out.println("Found 'test' positions: " + results);

            // Поиск с указанием кодировки
            results = SubstringFinder.find("test_file.txt", "Hello", StandardCharsets.UTF_8);
            System.out.println("Found 'Hello' positions: " + results);

            // Поиск несуществующей подстроки
            results = SubstringFinder.find("test_file.txt", "xyz");
            System.out.println("Found 'xyz' positions: " + results);

            // Поиск подстроки, встречающейся несколько раз
            results = SubstringFinder.find("test_file.txt", "is");
            System.out.println("Found 'is' positions: " + results);

            // Поиск одного символа
            results = SubstringFinder.find("test_file.txt", "o");
            System.out.println("Found 'o' positions: " + results);

        } catch (IOException e) {
            System.err.println("Error with file work: " + e.getMessage());
        }
    }

    private static void createTestFile() throws IOException {
        try (FileWriter writer = new FileWriter("test_file.txt")) {
            writer.write("Hello world! This is a test file. This test file contains test data. " +
                    "Testing is important for software quality. Test early, test often!");
        }
        System.out.println("Test file was created: test_file.txt");
    }
}
