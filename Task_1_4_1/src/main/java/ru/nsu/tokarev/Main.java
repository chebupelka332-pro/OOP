package ru.nsu.tokarev;

import ru.nsu.tokarev.CreditBook.*;
import java.time.LocalDate;

/**
 * Demo class for the CreditBook system.
 */
public class Main {
    public static void main(String[] args) {
        // Создаем зачетную книжку для студента на платном обучении
        CreditBook creditBook = new CreditBook("Ivanov Ivan Ivanovich", "21215001", StudyType.PAID);

        System.out.println("=== ELECTRONIC CREDIT BOOK DEMONSTRATION ===\n");
        System.out.println(creditBook);
        System.out.println();

        // Добавляем оценки за первый семестр
        System.out.println("Adding grades for 1st semester:");
        creditBook.addGrade(new Grade("Mathematical Analysis", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2023, 1, 20), 1));
        creditBook.addGrade(new Grade("Programming", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2023, 1, 22), 1));
        creditBook.addGrade(new Grade("Physics", GradeValue.GOOD,
                AssessmentType.DIFFERENTIATED_CREDIT, LocalDate.of(2023, 1, 18), 1));
        creditBook.addGrade(new Grade("History", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.of(2023, 1, 25), 1));

        printSemesterResults(creditBook, 1);

        // Добавляем оценки за второй семестр
        System.out.println("\nAdding grades for 2nd semester:");
        creditBook.addGrade(new Grade("Mathematical Analysis", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2023, 6, 15), 2));
        creditBook.addGrade(new Grade("Programming", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2023, 6, 18), 2));
        creditBook.addGrade(new Grade("Discrete Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2023, 6, 20), 2));
        creditBook.addGrade(new Grade("English Language", GradeValue.GOOD,
                AssessmentType.DIFFERENTIATED_CREDIT, LocalDate.of(2023, 6, 12), 2));

        printSemesterResults(creditBook, 2);

        // Добавляем оценки за третий семестр с удовлетворительной оценкой
        System.out.println("\nAdding grades for 3rd semester:");
        creditBook.addGrade(new Grade("Algorithms and Data Structures", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2024, 1, 15), 3));
        creditBook.addGrade(new Grade("Databases", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.of(2024, 1, 18), 3));
        creditBook.addGrade(new Grade("Operating Systems", GradeValue.SATISFACTORY,
                AssessmentType.EXAM, LocalDate.of(2024, 1, 22), 3));

        printSemesterResults(creditBook, 3);

        // Проверяем возможность перевода на бюджет после 3 семестра
        System.out.println("\nChecking budget transfer possibility after 3rd semester:");
        System.out.println("Can transfer to budget: " + creditBook.canTransferToBudget());
        System.out.println("Reason: has satisfactory grade on exam in last two semesters");

        // Добавляем четвертый семестр без удовлетворительных оценок на экзаменах
        System.out.println("\nAdding grades for 4th semester:");
        creditBook.addGrade(new Grade("Web Technologies", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2024, 6, 15), 4));
        creditBook.addGrade(new Grade("Machine Learning", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.of(2024, 6, 18), 4));
        creditBook.addGrade(new Grade("Physical Education", GradeValue.SATISFACTORY,
                AssessmentType.DIFFERENTIATED_CREDIT, LocalDate.of(2024, 6, 10), 4));

        printSemesterResults(creditBook, 4);

        // Проверяем возможность перевода на бюджет после 4 семестра
        System.out.println("\nChecking budget transfer possibility after 4th semester:");
        System.out.println("Can transfer to budget: " + creditBook.canTransferToBudget());
        System.out.println("Reason: no satisfactory grades on exams in semesters 3-4");

        if (creditBook.transferToBudget()) {
            System.out.println("✓ Student transferred to budget study!");
        }

        // Проверяем возможность получения красного диплома
        System.out.println("\nChecking red diploma possibility:");
        System.out.println("Current GPA: " + String.format("%.2f", creditBook.calculateGPA()));
        System.out.println("Can get red diploma: " + creditBook.canGetRedDiploma());

        // Анализируем требования для красного диплома
        analyzeRedDiplomaRequirements(creditBook);

        // Устанавливаем оценку за квалификационную работу
        System.out.println("\nSetting 'excellent' grade for qualification work:");
        creditBook.setQualificationWorkGrade(GradeValue.EXCELLENT);
        System.out.println("Now can get red diploma: " + creditBook.canGetRedDiploma());

        // Проверяем возможность повышенной стипендии
        System.out.println("\nChecking increased scholarship possibility:");
        System.out.println("Can get increased scholarship: " + creditBook.canGetIncreasedScholarship());

        System.out.println("\n" + creditBook);
    }

    private static void printSemesterResults(CreditBook creditBook, int semester) {
        System.out.println("GPA for " + semester + "th semester: " +
                String.format("%.2f", creditBook.getGPAForSemester(semester)));
        System.out.println("Overall GPA: " +
                String.format("%.2f", creditBook.calculateGPA()));
        System.out.println("Semester grades:");
        for (Grade grade : creditBook.getGradesForSemester(semester)) {
            System.out.println("  " + grade);
        }
    }

    private static void analyzeRedDiplomaRequirements(CreditBook creditBook) {
        // Подсчитаем статистику для анализа
        long totalSubjects = creditBook.getGrades().stream()
                .map(Grade::getSubjectName)
                .distinct()
                .count();

        long excellentGrades = creditBook.getGrades().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Grade::getSubjectName,
                    java.util.stream.Collectors.maxBy(
                        java.util.Comparator.comparing(Grade::getDate))))
                .values().stream()
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .mapToLong(grade -> grade.getGradeValue() == GradeValue.EXCELLENT ? 1 : 0)
                .sum();

        double excellentPercentage = totalSubjects > 0 ? (double) excellentGrades / totalSubjects * 100 : 0;

        System.out.println("Red diploma requirements analysis:");
        System.out.println("- Percentage of excellent grades: " + String.format("%.1f%%", excellentPercentage) +
                " (required ≥75%)");
        System.out.println("- Qualification work: " +
                (creditBook.isQualificationWorkCompleted() ?
                    creditBook.getQualificationWorkGrade().getDescription() : "not completed"));

        boolean hasNoSatisfactory = creditBook.getGrades().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Grade::getSubjectName,
                    java.util.stream.Collectors.maxBy(
                        java.util.Comparator.comparing(Grade::getDate))))
                .values().stream()
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .noneMatch(grade -> grade.getGradeValue() == GradeValue.SATISFACTORY);

        System.out.println("- No satisfactory final grades: " +
                (hasNoSatisfactory ? "+" : "-"));
    }
}
