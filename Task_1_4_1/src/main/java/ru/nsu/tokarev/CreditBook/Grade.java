package ru.nsu.tokarev.CreditBook;

import java.time.LocalDate;


public class Grade {
    private final String subjectName;
    private final GradeValue gradeValue;
    private final AssessmentType assessmentType;
    private final LocalDate date;
    private final int semester;

    public Grade(String subjectName, GradeValue gradeValue, AssessmentType assessmentType, 
                 LocalDate date, int semester) {
        this.subjectName = subjectName;
        this.gradeValue = gradeValue;
        this.assessmentType = assessmentType;
        this.date = date;
        this.semester = semester;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public GradeValue getGradeValue() {
        return gradeValue;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getSemester() {
        return semester;
    }

    @Override
    public String toString() {
        return String.format("%s: %s (%s) - semester %d",
                subjectName, gradeValue.getDescription(),
                assessmentType.getDescription(), semester);
    }
}
