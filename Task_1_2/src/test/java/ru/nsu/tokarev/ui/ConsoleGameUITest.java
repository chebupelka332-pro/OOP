package ru.nsu.tokarev.ui;

import ru.nsu.tokarev.localization.EnglishGameMessages;
import ru.nsu.tokarev.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleGameUITest {
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private ConsoleGameUI ui;
    private EnglishGameMessages messages;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        messages = new EnglishGameMessages();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testShowMessage() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        ui.showMessage("Test message");
        assertEquals("Test message" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testShowHandsHidden() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        Hand playerHand = new Hand();
        playerHand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        playerHand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        Hand dealerHand = new Hand();
        dealerHand.addCard(new Card(Suit.DIAMONDS, Rank.QUEEN));
        dealerHand.addCard(new Card(Suit.CLUBS, Rank.JACK));
        
        ui.showHands(playerHand, dealerHand, false);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Your cards:"));
        assertTrue(output.contains("Dealer cards:"));
        assertTrue(output.contains("<" + messages.hiddenCard() + ">"));
        assertFalse(output.contains("Jack of Clubs"));
    }

    @Test
    void testShowHandsRevealed() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        Hand playerHand = new Hand();
        playerHand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        
        Hand dealerHand = new Hand();
        dealerHand.addCard(new Card(Suit.DIAMONDS, Rank.QUEEN));
        dealerHand.addCard(new Card(Suit.CLUBS, Rank.JACK));
        
        ui.showHands(playerHand, dealerHand, true);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Your cards:"));
        assertTrue(output.contains("Dealer cards:"));
        assertTrue(output.contains("Jack of Clubs"));
        assertFalse(output.contains("<" + messages.hiddenCard() + ">"));
    }

    @Test
    void testShowHandsEmptyDealer() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        Hand playerHand = new Hand();
        playerHand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        
        Hand emptyDealerHand = new Hand();
        
        ui.showHands(playerHand, emptyDealerHand, false);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Your cards:"));
        assertTrue(output.contains("Dealer cards: []"));
    }

    @Test
    void testGetUserChoice() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        assertEquals(1, ui.getUserChoice());
    }

    @Test
    void testGetUserChoiceMultiple() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n0\n5".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        assertEquals(1, ui.getUserChoice());
        assertEquals(0, ui.getUserChoice());
        assertEquals(5, ui.getUserChoice());
    }

    @Test
    void testShowScoreFavoringPlayer() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        ui.showScore(3, 2);
        assertTrue(outputStream.toString().contains("Score 3:2 in your favor"));
    }

    @Test
    void testShowScoreFavoringDealer() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        ui.showScore(1, 3);
        assertTrue(outputStream.toString().contains("Score 1:3 in dealer's favor"));
    }

    @Test
    void testShowScoreTie() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);
        
        ui.showScore(2, 2);
        assertTrue(outputStream.toString().contains("Score 2:2 - tie"));
    }

    /*@Test
    void testClose() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        ui = new ConsoleGameUI(scanner, messages);

        ui.close();

        assertTrue(scanner.hasNextLine() || !scanner.hasNextLine());
    }*/
}