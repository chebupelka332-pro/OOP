package ru.nsu.tokarev.CreditBook;


public enum AssessmentType {
    EXAM("exam"),
    DIFFERENTIATED_CREDIT("differentiated credit"),
    REGULAR_CREDIT("credit");

    private final String description;

    AssessmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
