package ru.nsu.tokarev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class HandTest {

    private Hand hand;

    @BeforeEach
    void setUp() {
        hand = new Hand();
    }

    @Test
    void testGetValueWithNoCards() {
        assertEquals(0, hand.getValue());
    }

    @Test
    void testGetValueWithTwoCards() {
        hand.addCard(new Card(Suit.HEARTS, Rank.FIVE));
        hand.addCard(new Card(Suit.CLUBS, Rank.TEN));
        assertEquals(15, hand.getValue());
    }

    @Test
    void testGetValueWithOneAce() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.CLUBS, Rank.NINE));
        assertEquals(20, hand.getValue());
    }

    @Test
    void testGetValueWithTwoAces() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.CLUBS, Rank.ACE));
        assertEquals(12, hand.getValue()); // 11 + 1
    }

    @Test
    void testGetValueWithSoftAceBecomesHard() {
        hand.addCard(new Card(Suit.DIAMONDS, Rank.ACE)); // 11
        hand.addCard(new Card(Suit.SPADES, Rank.SEVEN)); // 7 -> Total 18
        assertEquals(18, hand.getValue());

        hand.addCard(new Card(Suit.CLUBS, Rank.FIVE)); // 5 -> Total 23, Ace becomes 1 -> 13
        assertEquals(13, hand.getValue());
    }

    @Test
    void testIsBlackjackReturnsTrue() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.CLUBS, Rank.KING));
        assertTrue(hand.isBlackjack());
        assertEquals(21, hand.getValue());
    }

    @Test
    void testIsBlackjackReturnsFalseForThreeCards() {
        hand.addCard(new Card(Suit.HEARTS, Rank.FIVE));
        hand.addCard(new Card(Suit.CLUBS, Rank.SIX));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.TEN));
        assertFalse(hand.isBlackjack());
        assertEquals(21, hand.getValue());
    }

    @Test
    void testIsBustedReturnsTrue() {
        hand.addCard(new Card(Suit.HEARTS, Rank.TEN));
        hand.addCard(new Card(Suit.CLUBS, Rank.TEN));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.TWO));
        assertTrue(hand.isBusted());
        assertEquals(22, hand.getValue());
    }

    @Test
    void testIsBustedReturnsFalse() {
        hand.addCard(new Card(Suit.HEARTS, Rank.TEN));
        hand.addCard(new Card(Suit.CLUBS, Rank.TEN));
        assertFalse(hand.isBusted());
    }
}