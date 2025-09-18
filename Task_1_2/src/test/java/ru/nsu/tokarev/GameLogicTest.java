package ru.nsu.tokarev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


class GameLogicTest {

    private Game game;
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // Вспомогательный метод для симуляции ввода пользователя
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    // Вспомогательный метод для замены приватного поля Deck
    private void injectDeck(Game gameInstance, Deck deck) throws Exception {
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(gameInstance, deck);
    }

    @Test
    void testPlayerGetsBlackjack() throws Exception {
        provideInput("0\n");

        List<Card> predefinedCards = List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.HEARTS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.KING),
                new Card(Suit.DIAMONDS, Rank.SIX)
        );
        final Queue<Card> cardQueue = new LinkedList<>(predefinedCards);

        Deck fakeDeck = new Deck() {
            @Override
            public void shuffle() {
                // Ничего не делаем, чтобы сохранить порядок карт
            }

            @Override
            public Card deal() {
                return cardQueue.poll();
            }
        };

        game = new Game(new Scanner(System.in));
        injectDeck(game, fakeDeck);

        game.start();

        String output = outContent.toString();
        assertTrue(output.contains("Блэкджек! Вы выиграли раунд!"),
                "Игра должна определить блэкджек у игрока.");
    }

    @Test
    void testPlayerHitsAndBusts() throws Exception {
        // Arrange
        provideInput("1\n0\n");

        List<Card> predefinedCards = List.of(
                new Card(Suit.SPADES, Rank.TEN),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.HEARTS, Rank.FIVE)
        );
        final Queue<Card> cardQueue = new LinkedList<>(predefinedCards);

        Deck fakeDeck = new Deck() {
            @Override
            public void shuffle() {

            }

            @Override
            public Card deal() {
                return cardQueue.poll();
            }
        };

        game = new Game(new Scanner(System.in));
        injectDeck(game, fakeDeck);

        game.start();

        String output = outContent.toString();
        assertTrue(output.contains("Перебор! Вы проиграли раунд."),
                "Игра должна зафиксировать перебор у игрока.");
    }
}