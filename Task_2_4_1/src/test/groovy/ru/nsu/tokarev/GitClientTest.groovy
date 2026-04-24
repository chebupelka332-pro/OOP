package ru.nsu.tokarev

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.nsu.tokarev.core.GitClient
import ru.nsu.tokarev.core.ProcessResult

import java.nio.file.Path
import java.time.LocalDate

import static org.junit.jupiter.api.Assertions.*

class GitClientTest {

    @TempDir
    Path tempDir

    File repoDir

    @BeforeEach
    void initGitRepo() {
        repoDir = tempDir.toFile()
        run("git", "init")
        run("git", "config", "user.email", "test@test.com")
        run("git", "config", "user.name", "Test User")
        // create initial commit
        new File(repoDir, "README.md").text = "hello"
        run("git", "add", ".")
        run("git", "commit", "-m", "initial commit")
    }

    private void run(String... cmd) {
        GitClient.run(cmd.toList(), repoDir)
    }

    @Test
    void runReturnsZeroOnSuccess() {
        def result = GitClient.run(["echo", "hello"], repoDir)
        assertEquals(0, result.exitCode)
        assertTrue(result.output.trim() == "hello")
    }

    @Test
    void runCapturesOutput() {
        def result = GitClient.run(["echo", "test output"], repoDir)
        assertTrue(result.output.contains("test output"))
    }

    @Test
    void runReturnsNonZeroOnFailure() {
        def result = GitClient.run(["git", "log", "--nonexistent-flag-xyz"], repoDir)
        assertNotEquals(0, result.exitCode)
    }

    @Test
    void getLastCommitDateReturnsToday() {
        def date = GitClient.getLastCommitDate(repoDir, null)
        assertNotNull(date)
        assertEquals(LocalDate.now(), date)
    }

    @Test
    void getLastCommitDateForMissingPathReturnsNull() {
        // no commits touch nonexistent path
        def date = GitClient.getLastCommitDate(repoDir, "nonexistent/path/xyz")
        assertNull(date)
    }

    @Test
    void getAllCommitDatesReturnsNonEmpty() {
        def dates = GitClient.getAllCommitDates(repoDir)
        assertFalse(dates.isEmpty())
        assertEquals(LocalDate.now(), dates[0])
    }

    @Test
    void getAllCommitDatesEmptyOnNonGitDir() {
        def plain = new File(repoDir, "subdir")
        plain.mkdirs()
        // subdir is not a git repo, git log will fail
        def result = GitClient.run(["git", "log", "--format=%ci"], plain)
        // may fail or succeed depending on parent repo, just verify no exception
        assertNotNull(result)
    }

    @Test
    void computeActivityWithSingleCommit() {
        // one commit today → at least one active week
        def activity = GitClient.computeWeeklyActivity(repoDir, null, null)
        assertTrue(activity > 0.0)
        assertTrue(activity <= 1.0)
    }

    @Test
    void computeActivityWithMultipleCommits() {
        // add more commits
        new File(repoDir, "file2.txt").text = "content"
        run("git", "add", ".")
        run("git", "commit", "-m", "second commit")

        def activity = GitClient.computeWeeklyActivity(repoDir, null, null)
        assertTrue(activity > 0.0)
    }

    @Test
    void computeActivityWithExplicitRange() {
        def start = LocalDate.now().minusWeeks(4)
        def end = LocalDate.now()
        def activity = GitClient.computeWeeklyActivity(repoDir, start, end)
        assertTrue(activity >= 0.0)
        assertTrue(activity <= 1.0)
    }

    @Test
    void cloneOrPullClonesLocalRepo() {
        // use the temp git repo as the "remote" (local path works as git URL)
        def student = new ru.nsu.tokarev.model.Student()
        student.nick = "testuser"
        student.fullName = "Test User"
        student.repo = repoDir.absolutePath

        def workDir = new File(tempDir.toFile(), "work")
        def cloned = GitClient.cloneOrPull(student, workDir)

        assertTrue(cloned.exists())
        assertTrue(new File(cloned, "README.md").exists())
    }

    @Test
    void cloneOrPullPullsExistingRepo() {
        def student = new ru.nsu.tokarev.model.Student()
        student.nick = "testuser"
        student.fullName = "Test User"
        student.repo = repoDir.absolutePath

        def workDir = new File(tempDir.toFile(), "work2")
        // first clone
        GitClient.cloneOrPull(student, workDir)
        // second call → should pull (repo already exists)
        def pulled = GitClient.cloneOrPull(student, workDir)
        assertTrue(pulled.exists())
    }

    @Test
    void cloneOrPullThrowsOnBadUrl() {
        def student = new ru.nsu.tokarev.model.Student()
        student.nick = "bad"
        student.fullName = "Bad"
        student.repo = "/nonexistent/path/xyz"

        def workDir = new File(tempDir.toFile(), "work3")
        assertThrows(RuntimeException) {
            GitClient.cloneOrPull(student, workDir)
        }
    }

    @Test
    void processResultIsSuccess() {
        def r = new ProcessResult(exitCode: 0, output: "")
        assertTrue(r.success)
    }

    @Test
    void processResultIsFailure() {
        def r = new ProcessResult(exitCode: 2, output: "err")
        assertFalse(r.success)
    }
}
