package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import org.codehaus.groovy.control.CompilerConfiguration
import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.dsl.CheckerScript

import static org.junit.jupiter.api.Assertions.*

class CheckerConfigTest {

    private CheckerConfig eval(String script) {
        def cc = new CompilerConfiguration(scriptBaseClass: CheckerScript.canonicalName)
        def shell = new GroovyShell(this.class.classLoader, new Binding(), cc)
        def s = shell.parse(script) as CheckerScript
        s.scriptFile = new File(".")
        s.run()
        return s.config
    }

    @Test
    void getStudentsToCheckAllWhenNoFilter() {
        def config = eval("""
            groups {
                group { name="G1"
                    students {
                        student { nick="s1"; fullName="S1"; repo="r" }
                        student { nick="s2"; fullName="S2"; repo="r" }
                    }
                }
            }
        """)
        assertEquals(2, config.studentsToCheck.size())
    }

    @Test
    void getStudentsToCheckByNick() {
        def config = eval("""
            groups { group { name="G"
                students {
                    student { nick="s1"; fullName="S1"; repo="r" }
                    student { nick="s2"; fullName="S2"; repo="r" }
                }
            }}
            check { students "s1" }
        """)
        def students = config.studentsToCheck
        assertEquals(1, students.size())
        assertEquals("s1", students[0].nick)
    }

    @Test
    void getTasksToCheckFiltered() {
        def config = eval("""
            tasks {
                task { id="t1"; name="T1"; maxScore=1.0 }
                task { id="t2"; name="T2"; maxScore=1.0 }
            }
            check { tasks "t1" }
        """)
        def tasks = config.tasksToCheck
        assertEquals(1, tasks.size())
        assertEquals("t1", tasks[0].id)
    }

    @Test
    void getTasksToCheckAllWhenNoFilter() {
        def config = eval("""
            tasks {
                task { id="t1"; name="T1"; maxScore=1.0 }
                task { id="t2"; name="T2"; maxScore=1.0 }
            }
        """)
        assertEquals(2, config.tasksToCheck.size())
    }

    @Test
    void findTaskById() {
        def config = eval("""
            tasks { task { id="2_1_1"; name="Primes"; maxScore=1.0 } }
        """)
        assertNotNull(config.findTask("2_1_1"))
        assertNull(config.findTask("nonexistent"))
    }

    @Test
    void findGroupForStudent() {
        def config = eval("""
            groups { group { name="G42"
                students { student { nick="alice"; fullName="Alice"; repo="r" } }
            }}
        """)
        def group = config.findGroupForStudent("alice")
        assertNotNull(group)
        assertEquals("G42", group.name)
        assertNull(config.findGroupForStudent("bob"))
    }

    @Test
    void checkBlockTasksViaSetTasks() {
        def config = eval("""
            tasks {
                task { id="t1"; name="T1"; maxScore=1.0 }
                task { id="t2"; name="T2"; maxScore=1.0 }
            }
            check { tasks = ["t1", "t2"] }
        """)
        assertEquals(["t1", "t2"], config.checkTasks)
    }

    @Test
    void checkBlockStudentsViaSetStudents() {
        def config = eval("""
            groups { group { name="G"
                students { student { nick="s1"; fullName="S1"; repo="r" } }
            }}
            check { students = ["s1"] }
        """)
        assertEquals(["s1"], config.checkStudents)
    }

    @Test
    void settingsBuilderAllFields() {
        def config = eval("""
            settings {
                workDir = "./repos"
                timeout = 200
                checkStyleEnabled = false
                activityBonusEnabled = true
                activityThreshold = 0.75
                activityBonus = 0.5
                gradeScale {
                    excellent = 9.0
                    good = 7.0
                    satisfactory = 5.0
                }
                extraPoints {
                    add "nick", "task", 1.0
                }
            }
        """)
        def s = config.settings
        assertEquals("./repos", s.workDir)
        assertEquals(200, s.timeout)
        assertFalse(s.checkStyleEnabled)
        assertTrue(s.activityBonusEnabled)
        assertEquals(0.75, s.activityThreshold, 0.001)
        assertEquals(0.5, s.activityBonus, 0.001)
        assertEquals(9.0, s.gradeScale.excellent, 0.001)
        assertEquals(7.0, s.gradeScale.good, 0.001)
        assertEquals(5.0, s.gradeScale.satisfactory, 0.001)
        assertEquals(1.0, s.getExtraPointsFor("nick", "task"), 0.001)
    }

    @Test
    void checkpointWithSetTasksList() {
        def config = eval("""
            checkpoints {
                checkpoint {
                    name = "КТ2"
                    date = "2026-04-30"
                    tasks = ["t1", "t2"]
                }
            }
        """)
        assertEquals(["t1", "t2"], config.checkpoints[0].taskIds)
    }

    @Test
    void multipleGroupsFiltered() {
        def config = eval("""
            groups {
                group { name="G1"
                    students { student { nick="a"; fullName="A"; repo="r" } }
                }
                group { name="G2"
                    students { student { nick="b"; fullName="B"; repo="r" } }
                }
            }
            check { groups "G2" }
        """)
        def students = config.studentsToCheck
        assertEquals(1, students.size())
        assertEquals("b", students[0].nick)
    }
}
