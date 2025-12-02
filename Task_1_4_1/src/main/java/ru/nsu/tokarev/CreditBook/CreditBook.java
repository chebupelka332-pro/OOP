package ru.nsu.tokarev.CreditBook;

import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CreditBook {
    private final String studentName;
    private final String studentId;
    @Setter
    private StudyType studyType;
    private final List<Grade> grades;
    @Setter
    private GradeValue qualificationWorkGrade;
    @Setter
    private boolean isQualificationWorkCompleted;

    public CreditBook(String studentName, String studentId, StudyType studyType) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.studyType = studyType;
        this.grades = new ArrayList<>();
        this.isQualificationWorkCompleted = false;
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public double calculateGPA() {
        if (grades.isEmpty()) {
            return 0.0;
        }

        Map<String, Grade> latestGrades = getLatestGradesBySubject();
        
        return latestGrades.values().stream()
                .mapToInt(grade -> grade.getGradeValue().getNumericValue())
                .average()
                .orElse(0.0);
    }

    public boolean canTransferToBudget() {
        if (studyType == StudyType.BUDGET) {
            return false; // Already on budget
        }

        int maxSemester = grades.stream()
                .mapToInt(Grade::getSemester)
                .max()
                .orElse(0);

        List<Grade> lastTwoSemestersExams = grades.stream()
                .filter(grade -> grade.getSemester() >= maxSemester - 1)
                .filter(grade -> grade.getAssessmentType() == AssessmentType.EXAM)
                .collect(Collectors.toList());

        return lastTwoSemestersExams.stream()
                .noneMatch(grade -> grade.getGradeValue() == GradeValue.SATISFACTORY);
    }


    public boolean canGetRedDiploma() {
        Map<String, Grade> latestGrades = getLatestGradesBySubject();
        
        if (latestGrades.isEmpty()) {
            return false;
        }

        boolean hasNoSatisfactoryGrades = latestGrades.values().stream()
                .noneMatch(grade -> grade.getGradeValue() == GradeValue.SATISFACTORY);

        if (!hasNoSatisfactoryGrades) {
            return false;
        }

        long excellentCount = latestGrades.values().stream()
                .mapToLong(grade -> grade.getGradeValue() == GradeValue.EXCELLENT ? 1 : 0)
                .sum();

        double excellentPercentage = (double) excellentCount / latestGrades.size();

        boolean qualificationWorkExcellent = !isQualificationWorkCompleted || 
                (qualificationWorkGrade == GradeValue.EXCELLENT);

        return excellentPercentage >= 0.75 && qualificationWorkExcellent;
    }

    public boolean canGetIncreasedScholarship() {
        int lastSemester = grades.stream()
                .mapToInt(Grade::getSemester)
                .max()
                .orElse(0);

        if (lastSemester == 0) {
            return false;
        }

        List<Grade> lastSemesterGrades = grades.stream()
                .filter(grade -> grade.getSemester() == lastSemester)
                .collect(Collectors.toList());

        if (lastSemesterGrades.isEmpty()) {
            return false;
        }

        boolean allGoodOrExcellent = lastSemesterGrades.stream()
                .allMatch(grade -> grade.getGradeValue() == GradeValue.EXCELLENT ||
                                 grade.getGradeValue() == GradeValue.GOOD);

        boolean hasExcellent = lastSemesterGrades.stream()
                .anyMatch(grade -> grade.getGradeValue() == GradeValue.EXCELLENT);

        return allGoodOrExcellent && hasExcellent;
    }

    public boolean transferToBudget() {
        if (canTransferToBudget()) {
            this.studyType = StudyType.BUDGET;
            return true;
        }
        return false;
    }

    public List<Grade> getGrades() {
        return new ArrayList<>(grades);
    }

    public List<Grade> getGradesForSemester(int semester) {
        return grades.stream()
                .filter(grade -> grade.getSemester() == semester)
                .collect(Collectors.toList());
    }

    public double getGPAForSemester(int semester) {
        List<Grade> semesterGrades = getGradesForSemester(semester);
        if (semesterGrades.isEmpty()) {
            return 0.0;
        }
        
        return semesterGrades.stream()
                .mapToInt(grade -> grade.getGradeValue().getNumericValue())
                .average()
                .orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("Student credit book: %s (ID: %s)\n" +
                           "Study type: %s\n" +
                           "Average grade: %.2f\n" +
                           "Number of grades: %d",
                           studentName, studentId, studyType.getDescription(),
                           calculateGPA(), grades.size());
    }

    private Map<String, Grade> getLatestGradesBySubject() {
        return grades.stream()
                .collect(Collectors.groupingBy(
                        Grade::getSubjectName,
                        Collectors.maxBy(Comparator.comparing(Grade::getDate))
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));
    }
}
