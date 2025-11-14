package ru.nsu.tokarev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nsu.tokarev.SubstringFinder.SubstringFinder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubstringFinderTest {

    private SubstringFinder substringFinder;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        substringFinder = new SubstringFinder();
    }

    @Test
    void testSimpleSubstringSearch() throws IOException {
        Path testFile = createTestFile("Hello world! This is a test.");
        
        List<Integer> results = substringFinder.find(testFile.toString(), "test");
        
        assertEquals(1, results.size());
        assertEquals(23, results.get(0));
    }

    @Test
    void testNonExistentSubstring() throws IOException {
        Path testFile = createTestFile("Hello world!");
        
        List<Integer> results = substringFinder.find(testFile.toString(), "xyz");
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testMultipleOccurrences() throws IOException {
        Path testFile = createTestFile("test test test");
        
        List<Integer> results = substringFinder.find(testFile.toString(), "test");
        
        assertEquals(3, results.size());
        assertEquals(0, results.get(0));
        assertEquals(5, results.get(1));
        assertEquals(10, results.get(2));
    }

    @Test
    void testOverlappingSubstrings() throws IOException {
        Path testFile = createTestFile("aaaa");
        
        List<Integer> results = substringFinder.find(testFile.toString(), "aa");
        
        assertEquals(3, results.size());
        assertEquals(0, results.get(0));
        assertEquals(1, results.get(1));
        assertEquals(2, results.get(2));
    }

    @Test
    void testEmptyFile() throws IOException {
        Path testFile = createTestFile("");
        
        List<Integer> results = substringFinder.find(testFile.toString(), "test");
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testEmptyPattern() throws IOException {
        Path testFile = createTestFile("Hello world!");
        
        List<Integer> results = substringFinder.find(testFile.toString(), "");
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testWithUTF8Encoding() throws IOException {
        Path testFile = createTestFile("Привет мир! Это тест.", StandardCharsets.UTF_8);
        
        List<Integer> results = substringFinder.find(testFile.toString(), "тест", StandardCharsets.UTF_8);
        
        assertEquals(1, results.size());
        assertTrue(results.get(0) >= 0);
    }

    @Test
    void testLargeFile() throws IOException {
        // Создаем файл размером больше буфера (1MB)
        StringBuilder content = new StringBuilder();
        String pattern = "SEARCH_ME";
        
        // Добавляем данные до 2MB
        for (int i = 0; i < 100000; i++) {
            content.append("This is line number ").append(i).append(". ");
            if (i == 50000) {
                content.append(pattern); // Вставляем паттерн посередине
            }
        }
        
        Path testFile = createTestFile(content.toString());
        
        List<Integer> results = substringFinder.find(testFile.toString(), pattern);
        
        assertEquals(1, results.size());
        assertTrue(results.get(0) > 0);
    }

    @Test
    void testPatternAcrossBufferBoundary() throws IOException {
        // Создаем контент, где паттерн может оказаться на границе буфера
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
        
        List<Integer> results = substringFinder.find(testFile.toString(), pattern);
        
        assertEquals(1, results.size());
        assertEquals(patternPosition, results.get(0));
    }

    @Test
    void testSingleCharacterPattern() throws IOException {
        Path testFile = createTestFile("abcdefghijk");
        
        List<Integer> results = substringFinder.find(testFile.toString(), "e");
        
        assertEquals(1, results.size());
        assertEquals(4, results.get(0));
    }

    @Test
    void testPatternEqualsFileContent() throws IOException {
        String content = "exact match";
        Path testFile = createTestFile(content);
        
        List<Integer> results = substringFinder.find(testFile.toString(), content);
        
        assertEquals(1, results.size());
        assertEquals(0, results.get(0));
    }

    @Test
    void testDifferentEncodings() throws IOException {
        String content = "Test content with special chars: äöü";

        Path testFileUTF8 = createTestFile(content, StandardCharsets.UTF_8);
        List<Integer> resultsUTF8 = substringFinder.find(testFileUTF8.toString(), "äöü", StandardCharsets.UTF_8);
        assertEquals(1, resultsUTF8.size());

        Path testFileISO = createTestFile(content, StandardCharsets.ISO_8859_1);
        List<Integer> resultsISO = substringFinder.find(testFileISO.toString(), "äöü", StandardCharsets.ISO_8859_1);
        assertEquals(1, resultsISO.size());
    }

    @Test
    void testNonExistentFile() {
        assertThrows(IOException.class, () -> {
            substringFinder.find("non_existent_file.txt", "test");
        });
    }

    private Path createTestFile(String content) throws IOException {
        return createTestFile(content, StandardCharsets.UTF_8);
    }

    private Path createTestFile(String content, Charset encoding) throws IOException {
        Path testFile = tempDir.resolve("test_file.txt");
        Files.write(testFile, content.getBytes(encoding));
        return testFile;
    }
}
