package ru.nsu.tokarev.core

import ru.nsu.tokarev.model.Student

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.logging.Logger

class GitClient {
    private static final Logger log = Logger.getLogger(GitClient.class.name)
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    static File cloneOrPull(Student student, File workDir) {
        def repoDir = new File(workDir, student.nick)
        if (repoDir.exists()) {
            run(["git", "fetch", "--all"], repoDir)
            run(["git", "checkout", "main"], repoDir)
            def result = run(["git", "pull", "--ff-only"], repoDir)
            if (result.exitCode != 0) {
                run(["git", "checkout", "master"], repoDir)
                run(["git", "pull", "--ff-only"], repoDir)
            }
        } else {
            workDir.mkdirs()
            def result = run(["git", "clone", student.repo, repoDir.absolutePath], workDir)
            if (result.exitCode != 0) {
                throw new RuntimeException("Не удалось клонировать репозиторий ${student.repo}:\n${result.output}")
            }
        }
        return repoDir
    }

    static LocalDate getLastCommitDate(File repoDir, String taskDir) {
        def args = ["git", "log", "HEAD", "--format=%ci", "-1"]
        if (taskDir) args += ["--", taskDir]
        def result = run(args, repoDir)
        if (result.exitCode != 0 || result.output.trim().isEmpty()) return null
        def dateStr = result.output.trim().substring(0, 10)
        return LocalDate.parse(dateStr, DATE_FMT)
    }

    static List<LocalDate> getAllCommitDates(File repoDir) {
        def result = run(["git", "log", "HEAD", "--format=%ci"], repoDir)
        if (result.exitCode != 0) return []
        return result.output.trim().split("\n")
            .findAll { it.trim() }
            .collect { line -> LocalDate.parse(line.trim().substring(0, 10), DATE_FMT) }
    }

    static double computeWeeklyActivity(File repoDir, LocalDate semesterStart, LocalDate semesterEnd) {
        def dates = getAllCommitDates(repoDir)
        if (dates.isEmpty()) return 0.0
        def end = semesterEnd ?: LocalDate.now()
        def start = semesterStart ?: dates.min()

        def activeWeeks = dates
            .findAll { !it.isBefore(start) && !it.isAfter(end) }
            .collect { getWeekKey(it) }
            .toSet()

        long totalWeeks = java.time.temporal.ChronoUnit.WEEKS.between(start, end) + 1
        return totalWeeks > 0 ? (activeWeeks.size() / (double) totalWeeks) : 0.0
    }

    private static String getWeekKey(LocalDate date) {
        def wf = java.time.temporal.WeekFields.ISO
        "${date.get(wf.weekBasedYear())}-W${date.get(wf.weekOfWeekBasedYear())}"
    }

    static ProcessResult run(List<String> cmd, File dir) {
        def pb = new ProcessBuilder(cmd)
            .directory(dir)
            .redirectErrorStream(true)
        def proc = pb.start()
        def output = proc.inputStream.text
        def exitCode = proc.waitFor()
        return new ProcessResult(exitCode: exitCode, output: output)
    }
}
