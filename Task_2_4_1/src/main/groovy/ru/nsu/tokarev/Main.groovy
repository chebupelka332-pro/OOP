package ru.nsu.tokarev

import ru.nsu.tokarev.commands.TestCommand
import ru.nsu.tokarev.dsl.ConfigLoader

static void main(String[] args) {
    if (args.length < 1) {
        printUsage()
        System.exit(1)
    }

    String command = args[0]
    String configPath = args.length >= 3 && args[1] == "--config"
        ? args[2]
        : ConfigLoader.DEFAULT_CONFIG

    try {
        def config = ConfigLoader.load(configPath)

        switch (command) {
            case "test":
                TestCommand.execute(config)
                break
            default:
                System.err.println("Неизвестная команда: ${command}")
                printUsage()
                System.exit(1)
        }
    } catch (FileNotFoundException e) {
        System.err.println("Ошибка: ${e.message}")
        System.exit(1)
    } catch (Exception e) {
        System.err.println("Ошибка выполнения: ${e.message}")
        e.printStackTrace(System.err)
        System.exit(1)
    }
}

private static void printUsage() {
    System.err.println("""
Использование: oop-checker <команда> [--config <файл>]

Команды:
  test    Проверить задачи студентов и вывести HTML-отчёт

Файл конфигурации по умолчанию: checker.groovy (в текущей директории)
""".trim())
}
