package ru.nsu.tokarev;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Представляет руку карт у одного из участников игры.
 * Класс отвечает за подсчет очков, включая сложную логику обработки тузов.
 */
public class Hand {
    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    /**
     * Добавляет карту в руку.
     *
     * @param card карта, которую нужно добавить.
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Рассчитывает и возвращает общую стоимость карт в руке.
     * Автоматически обрабатывает тузы.
     *
     * @return общая сумма очков в руке.
     */
    public int getValue() {
        int value = 0;
        int aceCount = 0;

        for (Card card : cards) {
            value += card.getValue();
            if (card.getValue() == 11) {
                aceCount += 1;
            }
        }

        while (aceCount != 0 && value > 21) {
            value -= 10;
            aceCount--;
        }

        return value;
    }

    /**
     * Проверка условия на блэкджек.
     *
     * @return {@code true}, если это блэкджек, иначе {@code false}.
     */
    public boolean isBlackjack() {
        return getValue() == 21 && cards.size() == 2;
    }

    /**
     * Проверка на переполнения карт.
     *
     * @return {@code true}, если перебор, иначе {@code false}.
     */
    public boolean isBusted() {
        return getValue() > 21;
    }

    /**
     * Преобразование списка карт в форматированную строку.
     *
     * @return форматированная строка, описывающая карту.
     */
    public String toString() {
        return cards.stream().map(Card::toString).collect(Collectors.joining(", ", "[", "]"));
    }

    /**
     * Проверка списка карт на пустоту.
     *
     * @return {@code true}, если карты кончились, иначе {@code false}.
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Получить i-карту из списка.
     *
     * @return карта под номером i.
     */
    public Card get(int i) {
        return cards.get(i);
    }
}
