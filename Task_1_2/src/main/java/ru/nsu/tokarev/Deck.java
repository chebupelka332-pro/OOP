package ru.nsu.tokarev;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private final List<Card> cards;


    public Deck() {
        cards = new ArrayList<>();
        // Используем одну колоду из 52 карт, как указано в правилах [cite: 11]
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }

    /**
     * Перемешивает карты в колоде в случайном порядке.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Раздает одну карту с верха колоды.
     * Карта удаляется из колоды после раздачи.
     *
     * @return верхняя карта в колоде или {@code null}, если колода пуста.
     */
    public Card deal() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.removeFirst();
    }
}
