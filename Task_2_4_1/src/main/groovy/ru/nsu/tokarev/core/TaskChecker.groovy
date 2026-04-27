package ru.nsu.tokarev.core

import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.model.CheckResult
import ru.nsu.tokarev.model.Student
import ru.nsu.tokarev.model.Task

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.logging.Logger

class TaskChecker {
    private static final Logger log = Logger.getLogger(TaskChecker.class.name)
    private final CheckerConfig config
    private final File workDir

    TaskChecker(CheckerConfig config) {
        this.config = config
        this.workDir = new File(config.settings.workDir)
    }

    Map<Student, Map<Task, CheckResult>> checkAll() {
        def students = config.studentsToCheck
        def tasks = config.tasksToCheck
        def results = new ConcurrentHashMap()
        int nThreads = Math.max(1, tasks.size())
        def executor = Executors.newFixedThreadPool(nThreads)

        try {
            students.each { student ->
                log.info("Обработка студента: ${student.fullName} (${student.nick})")
                File repoDir = null
                try {
                    repoDir = GitClient.cloneOrPull(student, workDir)
                } catch (Exception e) {
                    log.warning("Ошибка клонирования: ${e.message}")
                }

                def repoDirFinal = repoDir
                def studentResults = new ConcurrentHashMap()
                def futures = tasks.collect { task ->
                    executor.submit {
                        try {
                            studentResults[task] = checkTask(student, task, repoDirFinal)
                        } catch (Exception e) {
                            log.severe("Необработанная ошибка: задача ${task.id}, студент ${student.nick}: ${e.message}")
                            def err = new CheckResult()
                            err.buildSuccess = false
                            err.errorMessage = "Внутренняя ошибка: ${e.message}"
                            studentResults[task] = err
                        }
                    }
                }
                futures.each { it.get() }
                results[student] = studentResults
            }
        } finally {
            executor.shutdown()
        }
        return results
    }

    private CheckResult checkTask(Student student, Task task, File repoDir) {
        def result = new CheckResult()
        int timeout = config.settings.timeout

        if (repoDir == null) {
            result.buildSuccess = false
            result.errorMessage = "Репозиторий недоступен"
            return result
        }

        def taskDir = findTaskDir(repoDir, task.id)
        if (taskDir == null) {
            result.buildSuccess = false
            result.errorMessage = "Директория задачи Task_${task.id} не найдена"
            return result
        }

        log.info("  Задача ${task.id}: сборка...")
        def buildResult = GradleRunner.build(taskDir, timeout)
        result.buildSuccess = buildResult.success
        result.buildLog = buildResult.output

        if (!result.buildSuccess) {
            log.warning("  Задача ${task.id}: сборка провалена\n${buildResult.output.readLines().takeRight(10).join('\n')}")
            return result
        }

        log.info("  Задача ${task.id}: документация...")
        def docsResult = GradleRunner.generateDocs(taskDir, timeout)
        result.docsSuccess = docsResult.success

        if (config.settings.checkStyleEnabled) {
            def styleResult = GradleRunner.checkStyle(taskDir, timeout)
            result.styleSuccess = styleResult.success
        } else {
            result.styleSuccess = true
        }

        if (!result.docsSuccess || !result.styleSuccess) {
            log.warning("  Задача ${task.id}: документация/стиль не прошли")
            return result
        }

        log.info("  Задача ${task.id}: тесты...")
        GradleRunner.runTests(taskDir, timeout)
        TestResultParser.parseInto(taskDir, result)

        def commitDate = GitClient.getLastCommitDate(repoDir, "Task_${task.id}")
        result.extraPoints = config.settings.getExtraPointsFor(student.nick, task.id)
        result.score = ScoringEngine.calculate(task, result, commitDate, config.settings)

        log.info("  Задача ${task.id}: балл = ${result.score}")
        return result
    }

    Map<Student, Double> computeActivity() {
        def students = config.studentsToCheck
        def activity = [:]
        students.each { student ->
            File repoDir = new File(workDir, student.nick)
            if (repoDir.exists()) {
                activity[student] = GitClient.computeWeeklyActivity(repoDir, null, null)
            } else {
                activity[student] = 0.0
            }
        }
        return activity
    }

    private File findTaskDir(File repoDir, String taskId) {
        def candidates = [
            new File(repoDir, "Task_${taskId}"),
            new File(repoDir, taskId),
            new File(repoDir, "task_${taskId}"),
        ]
        return candidates.find { it.exists() && it.isDirectory() }
    }
}
