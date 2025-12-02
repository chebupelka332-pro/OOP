package ru.nsu.tokarev.CreditBook;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudyType {
    BUDGET("budget"),
    PAID("paid");

    private final String description;
}
