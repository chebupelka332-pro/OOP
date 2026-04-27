package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.nsu.tokarev.core.GradleRunner

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.*

class GradleRunnerTest {

    @TempDir
    Path tempDir

    private File fakeGradlew(int exitCode, String output = "BUILD SUCCESSFUL") {
        def dir = tempDir.toFile()
        def gradlew = new File(dir, "gradlew")
        gradlew.text = "#!/bin/sh\necho '${output}'\nexit ${exitCode}"
        gradlew.setExecutable(true)
        return dir
    }

    @Test
    void nonExistentDirReturnsFailure() {
        def result = GradleRunner.runTask(new File("/nonexistent_dir_xyz"), 30, "build")
        assertFalse(result.success)
        assertTrue(result.output.contains("не найдена"))
    }

    @Test
    void successfulBuild() {
        def dir = fakeGradlew(0, "BUILD SUCCESSFUL")
        def result = GradleRunner.runTask(dir, 30, "build", "-x", "test")
        assertTrue(result.success)
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    void failedBuild() {
        def dir = fakeGradlew(1, "BUILD FAILED")
        def result = GradleRunner.runTask(dir, 30, "build")
        assertFalse(result.success)
    }

    @Test
    void timeoutKillsProcess() {
        def dir = tempDir.toFile()
        def gradlew = new File(dir, "gradlew")
        gradlew.text = "#!/bin/sh\nsleep 60"
        gradlew.setExecutable(true)

        def result = GradleRunner.runTask(dir, 1, "build")
        assertFalse(result.success)
        assertTrue(result.output.contains("Превышено"))
    }

    @Test
    void buildDelegatesToRunTask() {
        def dir = fakeGradlew(0)
        assertTrue(GradleRunner.build(dir, 30).success)
    }

    @Test
    void generateDocsDelegatesToRunTask() {
        def dir = fakeGradlew(0)
        assertTrue(GradleRunner.generateDocs(dir, 30).success)
    }

    @Test
    void checkStyleDelegatesToRunTask() {
        def dir = fakeGradlew(0)
        assertTrue(GradleRunner.checkStyle(dir, 30).success)
    }

    @Test
    void runTestsDelegatesToRunTask() {
        def dir = fakeGradlew(0)
        assertTrue(GradleRunner.runTests(dir, 30).success)
    }

    @Test
    void usesGradleCommandWhenNoGradlew() {
        def dir = tempDir.toFile()
        def result = GradleRunner.runTask(dir, 5, "--version")
        assertNotNull(result)
        assertNotNull(result.output)
    }
}
