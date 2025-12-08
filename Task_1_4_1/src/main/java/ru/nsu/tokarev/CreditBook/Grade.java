package ru.nsu.tokarev.CreditBook;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Grade {
    private final String subjectName;
    private final GradeValue gradeValue;
    private final AssessmentType assessmentType;
    private final LocalDate date;
    private final int semester;

    @Override
    public String toString() {
        return String.format("%s: %s (%s) - semester %d",
                subjectName, gradeValue.getDescription(),
                assessmentType.getDescription(), semester);
    }
}
