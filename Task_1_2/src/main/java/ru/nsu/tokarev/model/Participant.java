package ru.nsu.tokarev.model;

public class Participant {
    private Hand hand;

    public Participant() {
        this.hand = new Hand();
    }

    public void addCard(Card card) {
        hand.addCard(card);
    }

    public Hand getHand() {
        return hand;
    }

    public int getHandValue() {
        return hand.getValue();
    }

    public void resetHand() {
        this.hand = new Hand();
    }
}