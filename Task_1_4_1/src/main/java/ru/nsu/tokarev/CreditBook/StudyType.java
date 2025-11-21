package ru.nsu.tokarev.CreditBook;


public enum StudyType {
    BUDGET("budget"),
    PAID("paid");

    private final String description;

    StudyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
