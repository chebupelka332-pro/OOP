package ru.nsu.tokarev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HandTest {
    private Hand hand;

    @BeforeEach
    void setUp() {
        hand = new Hand();
    }

    @Test
    void testEmptyHand() {
        assertTrue(hand.isEmpty());
        assertEquals(0, hand.getValue());
        assertEquals(0, hand.size());
        assertFalse(hand.isBlackjack());
        assertFalse(hand.isBusted());
    }

    @Test
    void testAddCard() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        hand.addCard(card);
        
        assertFalse(hand.isEmpty());
        assertEquals(1, hand.size());
        assertEquals(11, hand.getValue());
        assertEquals(card, hand.get(0));
    }

    @Test
    void testBlackjack() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        assertTrue(hand.isBlackjack());
        assertEquals(21, hand.getValue());
        assertFalse(hand.isBusted());
    }

    @Test
    void testBust() {
        hand.addCard(new Card(Suit.HEARTS, Rank.KING));
        hand.addCard(new Card(Suit.SPADES, Rank.QUEEN));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.FIVE));
        
        assertTrue(hand.isBusted());
        assertEquals(25, hand.getValue());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void testAceValueAdjustment() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.NINE));
        
        assertEquals(21, hand.getValue());
        assertFalse(hand.isBusted());
    }

    @Test
    void testMultipleAceAdjustment() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.ACE));
        hand.addCard(new Card(Suit.CLUBS, Rank.NINE));
        
        assertEquals(12, hand.getValue());
        assertFalse(hand.isBusted());
    }

    @Test
    void testToString() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        String expected = "[Ace of Hearts (11), King of Spades (10)]";
        assertEquals(expected, hand.toString());
    }
}