package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.dsl.CheckerScript
import org.codehaus.groovy.control.CompilerConfiguration

import static org.junit.jupiter.api.Assertions.*

class DslTest {

    private CheckerConfig evalDsl(String script) {
        def cc = new CompilerConfiguration(scriptBaseClass: CheckerScript.canonicalName)
        def shell = new GroovyShell(this.class.classLoader, new Binding(), cc)
        def s = shell.parse(script) as CheckerScript
        s.scriptFile = new File(".")
        s.run()
        return s.config
    }

    @Test
    void testTasksParsing() {
        def config = evalDsl("""
            tasks {
                task {
                    id = "2_1_1"
                    name = "Простые числа"
                    maxScore = 1.0
                    softDeadline = "2024-02-20"
                    hardDeadline = "2024-03-05"
                }
            }
        """)
        assertEquals(1, config.tasks.size())
        assertEquals("2_1_1", config.tasks[0].id)
        assertEquals("Простые числа", config.tasks[0].name)
        assertEquals(1.0, config.tasks[0].maxScore, 0.001)
    }

    @Test
    void testGroupsParsing() {
        def config = evalDsl("""
            groups {
                group {
                    name = "12345"
                    students {
                        student {
                            nick = "student1"
                            fullName = "Студент No1"
                            repo = "https://github.com/student1/OOP"
                        }
                    }
                }
            }
        """)
        assertEquals(1, config.groups.size())
        assertEquals("12345", config.groups[0].name)
        assertEquals(1, config.groups[0].students.size())
        assertEquals("student1", config.groups[0].students[0].nick)
    }

    @Test
    void testCheckpointsParsing() {
        def config = evalDsl("""
            tasks {
                task { id = "2_1_1"; name = "T1"; maxScore = 1.0 }
            }
            checkpoints {
                checkpoint {
                    name = "КТ1"
                    date = "2024-03-20"
                    tasks "2_1_1"
                }
            }
        """)
        assertEquals(1, config.checkpoints.size())
        assertEquals("КТ1", config.checkpoints[0].name)
        assertEquals(["2_1_1"], config.checkpoints[0].taskIds)
    }

    @Test
    void testSettingsParsing() {
        def config = evalDsl("""
            settings {
                workDir = "./test_repos"
                timeout = 60
                gradeScale {
                    excellent = 9.0
                    good = 7.0
                    satisfactory = 5.0
                }
                extraPoints {
                    add "nick1", "2_1_1", 0.5
                }
            }
        """)
        assertEquals("./test_repos", config.settings.workDir)
        assertEquals(60, config.settings.timeout)
        assertEquals(9.0, config.settings.gradeScale.excellent, 0.001)
        assertEquals(0.5, config.settings.getExtraPointsFor("nick1", "2_1_1"), 0.001)
    }

    @Test
    void testCheckBlock() {
        def config = evalDsl("""
            groups {
                group {
                    name = "12345"
                    students {
                        student { nick = "s1"; fullName = "S1"; repo = "r" }
                        student { nick = "s2"; fullName = "S2"; repo = "r" }
                    }
                }
            }
            check {
                groups "12345"
            }
        """)
        assertEquals(["12345"], config.checkGroups)
        assertEquals(2, config.studentsToCheck.size())
    }

    @Test
    void testGradeScale() {
        def gs = new ru.nsu.tokarev.model.Settings.GradeScale()
        gs.excellent = 8.0
        gs.good = 6.0
        gs.satisfactory = 4.0

        assertEquals("отлично", gs.grade(9.0))
        assertEquals("хорошо", gs.grade(7.0))
        assertEquals("удовлетворительно", gs.grade(5.0))
        assertEquals("-", gs.grade(2.0))
    }
}
