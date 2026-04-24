package ru.nsu.tokarev.dsl

import org.codehaus.groovy.control.CompilerConfiguration

class ConfigLoader {
    static final String DEFAULT_CONFIG = "checker.groovy"

    static CheckerConfig load(String path = DEFAULT_CONFIG) {
        def file = new File(path)
        if (!file.exists()) {
            def cwd = new File(".").absolutePath
            throw new FileNotFoundException(
                "Файл конфигурации не найден: ${file.absolutePath}\n" +
                "Текущая директория: ${cwd}\n" +
                "Создайте файл '${DEFAULT_CONFIG}' в рабочей директории."
            )
        }
        def cc = new CompilerConfiguration(scriptBaseClass: CheckerScript.canonicalName)
        def shell = new GroovyShell(ConfigLoader.classLoader, new Binding(), cc)
        def script = shell.parse(file) as CheckerScript
        script.scriptFile = file
        script.run()
        return script.config
    }
}
