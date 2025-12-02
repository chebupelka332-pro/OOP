package ru.nsu.tokarev.CreditBook;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssessmentType {
    EXAM("exam"),
    DIFFERENTIATED_CREDIT("differentiated credit"),
    REGULAR_CREDIT("credit");

    private final String description;
}
