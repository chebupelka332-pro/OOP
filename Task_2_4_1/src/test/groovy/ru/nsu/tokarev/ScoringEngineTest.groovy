package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import ru.nsu.tokarev.core.ScoringEngine
import ru.nsu.tokarev.model.CheckResult
import ru.nsu.tokarev.model.Settings
import ru.nsu.tokarev.model.Task

import java.time.LocalDate

import static org.junit.jupiter.api.Assertions.*

class ScoringEngineTest {

    private Task task(double maxScore, String soft = null, String hard = null) {
        def t = new Task()
        t.id = "test"; t.name = "Test"; t.maxScore = maxScore
        if (soft) t.setSoftDeadline(soft)
        if (hard) t.setHardDeadline(hard)
        return t
    }

    private CheckResult ok(int passed, int failed, int skipped, double extra = 0.0) {
        def r = new CheckResult()
        r.buildSuccess = true; r.docsSuccess = true; r.styleSuccess = true
        r.passedTests = passed; r.failedTests = failed; r.skippedTests = skipped
        r.extraPoints = extra
        return r
    }

    @Test
    void buildFailGivesZero() {
        def r = new CheckResult()
        r.buildSuccess = false
        assertEquals(0.0, ScoringEngine.calculate(task(1.0), r, null, new Settings()), 0.001)
    }

    @Test
    void docsFailGivesZero() {
        def r = ok(5, 0, 0)
        r.docsSuccess = false
        assertEquals(0.0, ScoringEngine.calculate(task(1.0), r, null, new Settings()), 0.001)
    }

    @Test
    void styleFailGivesZero() {
        def r = ok(5, 0, 0)
        r.styleSuccess = false
        assertEquals(0.0, ScoringEngine.calculate(task(1.0), r, null, new Settings()), 0.001)
    }

    @Test
    void noDeadlineFullScore() {
        assertEquals(2.0, ScoringEngine.calculate(task(2.0), ok(5, 0, 0), null, new Settings()), 0.001)
    }

    @Test
    void beforeSoftDeadlineFullFactor() {
        def t = task(2.0, "2030-01-01", "2030-06-01")
        assertEquals(2.0, ScoringEngine.calculate(t, ok(1, 0, 0), LocalDate.of(2026, 1, 1), new Settings()), 0.001)
    }

    @Test
    void betweenDeadlinesHalfFactor() {
        def t = task(2.0, "2025-01-01", "2030-01-01")
        assertEquals(1.0, ScoringEngine.calculate(t, ok(1, 0, 0), LocalDate.of(2026, 1, 1), new Settings()), 0.001)
    }

    @Test
    void afterHardDeadlineOnlyExtraPoints() {
        def t = task(2.0, "2020-01-01", "2021-01-01")
        assertEquals(1.0, ScoringEngine.calculate(t, ok(5, 0, 0, 1.0), LocalDate.of(2026, 1, 1), new Settings()), 0.001)
    }

    @Test
    void partialTestsPassRate() {
        assertEquals(3.0, ScoringEngine.calculate(task(4.0), ok(3, 1, 0), null, new Settings()), 0.001)
    }

    @Test
    void skippedTestsCountInTotal() {
        assertEquals(2.0, ScoringEngine.calculate(task(4.0), ok(2, 0, 2), null, new Settings()), 0.001)
    }

    @Test
    void noTestsGivesMaxScore() {
        assertEquals(1.0, ScoringEngine.calculate(task(1.0), ok(0, 0, 0), null, new Settings()), 0.001)
    }

    @Test
    void extraPointsAlwaysAdded() {
        def t = task(1.0, "2030-01-01")
        assertEquals(1.5, ScoringEngine.calculate(t, ok(1, 0, 0, 0.5), LocalDate.of(2026, 1, 1), new Settings()), 0.001)
    }

    @Test
    void commitDateNullWhenNosoftDeadline() {
        assertEquals(1.0, ScoringEngine.calculate(task(1.0), ok(1, 0, 0), LocalDate.of(2020, 1, 1), new Settings()), 0.001)
    }

    @Test
    void gradeExcellent() {
        assertEquals("отлично", ScoringEngine.grade(8.5, new Settings(), 0.0))
    }

    @Test
    void gradeGood() {
        assertEquals("хорошо", ScoringEngine.grade(7.0, new Settings(), 0.0))
    }

    @Test
    void gradeSatisfactory() {
        assertEquals("удовлетворительно", ScoringEngine.grade(5.0, new Settings(), 0.0))
    }

    @Test
    void gradeFail() {
        assertEquals("-", ScoringEngine.grade(1.0, new Settings(), 0.0))
    }

    @Test
    void activityBonusPushesGrade() {
        def s = new Settings()
        s.activityBonusEnabled = true
        s.activityThreshold = 0.8
        s.activityBonus = 1.0

        assertEquals("отлично", ScoringEngine.grade(7.0, s, 0.9))
    }

    @Test
    void activityBonusNotAppliedBelowThreshold() {
        def s = new Settings()
        s.activityBonusEnabled = true
        s.activityThreshold = 0.8
        s.activityBonus = 1.0
        assertEquals("хорошо", ScoringEngine.grade(7.0, s, 0.5))
    }

    @Test
    void activityBonusDisabled() {
        def s = new Settings()
        s.activityBonusEnabled = false
        s.activityBonus = 10.0
        assertEquals("хорошо", ScoringEngine.grade(7.0, s, 1.0))
    }
}
