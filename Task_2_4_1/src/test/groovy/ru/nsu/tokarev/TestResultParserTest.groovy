package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.nsu.tokarev.core.TestResultParser
import ru.nsu.tokarev.model.CheckResult

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.*

class TestResultParserTest {

    @TempDir
    Path tempDir

    private File makeResultsDir(String subPath = "build/test-results/test") {
        def dir = tempDir.resolve(subPath).toFile()
        dir.mkdirs()
        return dir
    }

    private void writeXml(File dir, String name, int tests, int failures, int errors, int skipped) {
        new File(dir, "${name}.xml").text = """<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="${name}" tests="${tests}" failures="${failures}" errors="${errors}" skipped="${skipped}">
</testsuite>"""
    }

    @Test
    void parsesPassedFailedSkipped() {
        def resultsDir = makeResultsDir()
        writeXml(resultsDir, "TestSuite", 10, 2, 1, 1)

        def result = new CheckResult()
        TestResultParser.parseInto(tempDir.toFile(), result)

        // passed = tests - failures - errors - skipped = 10 - 2 - 1 - 1 = 6
        assertEquals(6, result.passedTests)
        assertEquals(3, result.failedTests)  // failures + errors
        assertEquals(1, result.skippedTests)
    }

    @Test
    void parsesMultipleXmlFiles() {
        def resultsDir = makeResultsDir()
        writeXml(resultsDir, "Suite1", 5, 0, 0, 0)
        writeXml(resultsDir, "Suite2", 3, 1, 0, 0)

        def result = new CheckResult()
        TestResultParser.parseInto(tempDir.toFile(), result)

        assertEquals(7, result.passedTests)
        assertEquals(1, result.failedTests)
        assertEquals(0, result.skippedTests)
    }

    @Test
    void missingDirLeavesResultUntouched() {
        def result = new CheckResult()
        result.passedTests = 99

        TestResultParser.parseInto(tempDir.toFile(), result)

        // no results dir → nothing changed
        assertEquals(99, result.passedTests)
    }

    @Test
    void fallbackToBuildTestResults() {
        // create build/test-results (without /test subdirectory)
        def resultsDir = makeResultsDir("build/test-results")
        writeXml(resultsDir, "Suite", 4, 1, 0, 0)

        def result = new CheckResult()
        TestResultParser.parseInto(tempDir.toFile(), result)

        assertEquals(3, result.passedTests)
        assertEquals(1, result.failedTests)
    }

    @Test
    void allTestsPass() {
        def resultsDir = makeResultsDir()
        writeXml(resultsDir, "Suite", 5, 0, 0, 0)

        def result = new CheckResult()
        TestResultParser.parseInto(tempDir.toFile(), result)

        assertEquals(5, result.passedTests)
        assertEquals(0, result.failedTests)
        assertEquals(0, result.skippedTests)
    }
}
