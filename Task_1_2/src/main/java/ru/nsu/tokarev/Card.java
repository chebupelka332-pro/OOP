package ru.nsu.tokarev;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Card {
    Rank rank;
    Suit suit;
    boolean isShow;

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
     * @return целочисленное значение очков карты.
     */
    public int getValue() {
        return rank.getValue();
    }

    /**
     * @return форматированная строка, описывающая карту.
     */
    public String toString()
    {
        return rank.getName() + " " + suit.getName() + " (" + rank.getValue() + ")";
    }

}
