package ru.nsu.tokarev.ui;

import ru.nsu.tokarev.model.Card;
import ru.nsu.tokarev.model.Hand;

public interface GameUI {
    void showMessage(String message);
    void showHands(Hand playerHand, Hand dealerHand, boolean revealDealerCard);
    void showScore(int playerScore, int dealerScore);
    int getUserChoice();
}