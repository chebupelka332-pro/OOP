package ru.nsu.tokarev.report

import ru.nsu.tokarev.core.ScoringEngine
import ru.nsu.tokarev.dsl.CheckerConfig
import ru.nsu.tokarev.model.CheckPoint
import ru.nsu.tokarev.model.CheckResult
import ru.nsu.tokarev.model.Group
import ru.nsu.tokarev.model.Student
import ru.nsu.tokarev.model.Task

class HtmlReporter {
    private final CheckerConfig config
    private final Map<Student, Map<Task, CheckResult>> results
    private final Map<Student, Double> activity

    HtmlReporter(CheckerConfig config,
                 Map<Student, Map<Task, CheckResult>> results,
                 Map<Student, Double> activity) {
        this.config = config
        this.results = results
        this.activity = activity
    }

    String generate() {
        def sb = new StringBuilder()
        sb << htmlHeader()

        def tasks = config.tasksToCheck
        def groupedStudents = groupStudents()

        groupedStudents.each { groupName, students ->
            sb << "<h1>Группа ${esc(groupName)}</h1>\n"

            tasks.each { task ->
                sb << "<h2>Лабораторная ${esc(task.id)} (${esc(task.name)})</h2>\n"
                sb << taskTable(students, task)
            }

            sb << "<h2>Общая статистика группы ${esc(groupName)}</h2>\n"
            sb << summaryTable(students, tasks)

            if (config.checkpoints) {
                sb << "<h2>Контрольные точки</h2>\n"
                sb << checkpointsTable(students, tasks)
            }
        }

        sb << htmlFooter()
        return sb.toString()
    }

    private Map<String, List<Student>> groupStudents() {
        def students = config.studentsToCheck
        def grouped = [:]
        students.each { s ->
            def group = config.findGroupForStudent(s.nick)
            def groupName = group?.name ?: "Без группы"
            grouped.computeIfAbsent(groupName) { [] } << s
        }
        return grouped
    }

    private String taskTable(List<Student> students, Task task) {
        def sb = new StringBuilder()
        sb << "<table>\n<thead><tr>"
        ["Студент", "Сборка", "Документация", "Style guide", "Тесты", "Доп. балл", "Общий балл"].each {
            sb << "<th>${it}</th>"
        }
        sb << "</tr></thead>\n<tbody>\n"

        students.each { student ->
            def r = results[student]?.get(task) ?: new CheckResult()
            sb << "<tr>"
            sb << "<td>${esc(student.fullName)}</td>"
            sb << "<td class='${r.buildSuccess ? "ok" : "fail"}'>${r.buildSuccess ? "+" : "-"}</td>"
            sb << "<td class='${r.docsSuccess ? "ok" : "fail"}'>${r.docsSuccess ? "+" : "-"}</td>"
            def styleCell = config.settings.checkStyleEnabled
                ? "<td class='${r.styleSuccess ? "ok" : "fail"}'>${r.styleSuccess ? "+" : "-"}</td>"
                : "<td class='na'>N/A</td>"
            sb << styleCell
            sb << "<td>${r.testsString()}</td>"
            sb << "<td>${fmt(r.extraPoints)}</td>"
            sb << "<td><strong>${fmt(r.score)}</strong></td>"
            sb << "</tr>\n"
        }

        sb << "</tbody></table>\n"
        return sb.toString()
    }

    private String summaryTable(List<Student> students, List<Task> tasks) {
        def sb = new StringBuilder()
        sb << "<table>\n<thead><tr><th>Студент</th>"
        tasks.each { sb << "<th>${esc(it.id)}</th>" }
        sb << "<th>Сумма</th><th>Активность</th><th>Оценка</th></tr></thead>\n<tbody>\n"

        students.each { student ->
            def taskResults = results[student] ?: [:]
            double total = taskResults.values().sum { it.score } ?: 0.0
            double actRatio = activity[student] ?: 0.0
            def grade = ScoringEngine.grade(total, config.settings, actRatio)

            sb << "<tr>"
            sb << "<td>${esc(student.fullName)}</td>"
            tasks.each { task ->
                def r = taskResults[task]
                sb << "<td>${r ? fmt(r.score) : "—"}</td>"
            }
            sb << "<td><strong>${fmt(total)}</strong></td>"
            sb << "<td>${(actRatio * 100).round()}%</td>"
            sb << "<td>${esc(grade)}</td>"
            sb << "</tr>\n"
        }

        sb << "</tbody></table>\n"
        return sb.toString()
    }

    private String checkpointsTable(List<Student> students, List<Task> tasks) {
        def sb = new StringBuilder()
        sb << "<table>\n<thead><tr><th>Студент</th>"
        config.checkpoints.each { cp -> sb << "<th>${esc(cp.name)} (${cp.date})</th>" }
        sb << "</tr></thead>\n<tbody>\n"

        students.each { student ->
            sb << "<tr><td>${esc(student.fullName)}</td>"
            config.checkpoints.each { cp ->
                double cpScore = cp.taskIds.sum { taskId ->
                    def task = config.findTask(taskId)
                    task ? (results[student]?.get(task)?.score ?: 0.0) : 0.0
                } ?: 0.0
                sb << "<td>${fmt(cpScore)}</td>"
            }
            sb << "</tr>\n"
        }

        sb << "</tbody></table>\n"
        return sb.toString()
    }

    private static String esc(String s) {
        s?.replace("&", "&amp;")?.replace("<", "&lt;")?.replace(">", "&gt;") ?: ""
    }

    private static String fmt(double v) {
        v == (long) v ? String.valueOf((long) v) : String.format("%.2f", v)
    }

    private static String htmlHeader() {
        """<!DOCTYPE html>
<html lang="ru">
<head>
<meta charset="UTF-8">
<title>OOP Checker — Отчёт</title>
<style>
  body { font-family: Arial, sans-serif; margin: 2em; color: #222; }
  h1 { border-bottom: 2px solid #333; padding-bottom: 0.3em; }
  h2 { color: #444; margin-top: 1.5em; }
  table { border-collapse: collapse; margin: 1em 0; width: auto; }
  th, td { border: 1px solid #bbb; padding: 6px 12px; text-align: center; }
  th { background: #f0f0f0; font-weight: bold; }
  tr:nth-child(even) { background: #fafafa; }
  td:first-child { text-align: left; }
  .ok { color: #2a7; font-weight: bold; }
  .fail { color: #c33; font-weight: bold; }
  .na { color: #999; }
</style>
</head>
<body>
"""
    }

    private static String htmlFooter() {
        "\n</body>\n</html>\n"
    }
}
