package ru.nsu.tokarev.localization;

public interface GameMessages {
    String welcome();
    String round(int roundNumber);
    String dealerDealsCards();
    String yourCards();
    String dealerCards();
    String hiddenCard();
    String blackjackWin();
    String dealerBlackjack();
    String yourTurn();
    String hitOrStand();
    String youDrewCard();
    String bust();
    String dealerTurn();
    String dealerRevealsCard();
    String dealerDrawsCard();
    String dealerBust();
    String youWin();
    String youLose();
    String tie();
    String playAgain();
    String thanksForPlaying();
    String score(int playerScore, int dealerScore);
    String scoreInYourFavor(int playerScore, int dealerScore);
    String scoreInDealerFavor(int playerScore, int dealerScore);
    String scoreTie(int playerScore, int dealerScore);
}