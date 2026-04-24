package ru.nsu.tokarev.dsl.builder

import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.model.Task

class TasksBuilder {
    private final CheckerConfig config

    TasksBuilder(CheckerConfig config) { this.config = config }

    void task(Closure cl) {
        def t = new Task()
        cl.delegate = t
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        config.tasks << t
    }
}
