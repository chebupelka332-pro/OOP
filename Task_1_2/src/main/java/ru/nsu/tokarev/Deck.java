package ru.nsu.tokarev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Представляет стандартную игральную колоду из 52 карт.
 * Отвечает за создание, перемешивание и раздачу карт.
 */
public class Deck {
    private final List<Card> cards;

    /**
     * Создает новую колоду из 52 карт и сразу же перемешивает ее.
     */
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
        return cards.remove(0);
    }
}
