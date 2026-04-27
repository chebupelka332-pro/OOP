package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import ru.nsu.tokarev.core.ProcessResult
import ru.nsu.tokarev.model.*

import java.time.LocalDate

import static org.junit.jupiter.api.Assertions.*

class ModelTest {
    @Test
    void checkResultTestsString() {
        def r = new CheckResult()
        r.passedTests = 5; r.failedTests = 2; r.skippedTests = 1
        assertEquals("5/2/1", r.testsString())
    }

    @Test
    void checkResultZeroTests() {
        def r = new CheckResult()
        assertEquals("0/0/0", r.testsString())
    }

    @Test
    void checkResultGettersSetters() {
        def r = new CheckResult()
        r.buildSuccess = true
        r.docsSuccess = true
        r.styleSuccess = false
        r.extraPoints = 1.5
        r.score = 2.0
        r.buildLog = "log"
        r.errorMessage = "err"
        assertTrue(r.buildSuccess)
        assertTrue(r.docsSuccess)
        assertFalse(r.styleSuccess)
        assertEquals(1.5, r.extraPoints, 0.001)
        assertEquals(2.0, r.score, 0.001)
        assertEquals("log", r.buildLog)
        assertEquals("err", r.errorMessage)
    }

    @Test
    void processResultSuccess() {
        def r = new ProcessResult(exitCode: 0, output: "ok")
        assertTrue(r.success)
    }

    @Test
    void processResultFailure() {
        def r = new ProcessResult(exitCode: 1, output: "error")
        assertFalse(r.success)
    }

    @Test
    void taskDateSetViaString() {
        def t = new Task()
        t.setSoftDeadline("2026-03-01")
        t.setHardDeadline("2026-04-01")
        assertEquals(LocalDate.of(2026, 3, 1), t.softDeadline)
        assertEquals(LocalDate.of(2026, 4, 1), t.hardDeadline)
    }

    @Test
    void taskDateSetViaLocalDate() {
        def t = new Task()
        t.setSoftDeadline(LocalDate.of(2026, 5, 1))
        assertEquals(LocalDate.of(2026, 5, 1), t.softDeadline)
    }

    @Test
    void taskToString() {
        def t = new Task()
        t.id = "2_1_1"; t.name = "Test"; t.maxScore = 1.0
        assertTrue(t.toString().contains("2_1_1"))
    }

    @Test
    void studentGettersSetters() {
        def s = new Student()
        s.nick = "nick1"
        s.fullName = "Full Name"
        s.repo = "https://github.com/nick1/OOP"
        assertEquals("nick1", s.nick)
        assertEquals("Full Name", s.fullName)
        assertEquals("https://github.com/nick1/OOP", s.repo)
        assertTrue(s.toString().contains("nick1"))
    }

    @Test
    void groupAddStudent() {
        def g = new Group()
        g.name = "12345"
        def s = new Student()
        s.nick = "s1"
        g.addStudent(s)
        assertEquals(1, g.students.size())
        assertTrue(g.toString().contains("12345"))
    }

    @Test
    void groupSetStudents() {
        def g = new Group()
        g.students = [new Student(), new Student()]
        assertEquals(2, g.students.size())
    }


    @Test
    void checkPointDateViaString() {
        def cp = new CheckPoint()
        cp.setDate("2026-03-20")
        assertEquals(LocalDate.of(2026, 3, 20), cp.date)
    }

    @Test
    void checkPointAddTaskId() {
        def cp = new CheckPoint()
        cp.name = "КТ1"
        cp.addTaskId("2_1_1")
        cp.addTaskId("2_2_1")
        assertEquals(["2_1_1", "2_2_1"], cp.taskIds)
    }

    @Test
    void checkPointSetTaskIds() {
        def cp = new CheckPoint()
        cp.taskIds = ["2_1_1"]
        assertEquals(1, cp.taskIds.size())
    }

    @Test
    void extraPointsEntryGetters() {
        def e = new ExtraPointsEntry("nick1", "2_1_1", 0.5)
        assertEquals("nick1", e.studentNick)
        assertEquals("2_1_1", e.taskId)
        assertEquals(0.5, e.points, 0.001)
    }

    @Test
    void settingsDefaults() {
        def s = new Settings()
        assertEquals("./repos", s.workDir)
        assertEquals(120, s.timeout)
        assertTrue(s.checkStyleEnabled)
        assertFalse(s.activityBonusEnabled)
        assertEquals(0.8, s.activityThreshold, 0.001)
        assertEquals(0.5, s.activityBonus, 0.001)
    }

    @Test
    void settingsExtraPointsSum() {
        def s = new Settings()
        s.addExtraPoints(new ExtraPointsEntry("nick1", "2_1_1", 0.5))
        s.addExtraPoints(new ExtraPointsEntry("nick1", "2_1_1", 0.3))
        s.addExtraPoints(new ExtraPointsEntry("nick2", "2_1_1", 1.0))
        assertEquals(0.8, s.getExtraPointsFor("nick1", "2_1_1"), 0.001)
        assertEquals(1.0, s.getExtraPointsFor("nick2", "2_1_1"), 0.001)
        assertEquals(0.0, s.getExtraPointsFor("nick1", "2_2_1"), 0.001)
    }

    @Test
    void settingsCheckStyleFlag() {
        def s = new Settings()
        s.checkStyleEnabled = false
        assertFalse(s.checkStyleEnabled)
    }

    @Test
    void settingsActivityFields() {
        def s = new Settings()
        s.activityBonusEnabled = true
        s.activityThreshold = 0.7
        s.activityBonus = 1.0
        assertTrue(s.activityBonusEnabled)
        assertEquals(0.7, s.activityThreshold, 0.001)
        assertEquals(1.0, s.activityBonus, 0.001)
    }
}
