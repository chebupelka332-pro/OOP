package ru.nsu.tokarev.commands

import ru.nsu.tokarev.core.TaskChecker
import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.report.HtmlReporter

class TestCommand {
    static void execute(CheckerConfig config) {
        def checker = new TaskChecker(config)

        System.err.println("Запуск проверки...")
        def results = checker.checkAll()

        System.err.println("Вычисление активности...")
        def activity = checker.computeActivity()

        System.err.println("Генерация отчёта...")
        def reporter = new HtmlReporter(config, results, activity)
        println(reporter.generate())
    }
}
