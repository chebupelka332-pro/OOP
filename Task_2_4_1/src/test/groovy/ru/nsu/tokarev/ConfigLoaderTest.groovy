package ru.nsu.tokarev

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.nsu.tokarev.dsl.ConfigLoader

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.*

class ConfigLoaderTest {

    @TempDir
    Path tempDir

    private File write(String name, String content) {
        def f = tempDir.resolve(name).toFile()
        f.text = content
        return f
    }

    @Test
    void loadsSimpleConfig() {
        def f = write("checker.groovy", """
            tasks {
                task { id = "2_1_1"; name = "Простые числа"; maxScore = 1.0
                       softDeadline = "2026-02-01"; hardDeadline = "2026-03-01" }
            }
            groups {
                group { name = "G1"
                    students {
                        student { nick = "s1"; fullName = "Student"; repo = "r" }
                    }
                }
            }
        """)
        def config = ConfigLoader.load(f.absolutePath)
        assertEquals(1, config.tasks.size())
        assertEquals("2_1_1", config.tasks[0].id)
        assertEquals(1, config.groups.size())
    }

    @Test
    void loadsConfigWithInclude() {
        write("tasks.groovy", """
            tasks {
                task { id = "t1"; name = "T1"; maxScore = 2.0 }
            }
        """)
        def main = write("checker.groovy", """
            include 'tasks.groovy'
            groups {
                group { name = "G"
                    students { student { nick="s1"; fullName="S1"; repo="r" } }
                }
            }
        """)
        def config = ConfigLoader.load(main.absolutePath)
        assertEquals(1, config.tasks.size())
        assertEquals("t1", config.tasks[0].id)
        assertEquals(2.0, config.tasks[0].maxScore, 0.001)
    }

    @Test
    void throwsWhenFileNotFound() {
        assertThrows(FileNotFoundException) {
            ConfigLoader.load("/nonexistent/path/checker.groovy")
        }
    }

    @Test
    void loadsCheckAndSettingsBlock() {
        def f = write("checker.groovy", """
            tasks { task { id="t1"; name="T"; maxScore=1.0 } }
            groups { group { name="G"
                students { student { nick="s1"; fullName="S"; repo="r" } }
            }}
            check { groups "G"; tasks "t1" }
            settings {
                workDir = "./out"
                timeout = 90
                checkStyleEnabled = false
            }
        """)
        def config = ConfigLoader.load(f.absolutePath)
        assertEquals(["G"], config.checkGroups)
        assertEquals(["t1"], config.checkTasks)
        assertEquals("./out", config.settings.workDir)
        assertEquals(90, config.settings.timeout)
        assertFalse(config.settings.checkStyleEnabled)
    }
}
