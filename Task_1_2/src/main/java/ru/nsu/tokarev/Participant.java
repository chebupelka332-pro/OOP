package ru.nsu.tokarev;

public class Participant {
    protected Hand hand;

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