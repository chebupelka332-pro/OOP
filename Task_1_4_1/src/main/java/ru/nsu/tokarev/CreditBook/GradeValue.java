package ru.nsu.tokarev.CreditBook;


public enum GradeValue {
    EXCELLENT(5, "excellent"),
    GOOD(4, "good"),
    SATISFACTORY(3, "satisfactory"),
    UNSATISFACTORY(2, "unsatisfactory");

    private final int numericValue;
    private final String description;

    GradeValue(int numericValue, String description) {
        this.numericValue = numericValue;
        this.description = description;
    }

    public int getNumericValue() {
        return numericValue;
    }

    public String getDescription() {
        return description;
    }

    public static GradeValue fromNumeric(int value) {
        for (GradeValue grade : values()) {
            if (grade.numericValue == value) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Invalid grade value: " + value);
    }
}
