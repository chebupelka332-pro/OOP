package ru.nsu.tokarev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void testNewDeckSize() {
        assertEquals(52, deck.size());
        assertFalse(deck.isEmpty());
    }

    @Test
    void testDealCard() {
        int initialSize = deck.size();
        Card card = deck.deal();
        
        assertNotNull(card);
        assertEquals(initialSize - 1, deck.size());
    }

    @Test
    void testDealAllCards() {
        for (int i = 0; i < 52; i++) {
            assertNotNull(deck.deal());
        }
        
        assertTrue(deck.isEmpty());
        assertEquals(0, deck.size());
        assertNull(deck.deal());
    }

    @Test
    void testShuffle() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        
        Card[] cards1 = new Card[52];
        Card[] cards2 = new Card[52];
        
        for (int i = 0; i < 52; i++) {
            cards1[i] = deck1.deal();
            cards2[i] = deck2.deal();
        }
        
        boolean different = false;
        for (int i = 0; i < 52; i++) {
            if (!cards1[i].toString().equals(cards2[i].toString())) {
                different = true;
                break;
            }
        }
        
        assertTrue(different, "Two shuffled decks should be different");
    }

    @Test
    void testDeckContainsAllCards() {
        boolean[] foundCards = new boolean[52];
        
        for (int i = 0; i < 52; i++) {
            Card card = deck.deal();
            int index = card.getSuit().ordinal() * 13 + card.getRank().ordinal();
            foundCards[index] = true;
        }
        
        for (boolean found : foundCards) {
            assertTrue(found, "Deck should contain all 52 unique cards");
        }
    }

    @Test
    void testReset() {
        // Deal some cards
        for (int i = 0; i < 10; i++) {
            deck.deal();
        }
        assertEquals(42, deck.size());
        
        // Reset deck
        deck.reset();
        assertEquals(52, deck.size());
        assertFalse(deck.isEmpty());
        
        // Should be able to deal all cards again
        for (int i = 0; i < 52; i++) {
            assertNotNull(deck.deal());
        }
        assertEquals(0, deck.size());
    }

    @Test
    void testResetAndShuffle() {
        // Deal all cards to empty the deck
        for (int i = 0; i < 52; i++) {
            deck.deal();
        }
        assertTrue(deck.isEmpty());
        
        // Reset and shuffle
        deck.reset();
        deck.shuffle();
        
        assertEquals(52, deck.size());
        assertFalse(deck.isEmpty());
        assertNotNull(deck.deal());
    }
}