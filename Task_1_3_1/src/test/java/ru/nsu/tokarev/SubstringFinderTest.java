package ru.nsu.tokarev;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nsu.tokarev.SubstringFinder.SubstringFinder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubstringFinderTest {

    @TempDir
    Path tempDir;

    private List<Path> createdFiles = new ArrayList<>();

    @BeforeEach
    void setUp() {
        createdFiles.clear();
    }

    @AfterEach
    void tearDown() throws IOException {
        for (Path file : createdFiles) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete test file " + file + ": " + e.getMessage());
            }
        }
        createdFiles.clear();
    }

    @Test
    void testSimpleSubstringSearch() throws IOException {
        Path testFile = createTestFile("Hello world! This is a test.");

        List<Integer> results = SubstringFinder.find(testFile.toString(), "test");

        assertEquals(1, results.size());
        assertEquals(23, results.get(0));
    }

    @Test
    void testNonExistentSubstring() throws IOException {
        Path testFile = createTestFile("Hello world!");

        List<Integer> results = SubstringFinder.find(testFile.toString(), "xyz");

        assertTrue(results.isEmpty());
    }

    @Test
    void testMultipleOccurrences() throws IOException {
        Path testFile = createTestFile("test test test");

        List<Integer> results = SubstringFinder.find(testFile.toString(), "test");

        assertEquals(3, results.size());
        assertEquals(0, results.get(0));
        assertEquals(5, results.get(1));
        assertEquals(10, results.get(2));
    }

    @Test
    void testOverlappingSubstrings() throws IOException {
        Path testFile = createTestFile("aaaa");

        List<Integer> results = SubstringFinder.find(testFile.toString(), "aa");

        assertEquals(3, results.size());
        assertEquals(0, results.get(0));
        assertEquals(1, results.get(1));
        assertEquals(2, results.get(2));
    }

    @Test
    void testEmptyFile() throws IOException {
        Path testFile = createTestFile("");

        List<Integer> results = SubstringFinder.find(testFile.toString(), "test");

        assertTrue(results.isEmpty());
    }

    @Test
    void testEmptyPattern() throws IOException {
        Path testFile = createTestFile("Hello world!");

        List<Integer> results = SubstringFinder.find(testFile.toString(), "");

        assertTrue(results.isEmpty());
    }

    @Test
    void testWithUTF8Encoding() throws IOException {
        Path testFile = createTestFile("Привет мир! Это тест.", StandardCharsets.UTF_8);

        List<Integer> results = SubstringFinder.find(testFile.toString(), "тест", StandardCharsets.UTF_8);

        assertEquals(1, results.size());
        assertTrue(results.get(0) >= 0);
    }

    @Test
    void testLargeFile() throws IOException {
        StringBuilder content = new StringBuilder();
        String pattern = "SEARCH_ME";

        for (int i = 0; i < 100000; i++) {
            content.append("This is line number ").append(i).append(". ");
            if (i == 50000) {
                content.append(pattern);
            }
        }

        Path testFile = createTestFile(content.toString());

        List<Integer> results = SubstringFinder.find(testFile.toString(), pattern);

        assertEquals(1, results.size());
        assertTrue(results.get(0) > 0);
    }

    @Test
    void testPatternAcrossBufferBoundary() throws IOException {
        StringBuilder content = new StringBuilder();
        String pattern = "BOUNDARY_PATTERN";

        // Заполняем почти до размера буфера
        int bufferSize = 1024 * 1024; // 1MB
        String filler = "x";

        // Размещаем паттерн так, чтобы он полностью помещался в первом буфере
        int patternPosition = bufferSize - pattern.length() - 100;

        for (int i = 0; i < patternPosition; i++) {
            content.append(filler);
        }

        content.append(pattern);

        for (int i = 0; i < 1000; i++) {
            content.append(filler);
        }

        Path testFile = createTestFile(content.toString());

        List<Integer> results = SubstringFinder.find(testFile.toString(), pattern);

        assertEquals(1, results.size());
        assertEquals(patternPosition, results.get(0));
    }

    @Test
    void testSingleCharacterPattern() throws IOException {
        Path testFile = createTestFile("abcdefghijk");

        List<Integer> results = SubstringFinder.find(testFile.toString(), "e");

        assertEquals(1, results.size());
        assertEquals(4, results.get(0));
    }

    @Test
    void testPatternEqualsFileContent() throws IOException {
        String content = "exact match";
        Path testFile = createTestFile(content);

        List<Integer> results = SubstringFinder.find(testFile.toString(), content);

        assertEquals(1, results.size());
        assertEquals(0, results.get(0));
    }

    @Test
    void testDifferentEncodings() throws IOException {
        String content = "Test content with special chars: äöü";

        Path testFileUTF8 = createTestFile(content, StandardCharsets.UTF_8);
        List<Integer> resultsUTF8 = SubstringFinder.find(testFileUTF8.toString(), "äöü", StandardCharsets.UTF_8);
        assertEquals(1, resultsUTF8.size());

        Path testFileISO = createTestFile(content, StandardCharsets.ISO_8859_1);
        List<Integer> resultsISO = SubstringFinder.find(testFileISO.toString(), "äöü", StandardCharsets.ISO_8859_1);
        assertEquals(1, resultsISO.size());
    }

    @Test
    void testNonExistentFile() {
        assertThrows(IOException.class, () -> {
            SubstringFinder.find("non_existent_file.txt", "test");
        });
    }

    @Test
    void testEmojiAndJapaneseCharacters() throws IOException {
        String content = "Hello 😊 world 😊 test 😊 end";
        String pattern = "😊";

        Path testFile = createTestFile(content, StandardCharsets.UTF_8);
        List<Integer> results = SubstringFinder.find(testFile.toString(), pattern, StandardCharsets.UTF_8);

        assertEquals(3, results.size());
        assertTrue(results.get(0) >= 0);
        assertTrue(results.get(1) > results.get(0));
        assertTrue(results.get(2) > results.get(1));

        String japaneseContent = "こんにちは world こんにちは test";
        String japanesePattern = "こんにちは";
        Path japaneseTestFile = createTestFile(japaneseContent, StandardCharsets.UTF_8);
        List<Integer> japaneseResults = SubstringFinder.find(japaneseTestFile.toString(), japanesePattern, StandardCharsets.UTF_8);

        assertEquals(2, japaneseResults.size());
        assertEquals(0, japaneseResults.get(0));
        assertTrue(japaneseResults.get(1) > japaneseResults.get(0));
    }

    @Test
    void testVeryLargeFile() throws IOException {
        Path largeTestFile = tempDir.resolve("large_test_file.txt");
        createdFiles.add(largeTestFile);

        String pattern = "LARGE_FILE_SEARCH_TARGET";
        String block = "This is a repeated block of text that will be written many times to create a large file for testing. ";

        long targetSizeMB = 50L * 1024 * 1024; // 50 МБ
        long blockSize = block.getBytes(StandardCharsets.UTF_8).length;
        long blocksNeeded = targetSizeMB / blockSize;

        long patternInsertBlock = blocksNeeded / 2;

        try (java.io.BufferedWriter writer = Files.newBufferedWriter(largeTestFile, StandardCharsets.UTF_8)) {
            for (long i = 0; i < blocksNeeded; i++) {
                writer.write(block);

                if (i == patternInsertBlock) {
                    writer.write(pattern);
                }

                if (i % 100000 == 0 && i > 0) {
                    System.out.println("Written " + (i * blockSize / (1024 * 1024)) + " MB");
                }
            }
        }

        try {
            long fileSize = Files.size(largeTestFile);
            assertTrue(fileSize > 25L * 1024 * 1024, "File should be larger than 25MB");
            System.out.println("Created test file of size: " + (fileSize / (1024 * 1024)) + " MB");

            long startTime = System.currentTimeMillis();
            List<Integer> results = SubstringFinder.find(largeTestFile.toString(), pattern, StandardCharsets.UTF_8);
            long searchTime = System.currentTimeMillis() - startTime;

            assertEquals(1, results.size());
            assertTrue(results.get(0) > 0);
            System.out.println("Pattern found at position: " + results.get(0));
            System.out.println("Search completed in " + searchTime + " ms");
        } finally {
            try {
                Files.deleteIfExists(largeTestFile);
                createdFiles.remove(largeTestFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete large test file immediately: " + e.getMessage());
            }
        }
    }

    private Path createTestFile(String content) throws IOException {
        return createTestFile(content, StandardCharsets.UTF_8);
    }

    private Path createTestFile(String content, Charset encoding) throws IOException {
        Path testFile = tempDir.resolve("test_file.txt");
        Files.write(testFile, content.getBytes(encoding));
        createdFiles.add(testFile);
        return testFile;
    }
}
