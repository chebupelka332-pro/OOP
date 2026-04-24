package ru.nsu.tokarev.dsl.builder

import ru.nsu.tokarev.dsl.CheckerConfig

class CheckBuilder {
    private final CheckerConfig config

    CheckBuilder(CheckerConfig config) { this.config = config }

    void groups(String... names) { config.checkGroups.addAll(names) }
    void setGroups(List<String> names) { config.checkGroups.addAll(names) }

    void students(String... nicks) { config.checkStudents.addAll(nicks) }
    void setStudents(List<String> nicks) { config.checkStudents.addAll(nicks) }

    void tasks(String... ids) { config.checkTasks.addAll(ids) }
    void setTasks(List<String> ids) { config.checkTasks.addAll(ids) }
}
