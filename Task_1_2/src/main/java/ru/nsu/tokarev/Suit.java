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

    /**
     * Конструктор класса.
     *
     * @param name - имя.
     */
    Suit(String name) {
        this.name = name;
    }

    /**
     * Гетер для возврата масти.
     *
     * @return возвращает масть.
     */
    public String getName() {
        return name;
    }
}
