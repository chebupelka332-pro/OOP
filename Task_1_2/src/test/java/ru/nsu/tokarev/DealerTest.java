package ru.nsu.tokarev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    private Dealer dealer;

    @BeforeEach
    void setUp() {
        dealer = new Dealer();
    }

    @Test
    void testGetDealerFirstCardWhenHandIsEmpty() {
        String expected = "[]";
        String actual = dealer.getDealerFirstCard();
        assertEquals(expected, actual, "Метод должен возвращать '[]', если рука пуста.");
    }

    @Test
    void testGetDealerFirstCardWithCardsInHand() {
        Card firstCard = new Card(Suit.SPADES, Rank.KING); // Король Пики (10)
        Card secondCard = new Card(Suit.HEARTS, Rank.SEVEN); // Семерка Червы (7)
        dealer.addCard(firstCard);
        dealer.addCard(secondCard);
        
        String expected = "[" + firstCard.toString() + ", <закрытая карта>]";

        String actual = dealer.getDealerFirstCard();

        assertEquals(expected, actual, "Метод должен показывать только первую карту дилера.");
    }
}