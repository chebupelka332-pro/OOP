package ru.nsu.tokarev.core

import groovy.xml.XmlSlurper
import ru.nsu.tokarev.model.CheckResult

class TestResultParser {
    static void parseInto(File taskDir, CheckResult result) {
        def resultsDir = new File(taskDir, "build/test-results/test")
        if (!resultsDir.exists()) {
            resultsDir = new File(taskDir, "build/test-results")
        }
        if (!resultsDir.exists()) return

        int passed = 0, failed = 0, skipped = 0
        resultsDir.eachFileMatch(~/.*\.xml$/) { File xmlFile ->
            try {
                def suite = new XmlSlurper().parse(xmlFile)
                def tests = suite.@tests.toInteger()
                def failures = suite.@failures.toInteger()
                def errors = suite.@errors.toInteger()
                def skip = suite.@skipped.toInteger()
                failed += failures + errors
                skipped += skip
                passed += tests - failures - errors - skip
            } catch (Exception ignored) {}
        }
        result.passedTests = passed
        result.failedTests = failed
        result.skippedTests = skipped
    }
}
