package ru.nsu.tokarev.ui;

import ru.nsu.tokarev.localization.GameMessages;
import ru.nsu.tokarev.model.Hand;

import java.util.Scanner;

public class ConsoleGameUI implements GameUI {
    private final Scanner scanner;
    private final GameMessages messages;

    public ConsoleGameUI(Scanner scanner, GameMessages messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void showHands(Hand playerHand, Hand dealerHand, boolean revealDealerCard) {
        System.out.println(messages.yourCards() + playerHand + " => " + playerHand.getValue());
        if (revealDealerCard) {
            System.out.println(messages.dealerCards() + dealerHand + " => " + dealerHand.getValue());
        } else {
            String hiddenHandDisplay = dealerHand.isEmpty() ? "[]" : 
                "[" + dealerHand.get(0).toString() + ", <" + messages.hiddenCard() + ">]";
            System.out.println(messages.dealerCards() + hiddenHandDisplay);
        }
    }

    @Override
    public void showScore(int playerScore, int dealerScore) {
        if (playerScore > dealerScore) {
            System.out.println(messages.scoreInYourFavor(playerScore, dealerScore));
        } else if (playerScore < dealerScore) {
            System.out.println(messages.scoreInDealerFavor(playerScore, dealerScore));
        } else {
            System.out.println(messages.scoreTie(playerScore, dealerScore));
        }
    }

    @Override
    public int getUserChoice() {
        return scanner.nextInt();
    }
}