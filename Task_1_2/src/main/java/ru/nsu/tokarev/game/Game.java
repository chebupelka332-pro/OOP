package ru.nsu.tokarev.game;

import ru.nsu.tokarev.localization.GameMessages;
import ru.nsu.tokarev.model.*;
import ru.nsu.tokarev.ui.GameUI;

public class Game {
    private final Deck deck;
    private final Participant player;
    private final Participant dealer;
    private final GameUI ui;
    private final GameMessages messages;
    private int playerScore = 0;
    private int dealerScore = 0;
    private int round = 1;

    public Game(GameUI ui, GameMessages messages) {
        this.deck = new Deck();
        this.player = new Participant();
        this.dealer = new Participant();
        this.ui = ui;
        this.messages = messages;
    }

    public void start() {
        ui.showMessage(messages.welcome());

        do {
            playRound();
            ui.showMessage(messages.playAgain());
        } while (ui.getUserChoice() == 1);

        ui.showMessage(messages.thanksForPlaying());
    }

    private void playRound() {
        ui.showMessage(messages.round(round++));

        player.resetHand();
        dealer.resetHand();

        deck.reset();
        deck.shuffle();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        ui.showMessage(messages.dealerDealsCards());
        ui.showHands(player.getHand(), dealer.getHand(), false);

        if (player.getHand().isBlackjack()) {
            ui.showMessage(messages.dealerRevealsCard());
            ui.showHands(player.getHand(), dealer.getHand(), true);
            
            if (dealer.getHand().isBlackjack()) {
                ui.showMessage(messages.tie());
            } else {
                ui.showMessage(messages.blackjackWin());
                playerScore++;
            }
            ui.showScore(playerScore, dealerScore);
            return;
        }

        playerTurn();

        if (!player.getHand().isBusted()) {
            dealerTurn();
        }

        determineWinner();
        ui.showScore(playerScore, dealerScore);
    }

    private void playerTurn() {
        ui.showMessage(messages.yourTurn());
        while (true) {
            ui.showMessage(messages.hitOrStand());
            int choice = ui.getUserChoice();
            if (choice == 1) {
                Card newCard = deck.deal();
                player.addCard(newCard);
                ui.showMessage(messages.youDrewCard() + newCard);
                ui.showHands(player.getHand(), dealer.getHand(), false);
                if (player.getHand().isBusted()) {
                    ui.showMessage(messages.bust());
                    dealerScore++;
                    break;
                }
            } else if (choice == 0) {
                break;
            }
        }
    }

    private void dealerTurn() {
        ui.showMessage(messages.dealerTurn());
        ui.showMessage(messages.dealerRevealsCard());
        ui.showHands(player.getHand(), dealer.getHand(), true);

        while (dealer.getHandValue() < 17) {
            Card newCard = deck.deal();
            dealer.addCard(newCard);
            ui.showMessage(messages.dealerDrawsCard() + newCard);
            ui.showHands(player.getHand(), dealer.getHand(), true);
        }
    }

    private void determineWinner() {
        if (player.getHand().isBusted()) {
            return;
        }

        if (dealer.getHand().isBlackjack()) {
            ui.showMessage(messages.dealerBlackjack());
            dealerScore++;
            return;
        }
        
        if (dealer.getHand().isBusted()) {
            ui.showMessage(messages.dealerBust());
            playerScore++;
        } else if (player.getHandValue() > dealer.getHandValue()) {
            ui.showMessage(messages.youWin());
            playerScore++;
        } else if (player.getHandValue() < dealer.getHandValue()) {
            ui.showMessage(messages.youLose());
            dealerScore++;
        } else {
            ui.showMessage(messages.tie());
        }
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getDealerScore() {
        return dealerScore;
    }

    public int getCurrentRound() {
        return round;
    }
}