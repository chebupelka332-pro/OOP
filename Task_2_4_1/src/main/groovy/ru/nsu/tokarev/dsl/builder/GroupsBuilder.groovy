package ru.nsu.tokarev.dsl.builder

import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.model.Group
import ru.nsu.tokarev.model.Student

class GroupsBuilder {
    private final CheckerConfig config

    GroupsBuilder(CheckerConfig config) { this.config = config }

    void group(Closure cl) {
        def g = new Group()
        def proxy = new GroupProxy(g)
        cl.delegate = proxy
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        config.groups << g
    }
}

class GroupProxy {
    private final Group group

    GroupProxy(Group group) { this.group = group }

    void setName(String name) { group.name = name }

    void students(Closure cl) {
        def builder = new StudentsBuilder(group)
        cl.delegate = builder
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }
}

class StudentsBuilder {
    private final Group group

    StudentsBuilder(Group group) { this.group = group }

    void student(Closure cl) {
        def s = new Student()
        cl.delegate = s
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        group.addStudent(s)
    }
}
