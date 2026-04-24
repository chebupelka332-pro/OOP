// конфигурация семестра: группы, студенты, контрольные точки
groups {
    group {
        name = "24214"
        students {
            student {
                nick = "chebupelka332-pro"
                fullName = "Токарев Максим"
                repo = "https://github.com/chebupelka332-pro/OOP.git"
            }
        }
        students {
            student {
                nick = "Proletcultist"
                fullName = "Anonymous Proletcultist"
                repo = "https://github.com/Proletcultist/OOP.git"
            }
        }
    }
}

checkpoints {
    checkpoint {
        name = "КТ1"
        date = "2026-03-20"
        tasks "2_1_1", "2_2_1"
    }
    checkpoint {
        name = "КТ2"
        date = "2026-04-30"
        tasks "2_1_1", "2_2_1", "2_3_1"
    }
}
