package ru.nsu.tokarev.game;

import ru.nsu.tokarev.localization.EnglishGameMessages;
import ru.nsu.tokarev.model.Hand;
import ru.nsu.tokarev.ui.GameUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private TestGameUI testUI;
    private EnglishGameMessages messages;
    private Game game;

    private static class TestGameUI implements GameUI {
        private final List<String> displayedMessages = new ArrayList<>();
        private final List<Integer> userInputs = new ArrayList<>();
        private final List<HandDisplayRecord> handDisplays = new ArrayList<>();
        private final List<ScoreDisplayRecord> scoreDisplays = new ArrayList<>();
        private int inputIndex = 0;
        private boolean closed = false;

        public void addUserInput(int input) {
            userInputs.add(input);
        }

        public void addUserInputs(int... inputs) {
            for (int input : inputs) {
                userInputs.add(input);
            }
        }

        @Override
        public void showMessage(String message) {
            displayedMessages.add(message);
        }

        @Override
        public void showHands(Hand playerHand, Hand dealerHand, boolean revealDealerCard) {
            handDisplays.add(new HandDisplayRecord(
                playerHand.toString(), 
                dealerHand.toString(), 
                revealDealerCard
            ));
        }

        @Override
        public void showScore(int playerScore, int dealerScore) {
            scoreDisplays.add(new ScoreDisplayRecord(playerScore, dealerScore));
        }

        @Override
        public int getUserChoice() {
            if (inputIndex >= userInputs.size()) {
                throw new IllegalStateException("Not enough user inputs provided for test");
            }
            return userInputs.get(inputIndex++);
        }

        public List<String> getDisplayedMessages() {
            return displayedMessages;
        }

        public List<HandDisplayRecord> getHandDisplays() {
            return handDisplays;
        }

        public List<ScoreDisplayRecord> getScoreDisplays() {
            return scoreDisplays;
        }

        public boolean isClosed() {
            return closed;
        }

        public boolean hasMessage(String message) { 
            return displayedMessages.contains(message);
        }
    }

    private static class HandDisplayRecord {
        final String playerHand;
        final String dealerHand;
        final boolean revealDealerCard;

        HandDisplayRecord(String playerHand, String dealerHand, boolean revealDealerCard) {
            this.playerHand = playerHand;
            this.dealerHand = dealerHand;
            this.revealDealerCard = revealDealerCard;
        }
    }

    private static class ScoreDisplayRecord {
        final int playerScore;
        final int dealerScore;

        ScoreDisplayRecord(int playerScore, int dealerScore) {
            this.playerScore = playerScore;
            this.dealerScore = dealerScore;
        }
    }

    @BeforeEach
    void setUp() {
        testUI = new TestGameUI();
        messages = new EnglishGameMessages();
        game = new Game(testUI, messages);
    }

    @Test
    void testGameInitialization() {
        assertEquals(0, game.getPlayerScore());
        assertEquals(0, game.getDealerScore());
        assertEquals(1, game.getCurrentRound());
    }

    @Test
    void testStartGameExitsOnUserChoice() {
        testUI.addUserInputs(0, 0);
        
        game.start();
        
        assertTrue(testUI.hasMessage("Welcome to Blackjack!"));
        assertTrue(testUI.hasMessage(messages.thanksForPlaying()));
        assertFalse(testUI.isClosed());
    }

    @Test
    void testStartGamePlaysMultipleRounds() {
        // First round: player stands (0), then play another round (1)
        // Second round: player stands (0), then don't play another round (0)
        testUI.addUserInputs(0, 1, 0, 0);
        
        game.start();
        
        assertTrue(testUI.hasMessage("Welcome to Blackjack!"));
        assertTrue(testUI.hasMessage(messages.thanksForPlaying()));

        long playAgainCount = testUI.getDisplayedMessages().stream()
            .filter(msg -> msg.equals(messages.playAgain()))
            .count();
        assertEquals(2, playAgainCount);

        assertEquals(3, game.getCurrentRound());
    }

    @Test
    void testPlayerStanding() {
        // Player immediately stands
        testUI.addUserInputs(0, 0);
        
        game.start();
        
        assertTrue(testUI.hasMessage(messages.yourTurn()));
        assertTrue(testUI.hasMessage(messages.hitOrStand()));

        assertTrue(game.getPlayerScore() >= 0);
        assertTrue(game.getDealerScore() >= 0);
    }

    @Test
    void testPlayerHitting() {
        // Player hits once (1) then stands (0), then doesn't play again (0)  
        testUI.addUserInputs(1, 0, 0);
        
        game.start();
        
        assertTrue(testUI.hasMessage(messages.yourTurn()));

        assertFalse(testUI.getHandDisplays().isEmpty());

        assertFalse(testUI.getScoreDisplays().isEmpty());
    }

    @Test
    void testScoreTracking() {
        int initialPlayerScore = game.getPlayerScore();
        int initialDealerScore = game.getDealerScore();
        
        assertEquals(0, initialPlayerScore);
        assertEquals(0, initialDealerScore);

        testUI.addUserInputs(0, 0);
        game.start();
        
        assertTrue(game.getPlayerScore() >= 0);
        assertTrue(game.getDealerScore() >= 0);
        assertTrue(game.getPlayerScore() + game.getDealerScore() >= 0);
    }

    @Test
    void testRoundIncrement() {
        assertEquals(1, game.getCurrentRound());
        
        // Play two rounds
        testUI.addUserInputs(0, 1, 0, 0);
        game.start();
        
        assertEquals(3, game.getCurrentRound());
    }

    @Test
    void testBlackjackLogic() {
        // Test that dealer blackjack is only revealed after player's turn completes
        // or when player also has blackjack
        testUI.addUserInputs(0, 0); // stand, don't play again
        
        game.start();
        
        // Verify that game completes without exceptions
        assertTrue(testUI.getDisplayedMessages().contains("Welcome to Blackjack!"));
        assertTrue(testUI.getDisplayedMessages().contains(messages.thanksForPlaying()));
        
        // Check that dealer cards are properly hidden initially
        List<HandDisplayRecord> handDisplays = testUI.getHandDisplays();
        if (!handDisplays.isEmpty()) {
            // First display should have hidden dealer card (unless player has blackjack)
            boolean hasHiddenDisplay = handDisplays.stream()
                .anyMatch(display -> !display.revealDealerCard);
            boolean hasRevealedDisplay = handDisplays.stream()
                .anyMatch(display -> display.revealDealerCard);
            
            // Should have at least one display where dealer card is hidden
            // (unless player gets blackjack immediately)
            assertTrue(hasHiddenDisplay || testUI.hasMessage(messages.blackjackWin()) || testUI.hasMessage(messages.tie()));
        }
    }

    @Test
    void testGameFlowIntegrity() {
        // Verify that the game follows proper sequence
        testUI.addUserInputs(1, 0, 0); // hit once, then stand, don't play again
        
        game.start();
        
        List<String> messages = testUI.getDisplayedMessages();
        
        // Should start with welcome
        assertTrue(messages.contains("Welcome to Blackjack!"));
        
        // Should have your turn message (unless immediate blackjack)
        boolean hasYourTurn = messages.contains(this.messages.yourTurn());
        boolean hasBlackjack = messages.contains(this.messages.blackjackWin());
        
        assertTrue(hasYourTurn || hasBlackjack, "Should either have player turn or blackjack");
        
        // Should end with thanks
        assertTrue(messages.contains(this.messages.thanksForPlaying()));
    }
}