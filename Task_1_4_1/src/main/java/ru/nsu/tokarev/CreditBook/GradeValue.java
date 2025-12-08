package ru.nsu.tokarev.CreditBook;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GradeValue {
    EXCELLENT(5, "excellent"),
    GOOD(4, "good"),
    SATISFACTORY(3, "satisfactory"),
    UNSATISFACTORY(2, "unsatisfactory");

    private final int numericValue;
    private final String description;

    public static GradeValue fromNumeric(int value) {
        for (GradeValue grade : values()) {
            if (grade.numericValue == value) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Invalid grade value: " + value);
    }
}
