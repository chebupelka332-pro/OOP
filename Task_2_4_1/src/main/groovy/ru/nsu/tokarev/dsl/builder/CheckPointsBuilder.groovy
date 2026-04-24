package ru.nsu.tokarev.dsl.builder

import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.model.CheckPoint

class CheckPointsBuilder {
    private final CheckerConfig config

    CheckPointsBuilder(CheckerConfig config) { this.config = config }

    void checkpoint(Closure cl) {
        def cp = new CheckPoint()
        def proxy = new CheckPointProxy(cp)
        cl.delegate = proxy
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        config.checkpoints << cp
    }
}

class CheckPointProxy {
    private final CheckPoint cp

    CheckPointProxy(CheckPoint cp) { this.cp = cp }

    void setName(String name) { cp.name = name }
    void setDate(String date) { cp.setDate(date) }

    void tasks(String... ids) { ids.each { cp.addTaskId(it) } }
    void setTasks(List<String> ids) { ids.each { cp.addTaskId(it) } }
}
