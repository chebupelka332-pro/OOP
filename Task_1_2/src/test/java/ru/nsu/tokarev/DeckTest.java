package ru.nsu.tokarev;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DeckTest {

    @Test
    void testNewDeckHas52Cards() {
        Deck deck = new Deck();
        Set<Card> cards = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            cards.add(deck.deal());
        }
        assertEquals(52, cards.size());
        assertNull(deck.deal()); // Deck should be empty
    }

    @Test
    void testDealReducesDeckSize() {
        Deck deck = new Deck();
        Card card1 = deck.deal();
        assertNotNull(card1);

        // Check size by dealing all cards
        int count = 1; // Already dealt one
        while (deck.deal() != null) {
            count++;
        }
        assertEquals(52, count);
    }

    @Test
    void testShuffleChangesCardOrder() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();

        // It's technically possible for two shuffles to result in the same order,
        // but it's astronomically unlikely. We'll compare the first 10 cards.
        boolean orderIsDifferent = false;
        for (int i = 0; i < 10; i++) {
            if (!deck1.deal().toString().equals(deck2.deal().toString())) {
                orderIsDifferent = true;
                break;
            }
        }
        assertTrue(orderIsDifferent, "Shuffling should change the order of cards.");
    }
}