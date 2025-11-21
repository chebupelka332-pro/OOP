package ru.nsu.tokarev.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardCreation() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(Suit.HEARTS, card.getSuit());
        assertEquals(Rank.ACE, card.getRank());
        assertEquals(11, card.getValue());
    }

    @Test
    void testCardValue() {
        Card ace = new Card(Suit.HEARTS, Rank.ACE);
        Card king = new Card(Suit.SPADES, Rank.KING);
        Card two = new Card(Suit.DIAMONDS, Rank.TWO);

        assertEquals(11, ace.getValue());
        assertEquals(10, king.getValue());
        assertEquals(2, two.getValue());
    }

    @Test
    void testCardToString() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals("Ace of Hearts (11)", card.toString());
    }
}