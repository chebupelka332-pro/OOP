package ru.nsu.tokarev.dsl.builder

import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.model.ExtraPointsEntry
import ru.nsu.tokarev.model.Settings

class SettingsBuilder {
    private final CheckerConfig config
    private final Settings settings

    SettingsBuilder(CheckerConfig config) {
        this.config = config
        this.settings = config.settings
    }

    void setWorkDir(String dir) { settings.workDir = dir }
    void setTimeout(int sec) { settings.timeout = sec }
    void setCheckStyleEnabled(boolean v) { settings.checkStyleEnabled = v }
    void setActivityBonusEnabled(boolean v) { settings.activityBonusEnabled = v }
    void setActivityThreshold(double v) { settings.activityThreshold = v }
    void setActivityBonus(double v) { settings.activityBonus = v }

    void gradeScale(Closure cl) {
        cl.delegate = settings.gradeScale
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }

    void extraPoints(Closure cl) {
        def builder = new ExtraPointsBuilder(settings)
        cl.delegate = builder
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }
}

class ExtraPointsBuilder {
    private final Settings settings

    ExtraPointsBuilder(Settings settings) { this.settings = settings }

    void add(String nick, String taskId, double points) {
        settings.addExtraPoints(new ExtraPointsEntry(nick, taskId, points))
    }
}
