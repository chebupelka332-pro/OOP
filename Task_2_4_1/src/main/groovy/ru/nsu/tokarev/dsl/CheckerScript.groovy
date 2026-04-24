package ru.nsu.tokarev.dsl

import org.codehaus.groovy.control.CompilerConfiguration
import ru.nsu.tokarev.dsl.builder.*

abstract class CheckerScript extends Script {
    CheckerConfig config = new CheckerConfig()
    File scriptFile

    void include(String fileName) {
        def file = new File(scriptFile.parentFile, fileName)
        if (!file.exists()) throw new FileNotFoundException("Включаемый файл не найден: ${file.absolutePath}")
        def cc = new CompilerConfiguration(scriptBaseClass: CheckerScript.canonicalName)
        def shell = new GroovyShell(this.class.classLoader, new Binding(), cc)
        def included = shell.parse(file) as CheckerScript
        included.config = this.config
        included.scriptFile = file
        included.run()
    }

    void tasks(Closure cl) {
        def builder = new TasksBuilder(config)
        cl.delegate = builder
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }

    void groups(Closure cl) {
        def builder = new GroupsBuilder(config)
        cl.delegate = builder
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }

    void checkpoints(Closure cl) {
        def builder = new CheckPointsBuilder(config)
        cl.delegate = builder
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }

    void check(Closure cl) {
        def builder = new CheckBuilder(config)
        cl.delegate = builder
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }

    void settings(Closure cl) {
        def builder = new SettingsBuilder(config)
        cl.delegate = builder
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }
}
