package ru.nsu.tokarev.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Hand {
    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

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

    public boolean isBlackjack() {
        return getValue() == 21 && cards.size() == 2;
    }

    public boolean isBusted() {
        return getValue() > 21;
    }

    @Override
    public String toString() {
        return cards.stream().map(Card::toString).collect(Collectors.joining(", ", "[", "]"));
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Card get(int i) {
        return cards.get(i);
    }

    public int size() {
        return cards.size();
    }
}