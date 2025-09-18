package ru.nsu.tokarev;

/**
 * Представляет одну игральную карту с мастью и достоинством.
 * Этот класс является неизменяемым (immutable).
 */
public class Card {
    Rank rank;
    Suit suit;

    /**
     * Создает новую карту с указанными мастью и достоинством.
     *
     * @param suit масть карты (например, HEARTS).
     * @param rank достоинство карты (например, ACE).
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     *
     * @return целочисленное значение очков карты.
     */
    public int getValue() {
        return rank.getValue();
    }

    /**
     *
     * @return форматированная строка, описывающая карту.
     */
    public String toString() {
        return rank.getName() + " " + suit.getName() + " (" + rank.getValue() + ")";
    }

}
