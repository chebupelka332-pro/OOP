package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import org.codehaus.groovy.control.CompilerConfiguration
import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.dsl.CheckerScript
import ru.nsu.tokarev.model.CheckResult
import ru.nsu.tokarev.model.Student
import ru.nsu.tokarev.model.Task
import ru.nsu.tokarev.report.HtmlReporter

import static org.junit.jupiter.api.Assertions.*

class HtmlReporterTest {

    private CheckerConfig buildConfig(String dsl) {
        def cc = new CompilerConfiguration(scriptBaseClass: CheckerScript.canonicalName)
        def shell = new GroovyShell(this.class.classLoader, new Binding(), cc)
        def s = shell.parse(dsl) as CheckerScript
        s.scriptFile = new File(".")
        s.run()
        return s.config
    }

    private static CheckResult okResult(int passed, double score, double extra = 0.0) {
        def r = new CheckResult()
        r.buildSuccess = true; r.docsSuccess = true; r.styleSuccess = true
        r.passedTests = passed; r.score = score; r.extraPoints = extra
        return r
    }

    private static CheckResult failResult() {
        def r = new CheckResult()
        r.buildSuccess = false
        return r
    }

    private static String generateWith(CheckerConfig config,
                                       Map<Student, Map<Task, CheckResult>> results) {
        new HtmlReporter(config, results, [:]).generate()
    }

    @Test
    void htmlContainsDoctype() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
            groups { group { name="G1"
                students { student { nick="s1"; fullName="Student One"; repo="r" } }
            }}
            check { groups "G1"; tasks "t1" }
        """)
        def student = config.studentsToCheck[0]
        def task = config.tasksToCheck[0]
        def html = generateWith(config, [(student): [(task): okResult(5, 1.0)]])

        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("<title>OOP Checker"))
        assertTrue(html.contains("</html>"))
    }

    @Test
    void htmlContainsGroupName() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
            groups { group { name="ГруппаАльфа"
                students { student { nick="s1"; fullName="Студент"; repo="r" } }
            }}
            check { groups "ГруппаАльфа"; tasks "t1" }
        """)
        def student = config.studentsToCheck[0]
        def task = config.tasksToCheck[0]
        def html = generateWith(config, [(student): [(task): okResult(1, 1.0)]])

        assertTrue(html.contains("ГруппаАльфа"))
    }

    @Test
    void htmlContainsTaskName() {
        def config = buildConfig("""
            tasks { task { id="2_1_1"; name="Простые числа"; maxScore=1.0 } }
            groups { group { name="G"
                students { student { nick="s1"; fullName="S1"; repo="r" } }
            }}
            check { groups "G"; tasks "2_1_1" }
        """)
        def html = generateWith(config, [:])

        assertTrue(html.contains("Простые числа"))
        assertTrue(html.contains("2_1_1"))
    }

    @Test
    void htmlShowsPlusMinus() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
            groups { group { name="G"
                students {
                    student { nick="s1"; fullName="Passed"; repo="r" }
                    student { nick="s2"; fullName="Failed"; repo="r" }
                }
            }}
            check { groups "G"; tasks "t1" }
        """)
        def s1 = config.studentsToCheck.find { it.nick == "s1" }
        def s2 = config.studentsToCheck.find { it.nick == "s2" }
        def task = config.tasksToCheck[0]
        def html = generateWith(config, [
            (s1): [(task): okResult(3, 1.0)],
            (s2): [(task): failResult()],
        ])

        assertTrue(html.contains("class='ok'>+<"))
        assertTrue(html.contains("class='fail'>-<"))
    }

    @Test
    void htmlCheckStyleNaWhenDisabled() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
            groups { group { name="G"
                students { student { nick="s1"; fullName="S1"; repo="r" } }
            }}
            check { groups "G"; tasks "t1" }
            settings { checkStyleEnabled = false }
        """)
        def student = config.studentsToCheck[0]
        def task = config.tasksToCheck[0]
        def html = generateWith(config, [(student): [(task): okResult(1, 1.0)]])

        assertTrue(html.contains("N/A"))
        assertFalse(html.contains("class='na'>+"))
    }

    @Test
    void htmlSummaryTableContainsTotal() {
        def config = buildConfig("""
            tasks {
                task { id="t1"; name="T1"; maxScore=1.0 }
                task { id="t2"; name="T2"; maxScore=1.0 }
            }
            groups { group { name="G"
                students { student { nick="s1"; fullName="Студент"; repo="r" } }
            }}
            check { groups "G"; tasks "t1", "t2" }
        """)
        def student = config.studentsToCheck[0]
        def t1 = config.findTask("t1")
        def t2 = config.findTask("t2")
        def r1 = okResult(1, 1.0)
        def r2 = okResult(1, 0.5)
        def html = generateWith(config, [(student): [(t1): r1, (t2): r2]])

        assertTrue(html.contains("Общая статистика"))
        assertTrue(html.contains("Сумма"))
    }

    @Test
    void htmlCheckpointsTableShownWhenPresent() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
            groups { group { name="G"
                students { student { nick="s1"; fullName="S1"; repo="r" } }
            }}
            checkpoints {
                checkpoint { name="КТ1"; date="2026-03-20"; tasks "t1" }
            }
            check { groups "G"; tasks "t1" }
        """)
        def student = config.studentsToCheck[0]
        def task = config.tasksToCheck[0]
        def html = generateWith(config, [(student): [(task): okResult(1, 1.0)]])

        assertTrue(html.contains("Контрольные точки"))
        assertTrue(html.contains("КТ1"))
    }

    @Test
    void htmlStudentWithNoResults() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
            groups { group { name="G"
                students { student { nick="s1"; fullName="S1"; repo="r" } }
            }}
            check { groups "G"; tasks "t1" }
        """)
        // empty results map → should not throw
        def html = generateWith(config, [:])
        assertTrue(html.contains("S1"))
        assertTrue(html.contains("0/0/0"))
    }

    @Test
    void htmlExtraPointsShownInTable() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T1"; maxScore=1.0 } }
            groups { group { name="G"
                students { student { nick="s1"; fullName="S1"; repo="r" } }
            }}
            check { groups "G"; tasks "t1" }
        """)
        def student = config.studentsToCheck[0]
        def task = config.tasksToCheck[0]
        def r = okResult(1, 1.5, 0.5)
        def html = generateWith(config, [(student): [(task): r]])

        // extra points column should contain 0.5 (or formatted)
        assertTrue(html.contains(">0.50<") || html.contains(">0,50<") || html.contains("0.5"))
    }

    @Test
    void htmlEscapesSpecialChars() {
        def config = buildConfig("""
            tasks { task { id="t1"; name="T<1>&test"; maxScore=1.0 } }
            groups { group { name="G"
                students { student { nick="s1"; fullName="S&1<>"; repo="r" } }
            }}
            check { groups "G"; tasks "t1" }
        """)
        def html = generateWith(config, [:])
        assertFalse(html.contains("<1>"))
        assertTrue(html.contains("&lt;") || html.contains("&amp;"))
    }
}
