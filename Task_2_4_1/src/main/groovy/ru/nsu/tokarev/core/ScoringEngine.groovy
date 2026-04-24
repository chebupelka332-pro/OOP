package ru.nsu.tokarev.core

import ru.nsu.tokarev.model.CheckResult
import ru.nsu.tokarev.model.Settings
import ru.nsu.tokarev.model.Task

import java.time.LocalDate

class ScoringEngine {
    static double calculate(Task task, CheckResult result, LocalDate commitDate, Settings settings) {
        if (!result.buildSuccess) return 0.0
        if (!result.docsSuccess || !result.styleSuccess) return 0.0

        double factor = deadlineFactor(task, commitDate)

        int total = result.passedTests + result.failedTests + result.skippedTests
        double base = total > 0
            ? (result.passedTests / (double) total) * task.maxScore
            : task.maxScore

        // extraPoints начисляются всегда, независимо от дедлайна
        return base * factor + result.extraPoints
    }

    static String grade(double totalScore, Settings settings, double activityRatio) {
        double score = totalScore
        if (settings.activityBonusEnabled && activityRatio >= settings.activityThreshold) {
            score += settings.activityBonus
        }
        return settings.gradeScale.grade(score)
    }

    private static double deadlineFactor(Task task, LocalDate commitDate) {
        if (commitDate == null || task.softDeadline == null) return 1.0
        if (!commitDate.isAfter(task.softDeadline)) return 1.0
        if (task.hardDeadline != null && commitDate.isAfter(task.hardDeadline)) return 0.0
        return 0.5
    }
}
