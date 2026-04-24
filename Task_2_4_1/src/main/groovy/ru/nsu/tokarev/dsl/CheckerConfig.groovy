package ru.nsu.tokarev.dsl

import ru.nsu.tokarev.model.*

class CheckerConfig {
    List<Task> tasks = []
    List<Group> groups = []
    List<CheckPoint> checkpoints = []
    Settings settings = new Settings()

    List<String> checkGroups = []
    List<String> checkStudents = []
    List<String> checkTasks = []

    List<Student> getStudentsToCheck() {
        def all = groups.collectMany { it.students }
        if (checkStudents) return all.findAll { it.nick in checkStudents }
        if (checkGroups) return groups.findAll { it.name in checkGroups }.collectMany { it.students }
        return all
    }

    List<Task> getTasksToCheck() {
        if (checkTasks) return tasks.findAll { it.id in checkTasks }
        return tasks
    }

    Task findTask(String id) {
        tasks.find { it.id == id }
    }

    Group findGroupForStudent(String nick) {
        groups.find { g -> g.students.any { it.nick == nick } }
    }
}
