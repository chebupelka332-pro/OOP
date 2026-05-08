package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.nsu.tokarev.core.TaskChecker
import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.model.Group
import ru.nsu.tokarev.model.Settings
import ru.nsu.tokarev.model.Student
import ru.nsu.tokarev.model.Task

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class TaskCheckerTest {

    @TempDir
    Path tempDir

    private PrintStream originalOut
    private ByteArrayOutputStream capturedOut

    @BeforeEach
    void captureStdout() {
        originalOut = System.out
        capturedOut = new ByteArrayOutputStream()
        System.setOut(new PrintStream(capturedOut, true, "UTF-8"))
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut)
    }

    private CheckerConfig buildConfig(String workDirPath, String nick, String taskId) {
        def config = new CheckerConfig()

        def student = new Student()
        student.nick = nick
        student.fullName = "Test Student"
        student.repo = "https://dummy.invalid"

        def group = new Group()
        group.name = "test-group"
        group.addStudent(student)

        def task = new Task()
        task.id = taskId
        task.name = "Test Task"
        task.maxScore = 10.0

        def settings = new Settings()
        settings.workDir = workDirPath
        settings.checkStyleEnabled = false
        settings.timeout = 30

        config.groups = [group]
        config.tasks = [task]
        config.settings = settings
        return config
    }

    /** Инициализирует git-репозиторий в указанной директории и делает initial commit. */
    private void initGitRepo(File repoDir) {
        [
            ["git", "init"],
            ["git", "config", "user.email", "test@test.com"],
            ["git", "config", "user.name", "Test"],
            ["git", "add", "."],
            ["git", "commit", "-m", "init", "--allow-empty"]
        ].each { cmd ->
            new ProcessBuilder(cmd)
                .directory(repoDir)
                .redirectErrorStream(true)
                .start()
                .waitFor()
        }
    }

    /**
     * Создаёт директорию задачи с фиктивным gradlew.
     * Скрипт завершается с заданными кодами для команд build, javadoc и test.
     */
    private File createTaskDir(File repoDir, String taskId,
                               int buildExit, int docsExit = 0, int testExit = 0) {
        def taskDir = new File(repoDir, "Task_${taskId}")
        taskDir.mkdirs()

        def gradlew = new File(taskDir, "gradlew")
        gradlew.text = """\
#!/bin/sh
case "\$2" in
    build)   exit ${buildExit} ;;
    javadoc) exit ${docsExit} ;;
    test)    exit ${testExit} ;;
    *)       exit 0 ;;
esac
"""
        gradlew.setExecutable(true)
        return taskDir
    }

    /** Создаёт фиктивные JUnit XML-результаты в ожидаемом каталоге. */
    private void createTestResults(File taskDir, int passed, int failed) {
        def resultsDir = new File(taskDir, "build/test-results/test")
        resultsDir.mkdirs()
        def passedXml = passed > 0
            ? (1..passed).collect { "  <testcase name=\"test${it}\" classname=\"Suite\" time=\"0.001\"/>" }.join('\n')
            : ''
        def failedXml = failed > 0
            ? (1..failed).collect {
                "  <testcase name=\"fail${it}\" classname=\"Suite\" time=\"0.001\">" +
                "<failure message=\"assert\"/></testcase>"
              }.join('\n')
            : ''
        new File(resultsDir, "TEST-Suite.xml").text = """\
<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="Suite" tests="${passed + failed}" errors="0" failures="${failed}" skipped="0">
${passedXml}
${failedXml}
</testsuite>
"""
    }

    // --- тесты ---

    @Test
    void successfulTaskHasPassingTestsAndPositiveScore() {
        def workDir = tempDir.toFile()
        def repoDir = new File(workDir, "student1")
        repoDir.mkdirs()
        def taskDir = createTaskDir(repoDir, "1_1", 0)
        createTestResults(taskDir, 3, 0)
        initGitRepo(repoDir)

        def results = new TaskChecker(buildConfig(workDir.absolutePath, "student1", "1_1")).checkAll()

        assertEquals(1, results.size())
        def taskResult = results.values().first().values().first()
        assertTrue(taskResult.buildSuccess)
        assertTrue(taskResult.docsSuccess)
        assertEquals(3, taskResult.passedTests)
        assertEquals(0, taskResult.failedTests)
        assertTrue(taskResult.score > 0.0)
    }

    @Test
    void buildFailureProducesZeroScore() {
        def workDir = tempDir.toFile()
        def repoDir = new File(workDir, "student2")
        repoDir.mkdirs()
        createTaskDir(repoDir, "1_1", 1)
        initGitRepo(repoDir)

        def results = new TaskChecker(buildConfig(workDir.absolutePath, "student2", "1_1")).checkAll()

        def taskResult = results.values().first().values().first()
        assertFalse(taskResult.buildSuccess)
        assertEquals(0.0, taskResult.score, 0.001)
    }

    @Test
    void failingTestsReduceScore() {
        def workDir = tempDir.toFile()
        def repoDir = new File(workDir, "student3")
        repoDir.mkdirs()
        def taskDir = createTaskDir(repoDir, "1_1", 0)
        createTestResults(taskDir, 1, 2)   // 1 прошёл, 2 упали
        initGitRepo(repoDir)

        def results = new TaskChecker(buildConfig(workDir.absolutePath, "student3", "1_1")).checkAll()

        def taskResult = results.values().first().values().first()
        assertTrue(taskResult.buildSuccess)
        assertEquals(1, taskResult.passedTests)
        assertEquals(2, taskResult.failedTests)

        assertTrue(taskResult.score < 10.0)
        assertTrue(taskResult.score > 0.0)
    }

    @Test
    void unavailableRepoProducesErrorMessage() {
        def workDir = tempDir.toFile()

        def results = new TaskChecker(buildConfig(workDir.absolutePath, "ghost", "1_1")).checkAll()

        def taskResult = results.values().first().values().first()
        assertFalse(taskResult.buildSuccess)
        assertNotNull(taskResult.errorMessage)
        assertTrue(taskResult.errorMessage.contains("Репозиторий"))
    }

    @Test
    void missingTaskDirProducesNotFoundError() {
        def workDir = tempDir.toFile()
        def repoDir = new File(workDir, "student5")
        repoDir.mkdirs()
        // Директорию задачи не создаём
        initGitRepo(repoDir)

        def results = new TaskChecker(buildConfig(workDir.absolutePath, "student5", "1_1")).checkAll()

        def taskResult = results.values().first().values().first()
        assertFalse(taskResult.buildSuccess)
        assertTrue(taskResult.errorMessage.contains("не найдена"))
    }

    @Test
    void fullApplicationRunProducesHtmlReport() {
        def workDir = tempDir.toFile()

        def repoDir = new File(workDir, "int-student")
        repoDir.mkdirs()
        def taskDir = createTaskDir(repoDir, "1_1", 0)
        createTestResults(taskDir, 5, 0)
        initGitRepo(repoDir)

        def configFile = new File(workDir, "checker.groovy")
        configFile.text = """\
groups {
    group {
        name = "int-group"
        students {
            student {
                nick = "int-student"
                fullName = "Integration Tester"
                repo = "https://dummy.invalid"
            }
        }
    }
}
tasks {
    task {
        id = "1_1"
        name = "Integration Task"
        maxScore = 10.0
    }
}
settings {
    workDir = "${workDir.absolutePath.replace('\\', '/')}"
    checkStyleEnabled = false
    timeout = 30
}
"""

        Main.main("test", "--config", configFile.absolutePath)

        def html = capturedOut.toString("UTF-8")
        assertTrue(html.contains("<!DOCTYPE html>"), "Отчёт должен быть HTML-документом")
        assertTrue(html.contains("Integration Tester"), "Отчёт должен содержать имя студента")
        assertTrue(html.contains("Integration Task"), "Отчёт должен содержать название задачи")
        assertTrue(html.contains("int-group"), "Отчёт должен содержать название группы")
        assertTrue(html.contains("</html>"), "HTML должен быть закрыт")
    }

    @Test
    void allTasksCheckedWhenRunInParallel() {
        def workDir = tempDir.toFile()
        def repoDir = new File(workDir, "student6")
        repoDir.mkdirs()

        def taskIds = ["1_1", "1_2", "1_3"]
        taskIds.each { id ->
            def taskDir = createTaskDir(repoDir, id, 0)
            createTestResults(taskDir, 2, 0)
        }
        initGitRepo(repoDir)

        def config = new CheckerConfig()
        def student = new Student()
        student.nick = "student6"
        student.fullName = "Test Student"
        student.repo = "https://dummy.invalid"
        def group = new Group()
        group.name = "test"
        group.addStudent(student)
        taskIds.each { id ->
            def task = new Task()
            task.id = id
            task.name = "Task ${id}"
            task.maxScore = 10.0
            config.tasks << task
        }
        def settings = new Settings()
        settings.workDir = workDir.absolutePath
        settings.checkStyleEnabled = false
        settings.timeout = 30
        config.groups = [group]
        config.settings = settings

        def results = new TaskChecker(config).checkAll()

        assertEquals(1, results.size())
        def studentResults = results.values().first()
        assertEquals(3, studentResults.size())
        studentResults.values().each { taskResult ->
            assertTrue(taskResult.buildSuccess, "Все задачи должны пройти сборку")
            assertEquals(2, taskResult.passedTests)
        }
    }
}
