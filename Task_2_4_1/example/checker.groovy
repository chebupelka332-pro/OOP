// основной файл кkонфигурации
// Запуск: oop-checker test

include 'tasks.groovy'
include 'groups.groovy'

// Что проверяем
check {
    groups "24214"
    tasks "2_1_1", "2_2_1", "2_3_1"
}

settings {
    // Куда клонировать репозитории
    workDir = "./repos"

    // Таймаут в секундах на каждую операцию
    timeout = 350

    // Проверка Google Java Style (false - пропустить)
    checkStyleEnabled = false

    // Критерии оценок (сумма баллов -> оценка)
    gradeScale {
        excellent = 8.0
        good = 6.0
        satisfactory = 4.0
    }

    // Учитывать еженедельную активность (бонус к оценке)
    activityBonusEnabled = true
    activityThreshold = 0.8   // >= 80% активных недель
    activityBonus = 0.5       // бонус в баллах

    // Дополнительные баллы конкретным студентам за конкретные задачи
    extraPoints {
        add "chebupelka332-pro", "2_3_1", 1.0
    }
}
