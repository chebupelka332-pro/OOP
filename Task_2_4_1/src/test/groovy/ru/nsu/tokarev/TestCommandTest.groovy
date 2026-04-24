package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import org.codehaus.groovy.control.CompilerConfiguration
import ru.nsu.tokarev.commands.TestCommand
import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.dsl.CheckerScript

import static org.junit.jupiter.api.Assertions.*

class TestCommandTest {

    private CheckerConfig emptyConfig() {
        def cc = new CompilerConfiguration(scriptBaseClass: CheckerScript.canonicalName)
        def shell = new GroovyShell(this.class.classLoader, new Binding(), cc)
        def s = shell.parse("") as CheckerScript
        s.scriptFile = new File(".")
        s.run()
        return s.config
    }

    @Test
    void executeWithNoStudentsProducesHtml() {
        def config = emptyConfig()

        def oldOut = System.out
        def baos = new java.io.ByteArrayOutputStream()
        System.setOut(new java.io.PrintStream(baos))
        try {
            TestCommand.execute(config)
        } finally {
            System.setOut(oldOut)
        }

        def html = baos.toString()
        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("OOP Checker"))
    }

    @Test
    void executeWithTasksButNoStudents() {
        def cc = new CompilerConfiguration(scriptBaseClass: CheckerScript.canonicalName)
        def shell = new GroovyShell(this.class.classLoader, new Binding(), cc)
        def s = shell.parse("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
        """) as CheckerScript
        s.scriptFile = new File(".")
        s.run()
        def config = s.config

        def oldOut = System.out
        def baos = new java.io.ByteArrayOutputStream()
        System.setOut(new java.io.PrintStream(baos))
        try {
            TestCommand.execute(config)
        } finally {
            System.setOut(oldOut)
        }

        def html = baos.toString()
        assertTrue(html.contains("<!DOCTYPE html>"))
    }
}
