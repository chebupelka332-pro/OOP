package ru.nsu.tokarev;

/**
 * Перечисление, представляющее масти игральных карт.
 */
public enum Suit {
    HEARTS("Червы"),
    DIAMONDS("Бубны"),
    CLUBS("Трефы"),
    SPADES("Пики");

    private final String name;

    Suit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
